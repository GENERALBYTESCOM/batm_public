package com.generalbytes.batm.server.extensions;

/**
 * An object that describes data associated with the contact specified in the request.
 */
public class Contact implements java.io.Serializable{

    private boolean exists;

    private String error;

    private String firstName;

    private String lastName;

    private String address1;

    private String address2;

    private String address3;

    private String address4;

    private String city;

    private String stateProvince;

    private String country;

    private String zipCode;

    /**
     * True if exists contact data, else false.
     */
    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    /**
     * Error message. Contact data are not sent.
     */
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    /**
     * A string that indicates the contact's first name or given name. If the result is a business name, the full business name goes in this field. Depending on the data provider, it might or might not be possible to distinguish business names from personal names.
     */
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * A string that indicates the contact's last name or family name. If that contact is a business, this value is null.
     */
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * A string that indicates the first line of the contact's street address.
     */
    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    /**
     * A string that indicates the second line of the contact's street address.
     */
    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /**
     * A string that indicates the third line of the contact's street address.
     */
    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    /**
     * A string that indicates the fourth line of the contact's street address.
     */
    public String getAddress4() {
        return address4;
    }

    public void setAddress4(String address4) {
        this.address4 = address4;
    }

    /**
     * A string that indicates the city in which the contact is located.
     */
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    /**
     * A string that indicates the state, province, or region in which the contact is located.
     */
    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    /**
     * A string that indicates the country in which the contact is located.
     * ISO 3166-1 alpha-2 code of the country (2 digits).
     */
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * A string that indicates Postal Service ZIP Code in which the contact is located.
     */
    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "exists=" + exists +
                ", error='" + error + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address1='" + address1 + '\'' +
                ", address2='" + address2 + '\'' +
                ", address3='" + address3 + '\'' +
                ", address4='" + address4 + '\'' +
                ", city='" + city + '\'' +
                ", stateProvince='" + stateProvince + '\'' +
                ", country='" + country + '\'' +
                ", zipCode='" + zipCode + '\'' +
                '}';
    }
}
