package com.generalbytes.batm.server.extensions.examples.location;

import com.generalbytes.batm.server.extensions.IPerson;

import java.util.Date;

public class PersonExample implements IPerson {

    private Long id;
    private String firstname;
    private String lastname;
    private String contactEmail;
    private String contactPhone;
    private String contactAddress;
    private String contactCity;
    private String contactCountry;
    private String contactCountryIso2;
    private String contactProvince;
    private String contactZIP;
    private String qrcodeId;
    private Date createdAt;
    private String telegramUserId;
    private String telegramChatId;

    public PersonExample() {
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getFirstname() {
        return firstname;
    }

    @Override
    public String getLastname() {
        return lastname;
    }

    @Override
    public String getContactEmail() {
        return contactEmail;
    }

    @Override
    public String getContactPhone() {
        return contactPhone;
    }

    @Override
    public String getContactAddress() {
        return contactAddress;
    }

    @Override
    public String getContactCity() {
        return contactCity;
    }

    @Override
    public String getContactCountry() {
        return contactCountry;
    }

    @Override
    public String getContactCountryIso2() {
        return contactCountryIso2;
    }

    @Override
    public String getContactProvince() {
        return contactProvince;
    }

    @Override
    public String getContactZIP() {
        return contactZIP;
    }

    @Override
    public String getQrcodeId() {
        return qrcodeId;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public String getTelegramUserId() {
        return telegramUserId;
    }

    @Override
    public String getTelegramChatId() {
        return telegramChatId;
    }
}
