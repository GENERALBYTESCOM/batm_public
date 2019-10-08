/*************************************************************************************
 * Copyright (C) 2014-2019 GENERAL BYTES s.r.o. All rights reserved.
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
import com.generalbytes.batm.server.extensions.ILimit;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Path("/")
public class IdentityExampleRestService {

    // Uncomment this example in ***batm-extensions.xml*** and call it for example with:
    // curl -k -XPOST https://localhost:7743/extensions/identity-example/register -d "terminalSerialNumber=BT102239&externalId=EXTID0001&fiatCurrency=USD&limit=1000000&discount=100&phoneNumber=+12065550100&firstName=Chuck&lastName=Norris&emailAddress=chucknorrisfans@hotmail.com&idCardNumber=123456&contactZIP=77868&contactCountry=United States&contactProvince=TX&contactCity=Navasota&contactAddress=4360 Lone Wolf Ranch Road"

    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    public String register(@FormParam("fiatCurrency") String fiatCurrency, @FormParam("externalId") String externalId,
                           @FormParam("limit") BigDecimal limit, @FormParam("discount") BigDecimal discount,
                           @FormParam("terminalSerialNumber") String terminalSerialNumber, @FormParam("note") String note,
                           @FormParam("phoneNumber") String phoneNumber, @FormParam("firstName") String firstName,
                           @FormParam("lastName") String lastName, @FormParam("emailAddress") String emailAddress,
                           @FormParam("idCardNumber") String idCardNumber, @FormParam("contactZIP") String contactZIP,
                           @FormParam("contactCountry") String contactCountry, @FormParam("contactProvince") String contactProvince,
                           @FormParam("contactCity") String contactCity, @FormParam("contactAddress") String contactAddress) {


        IExtensionContext ctx = IdentityExampleExtension.getExtensionContext();
        List<ILimit> limits = Arrays.asList(new LimitExample(fiatCurrency, limit));

        int state = IIdentity.STATE_REGISTERED;
        Date now = new Date();

        // read the image data from the request or a file
        byte[] exampleJpeg = Base64.getDecoder().decode("/9j/2wBDACQZGyAbFyQgHiApJyQrNls7NjIyNm9PVEJbhHSKiIF0f32Ro9GxkZrFnX1/tve4xdje6uzqja////7j/9Hl6uH/2wBDAScpKTYwNms7O2vhln+W4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eHh4eH/wgARCAAFAAUDAREAAhEBAxEB/8QAFAABAAAAAAAAAAAAAAAAAAAAAf/EABQBAQAAAAAAAAAAAAAAAAAAAAD/2gAMAwEAAhADEAAAAQ//xAAVEAEBAAAAAAAAAAAAAAAAAAACAf/aAAgBAQABBQImq//EABQRAQAAAAAAAAAAAAAAAAAAAAD/2gAIAQMBAT8Bf//EABQRAQAAAAAAAAAAAAAAAAAAAAD/2gAIAQIBAT8Bf//EABgQAAMBAQAAAAAAAAAAAAAAAAECEQCh/9oACAEBAAY/AmAaTu//xAAWEAEBAQAAAAAAAAAAAAAAAAABABH/2gAIAQEAAT8hYg3ik//aAAwDAQACAAMAAAAQH//EABQRAQAAAAAAAAAAAAAAAAAAAAD/2gAIAQMBAT8Qf//EABQRAQAAAAAAAAAAAAAAAAAAAAD/2gAIAQIBAT8Qf//EABkQAAIDAQAAAAAAAAAAAAAAAAERMUFRkf/aAAgBAQABPxC1HhihlLD3WT//2Q==");

        IIdentity identity = ctx.addIdentity(fiatCurrency, terminalSerialNumber, externalId, limits, limits, limits, limits, limits, note, state, discount, discount, now, now);
        String identityPublicId = identity.getPublicId();
        ctx.addIdentityPiece(identityPublicId, IdentityPieceExample.fromPersonalInfo(firstName, lastName, idCardNumber, contactZIP, contactCountry, contactProvince, contactCity, contactAddress));
        ctx.addIdentityPiece(identityPublicId, IdentityPieceExample.fromPhoneNumber(phoneNumber));
        ctx.addIdentityPiece(identityPublicId, IdentityPieceExample.fromEmailAddress(emailAddress));
        ctx.addIdentityPiece(identityPublicId, IdentityPieceExample.fromSelfie("image/jpeg", exampleJpeg));
        ctx.addIdentityPiece(identityPublicId, IdentityPieceExample.fromIdScan("image/jpeg", exampleJpeg));

        return identityPublicId;
    }

}
