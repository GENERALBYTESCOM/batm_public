/*************************************************************************************
 * Copyright (C) 2015-2016 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/

package com.generalbytes.batm.server.extensions.extra.watchlists.ofac;

import com.generalbytes.batm.server.extensions.extra.watchlists.IParsedSanctions;
import com.generalbytes.batm.server.extensions.extra.watchlists.Match;
import com.generalbytes.batm.server.extensions.extra.watchlists.ofac.tags.DistinctPartySchemaType;
import com.generalbytes.batm.server.extensions.extra.watchlists.ofac.tags.DocumentedNameSchemaType;
import com.generalbytes.batm.server.extensions.extra.watchlists.ofac.tags.FeatureSchemaType;
import com.generalbytes.batm.server.extensions.extra.watchlists.ofac.tags.IdentitySchemaType;
import com.generalbytes.batm.server.extensions.extra.watchlists.ofac.tags.ReferenceValueSetsSchemaType;
import com.generalbytes.batm.server.extensions.extra.watchlists.ofac.tags.Sanctions;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/*

Having issues to understand what each element in XML means? Don't worry, I don't understand it too.

<AliasTypeValues>
    <AliasType ID="1400">A.K.A.</AliasType>
    <AliasType ID="1401">F.K.A.</AliasType>
    <AliasType ID="1402">N.K.A.</AliasType>
    <AliasType ID="1403">Name</AliasType>
</AliasTypeValues>

<NamePartTypeValues>
    <NamePartType ID="1520">Last Name</NamePartType>
    <NamePartType ID="1521">First Name</NamePartType>
    <NamePartType ID="1522">Middle Name</NamePartType>
    <NamePartType ID="1523">Maiden Name</NamePartType>
    <NamePartType ID="1524">Aircraft Name</NamePartType>
    <NamePartType ID="1525">Entity Name</NamePartType>
    <NamePartType ID="1526">Vessel Name</NamePartType>
    <NamePartType ID="1528">Nickname</NamePartType>
    <NamePartType ID="91708">Patronymic</NamePartType>
    <NamePartType ID="91709">Matronymic</NamePartType>
</NamePartTypeValues>


 */
public class ParsedSanctions implements IParsedSanctions {
    public static final String TYPE_LAST_NAME = "1520";
    public static final String TYPE_FIRST_NAME = "1521";

    public static final String TYPE_DIGITAL_CURRENCY_ADDRESS = "Digital Currency Address";

    private final Map<String, List<ParsedNamePart>> nameParts;
    private final Map<String, String> partyIndexes;
    private final Set<String> blacklistedCryptoAddresses;

    private ParsedSanctions(Map<String, List<ParsedNamePart>> nameParts, Map<String, String> partyIndexes, Set<String> blacklistedCryptoAddresses) {
        this.nameParts = nameParts;
        this.partyIndexes = partyIndexes;
        this.blacklistedCryptoAddresses = blacklistedCryptoAddresses;
    }

    public static ParsedSanctions parse(Sanctions sanctions) {
        List<ParsedNamePart> names = new ArrayList<>();
        Map<String, String> partyIndexes = new HashMap<>();
        Set<String> blacklistedCryptoAddresses = new HashSet<>();

        final ReferenceValueSetsSchemaType referenceValueSets = sanctions.getReferenceValueSets();
        final ReferenceValueSetsSchemaType.FeatureTypeValues featureTypeValues = referenceValueSets.getFeatureTypeValues();

        Set<BigInteger> featureTypeIDs = new HashSet<>();
        for (ReferenceValueSetsSchemaType.FeatureTypeValues.FeatureType featureType : featureTypeValues.getFeatureType()) {
            if (featureType.getValue() != null && featureType.getValue().startsWith(TYPE_DIGITAL_CURRENCY_ADDRESS)) {
                featureTypeIDs.add(featureType.getID());
            }
        }

        final Sanctions.DistinctParties distinctParties = sanctions.getDistinctParties();
        final List<DistinctPartySchemaType> distinctParty = distinctParties.getDistinctParty();

        for (int i = 0; i < distinctParty.size(); i++) {
            DistinctPartySchemaType dp = distinctParty.get(i);
            final String profileId = dp.getFixedRef();
            partyIndexes.put(profileId, i + "");

            final List<DistinctPartySchemaType.Profile> profile = dp.getProfile();
            for (DistinctPartySchemaType.Profile p : profile) {
                final List<IdentitySchemaType> identity = p.getIdentity();
                for (IdentitySchemaType idt : identity) {
                    final List<IdentitySchemaType.Alias> alias = idt.getAlias();
                    for (IdentitySchemaType.Alias as : alias) {
                        final List<DocumentedNameSchemaType> documentedName = as.getDocumentedName();
                        for (DocumentedNameSchemaType dnt : documentedName) {
                            final List<DocumentedNameSchemaType.DocumentedNamePart> dnp = dnt.getDocumentedNamePart();
                            for (DocumentedNameSchemaType.DocumentedNamePart part : dnp) {
                                final DocumentedNameSchemaType.DocumentedNamePart.NamePartValue namePartValue = part.getNamePartValue();
                                final String value = namePartValue.getValue();
                                final BigInteger namePartGroupID = namePartValue.getNamePartGroupID();
                                final String aliasType = as.getAliasTypeID() + "";

                                String nameType = findNamePartTypeFromNameGroup(idt.getNamePartGroups().getMasterNamePartGroup(), namePartGroupID);
                                names.add(new ParsedNamePart(profileId, namePartGroupID + "", nameType, aliasType, value));
                            }
                        }
                    }
                }

                for (FeatureSchemaType featureSchemaType : p.getFeature()) {
                    if (featureTypeIDs.contains(featureSchemaType.getFeatureTypeID())) {
                        for (FeatureSchemaType.FeatureVersion featureVersion : featureSchemaType.getFeatureVersion()) {
                            for (FeatureSchemaType.FeatureVersion.VersionDetail versionDetail : featureVersion.getVersionDetail()) {
                                blacklistedCryptoAddresses.add(versionDetail.getValue());
                            }
                        }
                    }
                }
            }
        }

        Map<String, List<ParsedNamePart>> result = new HashMap<>();
        for (ParsedNamePart namePart : names) {
            List<ParsedNamePart> parsedNameParts = result.computeIfAbsent(namePart.getNameType(), k -> new ArrayList<>());
            parsedNameParts.add(namePart);
        }

        return new ParsedSanctions(result, partyIndexes, blacklistedCryptoAddresses);
    }

    private static String findNamePartTypeFromNameGroup(List<IdentitySchemaType.NamePartGroups.MasterNamePartGroup> masterNamePartGroup, BigInteger namePartGroupID) {
        for (IdentitySchemaType.NamePartGroups.MasterNamePartGroup group : masterNamePartGroup) {
            final List<IdentitySchemaType.NamePartGroups.MasterNamePartGroup.NamePartGroup> namePartGroup = group.getNamePartGroup();
            for (IdentitySchemaType.NamePartGroups.MasterNamePartGroup.NamePartGroup partGroup : namePartGroup) {
                if (namePartGroupID.compareTo(partGroup.getID()) == 0) {
                    return partGroup.getNamePartTypeID() + "";
                }
            }
        }
        return null;

    }

    @Override
    public Set<Match> search(String firstName, String lastName) {
        String trimmedLastName = getTrimmedNonNullString(firstName);
        String trimmedFirstName = getTrimmedNonNullString(firstName);

        Set<Match> matchedParties = new HashSet<>();
        Set<String> candidateParties = new HashSet<>();

        // begin with last name
        List<ParsedNamePart> parsedNameParts = nameParts.get(TYPE_LAST_NAME);
        if (parsedNameParts != null) {
            for (ParsedNamePart namePart : parsedNameParts) {
                if (namePart.getValue().trim().equalsIgnoreCase(trimmedLastName)) {
                    candidateParties.add(namePart.getPartyId());
                }
            }
        }
        if (trimmedFirstName.isEmpty()) { // only last name filled in -> w
            for (String partyId : candidateParties) {
                matchedParties.add(new Match(partyId, 100));
            }
            return matchedParties;
        }

        // continue with first name
        parsedNameParts = nameParts.get(TYPE_FIRST_NAME);
        if (parsedNameParts != null) {
            for (ParsedNamePart namePart : parsedNameParts) {
                if (
                    candidateParties.contains(namePart.getPartyId())
                    && namePart.getValue().trim().equalsIgnoreCase(trimmedFirstName)
                ) { //ok seems like we have a winner
                    matchedParties.add(new Match(namePart.getPartyId(), 100));
                }
            }
        }

        if (matchedParties.isEmpty()) {
            //both first name and last name didn't match
            //so lets report at least lastname matches with 50% score/confidence
            for (String partyId : candidateParties) {
                matchedParties.add(new Match(partyId, 50));
            }
        }
        return matchedParties;
    }

    @Override
    public String getPartyIndexByPartyId(String partyId) {
        if (partyIndexes != null) {
            return partyIndexes.get(partyId);
        }
        return null;
    }

    public Set<String> getBlacklistedCryptoAddresses() {
        return blacklistedCryptoAddresses;
    }
}
