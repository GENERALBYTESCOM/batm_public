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

import com.generalbytes.batm.server.extensions.extra.watchlists.ofac.tags.*;

import java.math.BigInteger;
import java.util.*;


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
public class ParsedSanctions {
    public static final String TYPE_LAST_NAME = "1520";
    public static final String TYPE_FIRST_NAME = "1521";

    private Map<String,List<ParsedNamePart>> nameParts = new HashMap<String, List<ParsedNamePart>>();
    private Map<String,String> partyIndexes = new HashMap<String, String>();

    public ParsedSanctions(Map<String, List<ParsedNamePart>> nameParts, Map<String, String> partyIndexes) {
        this.nameParts = nameParts;
        this.partyIndexes = partyIndexes;
    }

    public static ParsedSanctions parse(Sanctions sanctions) {
        List<ParsedNamePart> names = new ArrayList<ParsedNamePart>();
        Map<String,String> partyIndexes = new HashMap<String, String>();
        final Sanctions.DistinctParties distinctParties = sanctions.getDistinctParties();
        final List<DistinctPartySchemaType> distinctParty = distinctParties.getDistinctParty();

        for (int i = 0; i < distinctParty.size(); i++) {
            DistinctPartySchemaType dp = distinctParty.get(i);
            final String profileId = dp.getFixedRef();
            partyIndexes.put(profileId,i +"");

            final List<DistinctPartySchemaType.Profile> profile = dp.getProfile();
            for (int j = 0; j < profile.size(); j++) {
                DistinctPartySchemaType.Profile p = profile.get(j);
                final List<IdentitySchemaType> identity = p.getIdentity();
                for (int k = 0; k < identity.size(); k++) {
                    IdentitySchemaType idt = identity.get(k);
                    final List<IdentitySchemaType.Alias> alias = idt.getAlias();
                    for (int l = 0; l < alias.size(); l++) {
                        IdentitySchemaType.Alias as = alias.get(l);
                        final List<DocumentedNameSchemaType> documentedName = as.getDocumentedName();
                        for (int m = 0; m < documentedName.size(); m++) {
                            DocumentedNameSchemaType dnt =  documentedName.get(m);
                            final List<DocumentedNameSchemaType.DocumentedNamePart> dnp = dnt.getDocumentedNamePart();
                            for (int n = 0; n < dnp.size(); n++) {
                                DocumentedNameSchemaType.DocumentedNamePart part = dnp.get(n);
                                final DocumentedNameSchemaType.DocumentedNamePart.NamePartValue namePartValue = part.getNamePartValue();
                                final String value = namePartValue.getValue();
                                final BigInteger namePartGroupID = namePartValue.getNamePartGroupID();
                                final String aliasType = as.getAliasTypeID() +"";

                                String nameType = findNamePartTypeFromNameGroup(idt.getNamePartGroups().getMasterNamePartGroup(), namePartGroupID);
                                names.add(new ParsedNamePart(profileId, namePartGroupID + "", nameType, aliasType, value));
                            }
                        }
                    }
                }
            }
        }


        Map<String,List<ParsedNamePart>> result = new HashMap<String, List<ParsedNamePart>>();
        for (int i = 0; i < names.size(); i++) {
            ParsedNamePart namePart = names.get(i);
            List<ParsedNamePart> parsedNameParts = result.get(namePart.getNameType());
            if (parsedNameParts == null) {
                parsedNameParts = new ArrayList<ParsedNamePart>();
                result.put(namePart.getNameType(), parsedNameParts);
            }
            parsedNameParts.add(namePart);
        }

        return new ParsedSanctions(result,partyIndexes);


    }

    private static String findNamePartTypeFromNameGroup(List<IdentitySchemaType.NamePartGroups.MasterNamePartGroup> masterNamePartGroup, BigInteger namePartGroupID) {
        for (int i = 0; i < masterNamePartGroup.size(); i++) {
            IdentitySchemaType.NamePartGroups.MasterNamePartGroup group = masterNamePartGroup.get(i);
            final List<IdentitySchemaType.NamePartGroups.MasterNamePartGroup.NamePartGroup> namePartGroup = group.getNamePartGroup();
            for (int j = 0; j < namePartGroup.size(); j++) {
                IdentitySchemaType.NamePartGroups.MasterNamePartGroup.NamePartGroup partGroup = namePartGroup.get(j);
                if (namePartGroupID.compareTo(partGroup.getID()) == 0) {
                    return partGroup.getNamePartTypeID() +"";
                }
            }
        }
        return null;

    }

    /**
     * Returns list of matched party ids based on first and last name
     * @param firstName
     * @param lastName
     * @return
     */
    public Set<Match> search(String firstName, String lastName) {
        lastName = lastName.trim();
        firstName = firstName.trim();

        Set<String> candidateParties = new HashSet<String>();
        Set<Match> matchedParties = new HashSet<Match>();


        if (firstName.isEmpty()) {
            //search just against lastnames

            List<ParsedNamePart> parsedNameParts = nameParts.get(TYPE_LAST_NAME);
            if (parsedNameParts != null) {
                for (int i = 0; i < parsedNameParts.size(); i++) {
                    ParsedNamePart namePart = parsedNameParts.get(i);
                    if (namePart.getValue().trim().equalsIgnoreCase(lastName)) {
                        matchedParties.add(new Match(namePart.getPartyId(),100));
                    }
                }
            }
        }else {
            //search against lastname ans firstname
            List<ParsedNamePart> parsedNameParts = nameParts.get(TYPE_LAST_NAME);
            if (parsedNameParts != null) {
                for (int i = 0; i < parsedNameParts.size(); i++) {
                    ParsedNamePart namePart = parsedNameParts.get(i);
                    if (namePart.getValue().trim().equalsIgnoreCase(lastName)) {
                        candidateParties.add(namePart.getPartyId());
                    }
                }
            }


            parsedNameParts = nameParts.get(TYPE_FIRST_NAME);
            if (parsedNameParts != null) {
                for (int i = 0; i < parsedNameParts.size(); i++) {
                    ParsedNamePart namePart = parsedNameParts.get(i);
                    if (candidateParties.contains(namePart.getPartyId())) {
                        if (namePart.getValue().trim().equalsIgnoreCase(firstName)) {
                            //ok seems like we have a winner
                            matchedParties.add(new Match(namePart.getPartyId(),100));
                        }
                    }
                }
            }

            if (matchedParties.size() == 0) {
                //both first name and last name didn't match
                //so lets report at least lastname matches with 50% score/confidence
                for (String candidateParty : candidateParties) {
                    matchedParties.add(new Match(candidateParty,50));
                }
            }
        }
        return matchedParties;
    }

    public String getPartyIndexByPartyId(String partyId) {
        if (partyIndexes !=null) {
            return partyIndexes.get(partyId);
        }
        return null;
    }


}
