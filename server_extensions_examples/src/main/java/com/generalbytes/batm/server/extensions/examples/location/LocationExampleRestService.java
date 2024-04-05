package com.generalbytes.batm.server.extensions.examples.location;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.ILocation;
import com.generalbytes.batm.server.extensions.ILocationDetail;

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
        IExtensionContext ctx = LocationExampleExtension.getExtensionContext();

        ILocationDetail loc =  ctx.getLocationByPublicId(locationPublicId);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm a z"));
        return objectMapper.writeValueAsString(loc);
    }

    //curl -k -XPOST https://localhost:7743/extensions/location-example/addLocation -d "location={"name":"NAME_59","contactAddress":"ADDR_59","city":"CITY59","country":"Czech Republic","countryIso2":"CZ","province":"","zip":"","description":"","gpsLat":"50.10847","gpsLon":"14.4518","timeZone":"Europe/Prague","publicId":"P7MPOR","contactPerson":{"id":3,"firstname":"FN_3","lastname":"LN_3","contactEmail":"EMAIL_3","contactPhone":"+1 211-211-1111","contactAddress":"ADDR_3","contactCity":"CITY_3","contactCountry":"United States","contactCountryIso2":"US","contactProvince":"IL","contactZIP":null,"qrcodeId":"","createdAt":null,"telegramUserId":null,"telegramChatId":null},"organization":{"id":"2","name":"NAME_2"},"cashCollectionCompany":"","terminalCapacity":2,"notes":[],"openingHours":[{"day":"MON","from":"1970-01-01T08:00 AM CET","to":"1970-01-01T20:00 PM CET","cashCollectionDay":false},{"day":"TUE","from":"1970-01-01T08:00 AM CET","to":"1970-01-01T20:00 PM CET","cashCollectionDay":false},{"day":"WED","from":"1970-01-01T08:00 AM CET","to":"1970-01-01T20:00 PM CET","cashCollectionDay":false},{"day":"THU","from":"1970-01-01T08:00 AM CET","to":"1970-01-01T20:00 PM CET","cashCollectionDay":false},{"day":"FRI","from":"1970-01-01T08:00 AM CET","to":"1970-01-01T20:00 PM CET","cashCollectionDay":false}],"cashCollectionDays":[{"dayOfMonth":6},{"dayOfMonth":7},{"dayOfMonth":8}]}"
    //curl -k -XPOST https://localhost:7743/extensions/location-example/addLocation -d "location={"name":"NAME_59","contactAddress":"ADDR_59","city":"CITY59","country":"Czech Republic","countryIso2":"CZ","province":"","zip":"","description":"","gpsLat":"50.10847","gpsLon":"14.4518","timeZone":"Europe/Prague","publicId":"P7MPOR","contactPerson":{"id":3,"firstname":"FN_3","lastname":"LN_3","contactEmail":"EMAIL_3","contactPhone":"+1 211-211-1111","contactAddress":"ADDR_3","contactCity":"CITY_3","contactCountry":"United States","contactCountryIso2":"US","contactProvince":"IL","contactZIP":null,"qrcodeId":"","createdAt":null,"telegramUserId":null,"telegramChatId":null},"organization":{"id":"2","name":"NAME_2"},"cashCollectionCompany":"","terminalCapacity":2,"notes":[],"openingHours":[{"day":"MON","from":"1970-01-01T08:00 AM CET","to":"1970-01-01T20:00 PM CET","cashCollectionDay":true},{"day":"TUE","from":"1970-01-01T08:00 AM CET","to":"1970-01-01T20:00 PM CET","cashCollectionDay":false},{"day":"WED","from":"1970-01-01T08:00 AM CET","to":"1970-01-01T20:00 PM CET","cashCollectionDay":true},{"day":"THU","from":"1970-01-01T08:00 AM CET","to":"1970-01-01T20:00 PM CET","cashCollectionDay":true},{"day":"FRI","from":"1970-01-01T08:00 AM CET","to":"1970-01-01T20:00 PM CET","cashCollectionDay":false}]}"
    @POST
    @Path("/addlocation")
    @Produces(MediaType.APPLICATION_JSON)
    public ILocation addLocation(@FormParam("location") String locationJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm a z"));
        ILocationDetail location = objectMapper.readValue(locationJson, LocationDetailExample.class);

        IExtensionContext ctx = LocationExampleExtension.getExtensionContext();

        return ctx.addLocation(location);
    }

    //curl -k -XPOST https://localhost:7743/extensions/location-example/updateLocation -d "locationPublicId=P8V3MU&location={"name":"Location001","contactAddress":"newLocation Address 888","city":"Hamburg","country":"Germany","countryIso2":"DE","province":"","zip":"386 23","description":"Some Short Description","gpsLat":"98.7620","gpsLon":"69.057744","timeZone":"Europe/Berlin","publicId":"PITXXX","contactPerson":{"id":1,"firstname":"Admin","lastname":"Administranos","contactEmail":"contact1@gmail.com","contactPhone":null,"contactAddress":null,"contactCity":null,"contactCountry":null,"contactCountryIso2":null,"contactProvince":null,"contactZIP":null,"qrcodeId":null,"createdAt":null,"telegramUserId":null,"telegramChatId":null},"organization":{"id":"1","name":"GENERAL CCC BYTES s.r.o."},"cashCollectionCompany":"CashCollection company","terminalCapacity":1,"notes":[{"text":"note003","createdAt":"2021-09-21T13:40 PM CEST","deletedAt":null,"userName":"a","deleted":true},{"text":"note004","createdAt":"2021-09-27T15:05 PM CEST","deletedAt":null,"userName":"a","deleted":true}],"openingHours":[{"day":"MON","from":"1970-01-01T08:00 AM CET","to":"1970-01-01T20:00 PM CET","cashCollectionDay":false},{"day":"TUE","from":"1970-01-01T08:00 AM CET","to":"1970-01-01T20:00 PM CET","cashCollectionDay":false},{"day":"WED","from":"1970-01-01T08:00 AM CET","to":"1970-01-01T20:00 PM CET","cashCollectionDay":true},{"day":"THU","from":"1970-01-01T08:00 AM CET","to":"1970-01-01T20:00 PM CET","cashCollectionDay":false},{"day":"FRI","from":"1970-01-01T08:00 AM CET","to":"1970-01-01T20:00 PM CET","cashCollectionDay":true}]}"
    //curl -k -XPOST https://localhost:7743/extensions/location-example/updateLocation -d "locationPublicId=P8V3MU&location={"name":"Location001","contactAddress":"newLocation Address 888","city":"Hamburg","country":"Germany","countryIso2":"DE","province":"","zip":"386 23","description":"Some Short Description","gpsLat":"98.7620","gpsLon":"69.057744","timeZone":"Europe/Berlin","publicId":"PITXXX","contactPerson":{"id":1,"firstname":"Admin","lastname":"Administranos","contactEmail":"contact1@gmail.com","contactPhone":null,"contactAddress":null,"contactCity":null,"contactCountry":null,"contactCountryIso2":null,"contactProvince":null,"contactZIP":null,"qrcodeId":null,"createdAt":null,"telegramUserId":null,"telegramChatId":null},"organization":{"id":"1","name":"GENERAL CCC BYTES s.r.o."},"cashCollectionCompany":"CashCollection company","terminalCapacity":1,"notes":[{"text":"note003","createdAt":"2021-09-21T13:40 PM CEST","deletedAt":null,"userName":"a","deleted":true},{"text":"note004","createdAt":"2021-09-27T15:05 PM CEST","deletedAt":null,"userName":"a","deleted":true}],"openingHours":[{"day":"MON","from":"1970-01-01T08:00 AM CET","to":"1970-01-01T20:00 PM CET","cashCollectionDay":false},{"day":"TUE","from":"1970-01-01T08:00 AM CET","to":"1970-01-01T20:00 PM CET","cashCollectionDay":false},{"day":"WED","from":"1970-01-01T08:00 AM CET","to":"1970-01-01T20:00 PM CET","cashCollectionDay":false},{"day":"THU","from":"1970-01-01T08:00 AM CET","to":"1970-01-01T20:00 PM CET","cashCollectionDay":false},{"day":"FRI","from":"1970-01-01T08:00 AM CET","to":"1970-01-01T20:00 PM CET","cashCollectionDay":false}], "cashCollectionDays":[{"dayOfMonth":6}]}"
    @POST
    @Path("/updatelocation")
    @Produces(MediaType.APPLICATION_JSON)
    public ILocation updateLocation(@FormParam("locationPublicId") String locationPublicId, @FormParam("location") String locationJson) throws ParseException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm a z"));
        ILocationDetail location = objectMapper.readValue(locationJson, LocationDetailExample.class);

        IExtensionContext ctx = LocationExampleExtension.getExtensionContext();

        return ctx.updateLocationById(locationPublicId, location);
    }

}
