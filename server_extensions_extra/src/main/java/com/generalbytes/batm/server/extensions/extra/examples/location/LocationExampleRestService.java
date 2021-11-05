package com.generalbytes.batm.server.extensions.extra.examples.location;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.ILocation;
import com.generalbytes.batm.server.extensions.ILocationDetail;
import com.generalbytes.batm.server.extensions.extra.examples.identity.IdentityExampleExtension;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Path("/")
public class LocationExampleRestService {

    // curl -k -XPOST https://localhost:7743/extensions/location-example/getlocation -d "locationPublicId=PITKZD"
    @POST
    @Path("/getlocation")
    @Produces(MediaType.APPLICATION_JSON)
    public String getLocation(@FormParam("locationPublicId") String locationPublicId) throws JsonProcessingException {
        IExtensionContext ctx = IdentityExampleExtension.getExtensionContext();

        ILocationDetail loc =  ctx.getLocationByPublicId(locationPublicId);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm a z"));
        return objectMapper.writeValueAsString(loc);
    }

    //curl -k -XPOST https://localhost:7743/extensions/location-example/addLocation -d "location={"name":"Location001","contactAddress":"newLocation Address 888","city":"Hamburg","country":"Germany","countryIso2":"DE","province":"","zip":"386 23","description":"Some Short Description","gpsLat":"98.7620","gpsLon":"69.057744","timeZone":"Europe/Berlin","contactPerson":{"id":1,"firstname":"Admin","lastname":"Administranos","contactEmail":"contact1@gmail.com","contactPhone":null,"contactAddress":null,"contactCity":null,"contactCountry":null,"contactCountryIso2":null,"contactProvince":null,"contactZIP":null,"qrcodeId":null,"createdAt":null,"telegramUserId":null,"telegramChatId":null},"organization":{"id":"1","name":"GENERAL CCC BYTES s.r.o."},"cashCollectionCompany":"CashCollection company","terminalCapacity":1,"notes":[{"text":"note001","createdAt":"2021-09-21T13:40 PM CEST","deletedAt":null,"userName":"a","deleted":false},{"text":"note002","createdAt":"2021-09-27T15:05 PM CEST","deletedAt":null,"userName":"a","deleted":false}],"openingHours":[{"day":"MON","from":"1970-01-01T08:00 AM CET","to":"1970-01-01T20:00 PM CET","cashCollectionDay":true},{"day":"TUE","from":"1970-01-01T08:00 AM CET","to":"1970-01-01T20:00 PM CET","cashCollectionDay":true},{"day":"WED","from":"1970-01-01T08:00 AM CET","to":"1970-01-01T20:00 PM CET","cashCollectionDay":true},{"day":"THU","from":"1970-01-01T08:00 AM CET","to":"1970-01-01T20:00 PM CET","cashCollectionDay":false},{"day":"FRI","from":"1970-01-01T08:00 AM CET","to":"1970-01-01T20:00 PM CET","cashCollectionDay":false}]}"
    @POST
    @Path("/addlocation")
    @Produces(MediaType.APPLICATION_JSON)
    public ILocation addLocation(@FormParam("location") String locationJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm a z"));
        ILocationDetail location = objectMapper.readValue(locationJson, LocationDetailExample.class);

        IExtensionContext ctx = IdentityExampleExtension.getExtensionContext();

        return ctx.addLocation(location);
    }

    //curl -k -XPOST https://localhost:7743/extensions/location-example/updateLocation -d "locationPublicId=P8V3MU&location={"name":"Location001","contactAddress":"newLocation Address 888","city":"Hamburg","country":"Germany","countryIso2":"DE","province":"","zip":"386 23","description":"Some Short Description","gpsLat":"98.7620","gpsLon":"69.057744","timeZone":"Europe/Berlin","publicId":"PITXXX","contactPerson":{"id":1,"firstname":"Admin","lastname":"Administranos","contactEmail":"contact1@gmail.com","contactPhone":null,"contactAddress":null,"contactCity":null,"contactCountry":null,"contactCountryIso2":null,"contactProvince":null,"contactZIP":null,"qrcodeId":null,"createdAt":null,"telegramUserId":null,"telegramChatId":null},"organization":{"id":"1","name":"GENERAL CCC BYTES s.r.o."},"cashCollectionCompany":"CashCollection company","terminalCapacity":1,"notes":[{"text":"note003","createdAt":"2021-09-21T13:40 PM CEST","deletedAt":null,"userName":"a","deleted":true},{"text":"note004","createdAt":"2021-09-27T15:05 PM CEST","deletedAt":null,"userName":"a","deleted":true}],"openingHours":[{"day":"MON","from":"1970-01-01T08:00 AM CET","to":"1970-01-01T20:00 PM CET","cashCollectionDay":false},{"day":"TUE","from":"1970-01-01T08:00 AM CET","to":"1970-01-01T20:00 PM CET","cashCollectionDay":false},{"day":"WED","from":"1970-01-01T08:00 AM CET","to":"1970-01-01T20:00 PM CET","cashCollectionDay":true},{"day":"THU","from":"1970-01-01T08:00 AM CET","to":"1970-01-01T20:00 PM CET","cashCollectionDay":false},{"day":"FRI","from":"1970-01-01T08:00 AM CET","to":"1970-01-01T20:00 PM CET","cashCollectionDay":false}]}"
    @POST
    @Path("/updatelocation")
    @Produces(MediaType.APPLICATION_JSON)
    public ILocation updateLocation(@FormParam("locationPublicId") String locationPublicId, @FormParam("location") String locationJson) throws ParseException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm a z"));
        ILocationDetail location = objectMapper.readValue(locationJson, LocationDetailExample.class);

        IExtensionContext ctx = IdentityExampleExtension.getExtensionContext();

        return ctx.updateLocationById(locationPublicId, location);
    }

}
