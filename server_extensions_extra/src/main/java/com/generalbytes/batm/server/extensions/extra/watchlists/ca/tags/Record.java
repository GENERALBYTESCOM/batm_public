package com.generalbytes.batm.server.extensions.extra.watchlists.ca.tags;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "record", propOrder = {
    "country",
    "lastName",
    "givenName",
    "dateOfBirth",
    "item",
    "entity",
    "aliases",
    "schedule",
    "title"
})
public class Record {

    @XmlElement(name = "Country")
    private String country;
    @XmlElement(name = "LastName")
    private String lastName;
    @XmlElement(name = "GivenName")
    private String givenName;
    @XmlElement(name = "DateOfBirth")
    private String dateOfBirth;
    @XmlElement(name ="Item" )
    private String item;
    @XmlElement(name = "Entity")
    private String entity;
    @XmlElement(name = "Aliases")
    private String aliases;
    @XmlElement(name = "Schedule")
    private String schedule;
    @XmlElement(name = "Title")
    private String title;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getAliases() {
        return aliases;
    }

    public void setAliases(String aliases) {
        this.aliases = aliases;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
