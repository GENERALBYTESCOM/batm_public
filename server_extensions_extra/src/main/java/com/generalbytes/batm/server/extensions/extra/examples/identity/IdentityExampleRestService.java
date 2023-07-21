/*************************************************************************************
 * Copyright (C) 2014-2020 GENERAL BYTES s.r.o. All rights reserved.
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
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.examples.identity;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IIdentity;
import com.generalbytes.batm.server.extensions.IIdentityNote;
import com.generalbytes.batm.server.extensions.IIdentityPiece;
import com.generalbytes.batm.server.extensions.IIdentityBase;
import com.generalbytes.batm.server.extensions.ILimit;
import com.generalbytes.batm.server.extensions.ILimitExtended;
import com.generalbytes.batm.server.extensions.IVerificationInfo;
import com.generalbytes.batm.server.extensions.PhoneNumberQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Path("/")
public class IdentityExampleRestService {

    private static final Logger log = LoggerFactory.getLogger(IdentityExampleRestService.class);

    // Uncomment this example in ***batm-extensions.xml*** and call it for example with:
    // curl -k -XPOST https://localhost:7743/extensions/identity-example/register -d "terminalSerialNumber=BT102239&externalId=EXTID0001&fiatCurrency=USD&limit=1000000&discount=100&phoneNumber=+12065550100&firstName=Chuck&lastName=Norris&emailAddress=chucknorrisfans@hotmail.com&idCardNumber=123456&contactZIP=77868&contactCountry=United States&contactProvince=TX&contactCity=Navasota&contactAddress=4360 Lone Wolf Ranch Road&dateOfBirth=12/31/1999"
    @POST
    @Path("/register2")
    @Produces(MediaType.APPLICATION_JSON)
    public String register(@FormParam("fiatCurrency") String fiatCurrency, @FormParam("externalId") String externalId,
                           @FormParam("limit") BigDecimal limit, @FormParam("discount") BigDecimal discount,
                           @FormParam("terminalSerialNumber") String terminalSerialNumber, @FormParam("note") String note,
                           @FormParam("phoneNumber") String phoneNumber, @FormParam("firstName") String firstName,
                           @FormParam("lastName") String lastName, @FormParam("emailAddress") String emailAddress,
                           @FormParam("idCardNumber") String idCardNumber, @FormParam("documentValidToYYYYMMDD") String documentValidToYYYYMMDD,
                           @FormParam("contactZIP") String contactZIP,
                           @FormParam("contactCountry") String contactCountry, @FormParam("contactCountryIso2") String contactCountryIso2,
                           @FormParam("contactProvince") String contactProvince,
                           @FormParam("contactCity") String contactCity, @FormParam("contactAddress") String contactAddress,
                           @FormParam("dateOfBirth") String dateOfBirth, @FormParam("occupation") String occupation,
                           @FormParam("ssn") String ssn) throws ParseException {

        IExtensionContext ctx = IdentityExampleExtension.getExtensionContext();
        List<ILimit> limits = Arrays.asList(new LimitExample(fiatCurrency, limit));

        PhoneNumberQueryResult phoneNumberQueryResult = ctx.queryPhoneNumber(phoneNumber, terminalSerialNumber);
        if (phoneNumberQueryResult.isQuerySuccessful()) {
            if (phoneNumberQueryResult.isLineTypeBlocked()) {
                return "PHONE BLOCKED";
            }
            System.out.println("Phone type: " + phoneNumberQueryResult.getPhoneLineType().getPhoneTypeCode().name());
        }

        Date dateOfBirthParsed = new SimpleDateFormat("MM/dd/yyyy", Locale.US).parse(dateOfBirth);
        int state = IIdentity.STATE_NOT_REGISTERED;
        Date now = new Date();

        // read the image data from the request or a file
        byte[] exampleJpeg = Base64.getDecoder().decode("/9j/2wBDACQZGyAbFyQgHiApJyQrNls7NjIyNm9PVEJbhHSKiIF0f32Ro9GxkZrFnX1/tve4xdje6uzqja////7j/9Hl6uH/2wBDAScpKTYwNms7O2vhln+W4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eH/wgARCAAFAAUDAREAAhEBAxEB/8QAFAABAAAAAAAAAAAAAAAAAAAAAf/EABQBAQAAAAAAAAAAAAAAAAAAAAD/2gAMAwEAAhADEAAAAQ//xAAVEAEBAAAAAAAAAAAAAAAAAAACAf/aAAgBAQABBQImq//EABQRAQAAAAAAAAAAAAAAAAAAAAD/2gAIAQMBAT8Bf//EABQRAQAAAAAAAAAAAAAAAAAAAAD/2gAIAQIBAT8Bf//EABgQAAMBAQAAAAAAAAAAAAAAAAECEQCh/9oACAEBAAY/AmAaTu//xAAWEAEBAQAAAAAAAAAAAAAAAAABABH/2gAIAQEAAT8hYg3ik//aAAwDAQACAAMAAAAQH//EABQRAQAAAAAAAAAAAAAAAAAAAAD/2gAIAQMBAT8Qf//EABQRAQAAAAAAAAAAAAAAAAAAAAD/2gAIAQIBAT8Qf//EABkQAAIDAQAAAAAAAAAAAAAAAAERMUFRkf/aAAgBAQABPxC1HhihlLD3WT//2Q==");

        IIdentity identity = ctx.addIdentity(fiatCurrency, terminalSerialNumber, externalId, limits, limits, limits, limits, limits, note, state, discount, discount, now, now);
        String identityPublicId = identity.getPublicId();
        ctx.addIdentityPiece(identityPublicId, IdentityPieceExample.fromPersonalInfo(firstName, lastName, idCardNumber, IIdentityPiece.DOCUMENT_TYPE_ID_CARD,
            documentValidToYYYYMMDD == null ? null : new SimpleDateFormat("yyyyMMdd", Locale.US).parse(documentValidToYYYYMMDD),
            contactZIP, contactCountry, contactCountryIso2, contactProvince, contactCity, contactAddress, dateOfBirthParsed, occupation, ssn));
        ctx.addIdentityPiece(identityPublicId, IdentityPieceExample.fromPhoneNumber(phoneNumber));
        ctx.addIdentityPiece(identityPublicId, IdentityPieceExample.fromEmailAddress(emailAddress));
        ctx.addIdentityPiece(identityPublicId, IdentityPieceExample.fromSelfie("image/jpeg", exampleJpeg));
        ctx.addIdentityPiece(identityPublicId, IdentityPieceExample.fromIdScan("image/jpeg", exampleJpeg));

        return identityPublicId;
    }

    // curl -k -XPOST https://localhost:7743/extensions/identity-example/update -d "identityPublicId=IE3BVEBUIIXZ3SZV&emailAddress=email@example.com"
    @POST
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    public String update(@FormParam("identityPublicId") String identityPublicId, @FormParam("emailAddress") String emailAddress) {

        IExtensionContext ctx = IdentityExampleExtension.getExtensionContext();
        IIdentity identity = ctx.findIdentityByIdentityId(identityPublicId);
        if (identity == null) {
            return "identity not found";
        }
        int newState = IIdentity.STATE_REGISTERED;
        String note = identity.getNote() + " updated from an extension";
        IIdentity updatedIdentity = ctx.updateIdentity(identityPublicId, identity.getExternalId(),
            newState, identity.getType(), identity.getCreated(), identity.getRegistered(),
            identity.getVipBuyDiscount(), identity.getVipSellDiscount(), note,
            identity.getLimitCashPerTransaction(), identity.getLimitCashPerHour(), identity.getLimitCashPerDay(), identity.getLimitCashPerWeek(),
            identity.getLimitCashPerMonth(), identity.getLimitCashPer3Months(), identity.getLimitCashPer12Months(), identity.getLimitCashPerCalendarQuarter(),
            identity.getLimitCashPerCalendarYear(), identity.getLimitCashTotalIdentity(), identity.getConfigurationCashCurrency());

        return updatedIdentity.getPublicId();
    }

    // curl -k -XPOST https://localhost:7743/extensions/identity-example/update-opt-in -d "identityPublicId=IE3BVEBUIIXZ3SZV&agreeWithMarketingOptIn=true"
    @POST
    @Path("/update-opt-in")
    @Produces(MediaType.APPLICATION_JSON)
    public void updateOptInAgreement(@FormParam("identityPublicId") String identityPublicId, @FormParam("agreeWithMarketingOptIn") boolean agreeWithMarketingOptIn) {
        IExtensionContext ctx = IdentityExampleExtension.getExtensionContext();
        IIdentity identity = ctx.findIdentityByIdentityId(identityPublicId);
        if (identity == null) {
            log.debug("Identity {} not found", identityPublicId);
            return;
        }
        ctx.updateIdentityMarketingOptIn(identityPublicId, agreeWithMarketingOptIn);
    }

    // curl -k -XPOST https://localhost:7743/extensions/identity-example/getnotes -d "identityPublicId=IE3BVEBUIIXZ3SZV"
    @POST
    @Path("/getnotes")
    @Produces(MediaType.APPLICATION_JSON)
    public List<IIdentityNote> getNotes(@FormParam("identityPublicId") String identityPublicId) {

        IExtensionContext ctx = IdentityExampleExtension.getExtensionContext();
        IIdentity identity = ctx.findIdentityByIdentityId(identityPublicId);
        if (identity == null) {
            return new ArrayList<>();
        }

        return identity.getNotes();
    }

    // curl -k -XPOST https://localhost:7743/extensions/identity-example/getidentityremaininglimits -d "fiatCurrency=CZK&terminalSerialNumber=BT100305&identityPublicId=IE3BVEBUIIXZ3SZV"
    @POST
    @Path("/getidentityremaininglimits")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ILimitExtended> getIdentityRemainingLimits(@FormParam("fiatCurrency") String fiatCurrency, @FormParam("terminalSerialNumber") String terminalSerialNumber, @FormParam("identityPublicId") String identityPublicId /* , fiatCurrency */) {

        IExtensionContext ctx = IdentityExampleExtension.getExtensionContext();

        return ctx.getIdentityRemainingLimits(fiatCurrency, terminalSerialNumber, identityPublicId);
    }

    // curl -k -XPOST https://localhost:7743/extensions/identity-example/find-identities-by-phone-number?phoneNumber=%2B420608123555
    // curl -k -XPOST https://localhost:7743/extensions/identity-example/find-identities-by-phone-number?phoneNumber=608123555&country=CZ
    @POST
    @Path("/find-identities-by-phone-number")
    @Produces(MediaType.APPLICATION_JSON)
    public List<IIdentity> findIdentitiesByPhoneNumber(@QueryParam("phoneNumber") String phoneNumber, @QueryParam("country") String country) {
        IExtensionContext ctx = IdentityExampleExtension.getExtensionContext();

        log.debug("Call findIdentitiesByPhoneNumber phoneNumber='{}' country='{}'", phoneNumber, country);

        long before = System.nanoTime();
        List<IIdentity> identities;
        if (country == null) {
            identities = ctx.findIdentitiesByPhoneNumber(phoneNumber);
        } else {
            identities = ctx.findIdentitiesByPhoneNumber(phoneNumber, country);
        }

        long after = System.nanoTime();
        long duration = TimeUnit.NANOSECONDS.toMillis(after - before);
        log.debug("Found {} identities for phone number '{}' in {} milliseconds", identities.size(), phoneNumber, duration);

        return identities;
    }

    // curl -k -XPOST https://localhost:7743/extensions/identity-example/find-identities-base-by-phone-number?phoneNumber=%2B420608123555
    // curl -k -XPOST https://localhost:7743/extensions/identity-example/find-identities-base-by-phone-number?phoneNumber=608123555&country=CZ
    @POST
    @Path("/find-identities-base-by-phone-number")
    @Produces(MediaType.APPLICATION_JSON)
    public List<IIdentityBase> findIdentitiesBaseByPhoneNumber(@QueryParam("phoneNumber") String phoneNumber, @QueryParam("country") String country) {
        IExtensionContext ctx = IdentityExampleExtension.getExtensionContext();

        log.debug("Call findIdentitiesBaseByPhoneNumber phoneNumber='{}' country='{}'", phoneNumber, country);

        long before = System.nanoTime();
        List<IIdentityBase> identities;
        if (country == null) {
            identities = ctx.findIdentitiesBaseByPhoneNumber(phoneNumber);
        } else {
            identities = ctx.findIdentitiesBaseByPhoneNumber(phoneNumber, country);
        }
        long after = System.nanoTime();
        long duration = TimeUnit.NANOSECONDS.toMillis(after - before);
        log.debug("Found {} identities for phone number '{}' in {} milliseconds", identities.size(), phoneNumber, duration);

        return identities;
    }

    // curl -k -XPOST https://localhost:7743/extensions/identity-example/startverif?identityPublicId=IE3BVEBUIIXZ3SZV&message=Please%20verify%20identity%20%7Blink%7D
    @POST
    @Path("/startverif")
    @Produces(MediaType.APPLICATION_JSON)
    public IVerificationInfo startVerificationByIdentityId(@QueryParam("identityPublicId") String identityPublicId, @QueryParam("message") String message) {
        IExtensionContext ctx = IdentityExampleExtension.getExtensionContext();
        return ctx.startVerificationByIdentityId(identityPublicId, message);
    }

}
