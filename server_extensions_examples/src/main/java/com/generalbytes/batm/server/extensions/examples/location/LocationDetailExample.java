package com.generalbytes.batm.server.extensions.examples.location;

import com.generalbytes.batm.server.extensions.ICashCollectionDay;
import com.generalbytes.batm.server.extensions.ILocationDetail;
import com.generalbytes.batm.server.extensions.INote;
import com.generalbytes.batm.server.extensions.IOpeningHours;
import com.generalbytes.batm.server.extensions.IOrganization;
import com.generalbytes.batm.server.extensions.IPerson;

import java.util.ArrayList;
import java.util.List;

public class LocationDetailExample implements ILocationDetail {
    private String name;
    private String contactAddress;
    private String city;
    private String country;
    private String countryIso2;
    private String province;
    private String zip;
    private String description;
    private String gpsLat;
    private String gpsLon;
    private String timeZone;
    private String publicId;
    private String externalLocationId;

    private PersonExample contactPerson;
    private OrganizationExample organization;
    private String cashCollectionCompany;
    private Integer terminalCapacity;
    private List<NoteExample> notes;
    private List<OpeningHoursExample> openingHours;
    private List<CashCollectionDayExample> cashCollectionDays = new ArrayList<>();

    public LocationDetailExample() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getContactAddress() {
        return contactAddress;
    }

    @Override
    public String getCity() {
        return city;
    }

    @Override
    public String getCountry() {
        return country;
    }

    @Override
    public String getCountryIso2() {
        return countryIso2;
    }

    @Override
    public String getProvince() {
        return province;
    }

    @Override
    public String getZip() {
        return zip;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getGpsLat() {
        return gpsLat;
    }

    @Override
    public String getGpsLon() {
        return gpsLon;
    }

    @Override
    public String getTimeZone() {
        return timeZone;
    }

    @Override
    public String getPublicId() {
        return publicId;
    }

    @Override
    public String getExternalLocationId() {
        return externalLocationId;
    }

    @Override
    public IPerson getContactPerson() {
        return contactPerson;
    }

    @Override
    public IOrganization getOrganization() {
        return organization;
    }

    @Override
    public String getCashCollectionCompany() {
        return cashCollectionCompany;
    }

    @Override
    public Integer getTerminalCapacity() {
        return terminalCapacity;
    }

    @Override
    public List<INote> getNotes() {
        return new ArrayList<>(notes);
    }

    @Override
    public List<IOpeningHours> getOpeningHours() {
        return new ArrayList<>(openingHours);
    }

    @Override
    public List<ICashCollectionDay> getCashCollectionDays() {
        return new ArrayList<>(cashCollectionDays);
    }
}
