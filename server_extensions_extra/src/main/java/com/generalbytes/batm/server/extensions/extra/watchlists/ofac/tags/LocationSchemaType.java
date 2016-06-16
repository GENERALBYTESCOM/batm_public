
package com.generalbytes.batm.server.extensions.extra.watchlists.ofac.tags;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for LocationSchemaType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LocationSchemaType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.un.org/sanctions/1.0}Comment" minOccurs="0"/>
 *         &lt;element name="LocationAreaCode" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="AreaCodeID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *                 &lt;attribute name="DeltaAction" type="{http://www.un.org/sanctions/1.0}DeltaActionSchemaType" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="LocationCountry" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="CountryID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *                 &lt;attribute name="CountryRelevanceID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *                 &lt;attribute name="DeltaAction" type="{http://www.un.org/sanctions/1.0}DeltaActionSchemaType" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="LocationPart" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="LocationPartValue" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element ref="{http://www.un.org/sanctions/1.0}Comment" minOccurs="0"/>
 *                             &lt;element name="Value">
 *                               &lt;complexType>
 *                                 &lt;simpleContent>
 *                                   &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                                     &lt;attribute name="DeltaAction" type="{http://www.un.org/sanctions/1.0}DeltaActionSchemaType" />
 *                                   &lt;/extension>
 *                                 &lt;/simpleContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="Primary" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *                           &lt;attribute name="LocPartValueTypeID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *                           &lt;attribute name="LocPartValueStatusID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *                           &lt;attribute name="DeltaAction" type="{http://www.un.org/sanctions/1.0}DeltaActionSchemaType" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="LocPartTypeID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *                 &lt;attribute name="DeltaAction" type="{http://www.un.org/sanctions/1.0}DeltaActionSchemaType" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element ref="{http://www.un.org/sanctions/1.0}FeatureVersionReference" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.un.org/sanctions/1.0}IDRegDocumentReference" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="ID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="DeltaAction" type="{http://www.un.org/sanctions/1.0}DeltaActionSchemaType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LocationSchemaType", namespace = "http://www.un.org/sanctions/1.0", propOrder = {
    "comment",
    "locationAreaCode",
    "locationCountry",
    "locationPart",
    "featureVersionReference",
    "idRegDocumentReference"
})
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
public class LocationSchemaType {

    @XmlElement(name = "Comment", namespace = "http://www.un.org/sanctions/1.0")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    protected Comment comment;
    @XmlElement(name = "LocationAreaCode", namespace = "http://www.un.org/sanctions/1.0")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    protected List<LocationSchemaType.LocationAreaCode> locationAreaCode;
    @XmlElement(name = "LocationCountry", namespace = "http://www.un.org/sanctions/1.0")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    protected List<LocationSchemaType.LocationCountry> locationCountry;
    @XmlElement(name = "LocationPart", namespace = "http://www.un.org/sanctions/1.0")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    protected List<LocationSchemaType.LocationPart> locationPart;
    @XmlElement(name = "FeatureVersionReference", namespace = "http://www.un.org/sanctions/1.0")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    protected List<FeatureVersionReference> featureVersionReference;
    @XmlElement(name = "IDRegDocumentReference", namespace = "http://www.un.org/sanctions/1.0")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    protected List<IDRegDocumentReference> idRegDocumentReference;
    @XmlAttribute(name = "ID", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    protected BigInteger id;
    @XmlAttribute(name = "DeltaAction")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    protected DeltaActionSchemaType deltaAction;

    /**
     * Gets the value of the comment property.
     * 
     * @return
     *     possible object is
     *     {@link Comment }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public Comment getComment() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Comment }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public void setComment(Comment value) {
        this.comment = value;
    }

    /**
     * Gets the value of the locationAreaCode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the locationAreaCode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLocationAreaCode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LocationSchemaType.LocationAreaCode }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public List<LocationSchemaType.LocationAreaCode> getLocationAreaCode() {
        if (locationAreaCode == null) {
            locationAreaCode = new ArrayList<LocationSchemaType.LocationAreaCode>();
        }
        return this.locationAreaCode;
    }

    /**
     * Gets the value of the locationCountry property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the locationCountry property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLocationCountry().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LocationSchemaType.LocationCountry }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public List<LocationSchemaType.LocationCountry> getLocationCountry() {
        if (locationCountry == null) {
            locationCountry = new ArrayList<LocationSchemaType.LocationCountry>();
        }
        return this.locationCountry;
    }

    /**
     * Gets the value of the locationPart property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the locationPart property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLocationPart().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LocationSchemaType.LocationPart }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public List<LocationSchemaType.LocationPart> getLocationPart() {
        if (locationPart == null) {
            locationPart = new ArrayList<LocationSchemaType.LocationPart>();
        }
        return this.locationPart;
    }

    /**
     * Gets the value of the featureVersionReference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the featureVersionReference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFeatureVersionReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FeatureVersionReference }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public List<FeatureVersionReference> getFeatureVersionReference() {
        if (featureVersionReference == null) {
            featureVersionReference = new ArrayList<FeatureVersionReference>();
        }
        return this.featureVersionReference;
    }

    /**
     * Gets the value of the idRegDocumentReference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the idRegDocumentReference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIDRegDocumentReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IDRegDocumentReference }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public List<IDRegDocumentReference> getIDRegDocumentReference() {
        if (idRegDocumentReference == null) {
            idRegDocumentReference = new ArrayList<IDRegDocumentReference>();
        }
        return this.idRegDocumentReference;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public BigInteger getID() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public void setID(BigInteger value) {
        this.id = value;
    }

    /**
     * Gets the value of the deltaAction property.
     * 
     * @return
     *     possible object is
     *     {@link DeltaActionSchemaType }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public DeltaActionSchemaType getDeltaAction() {
        return deltaAction;
    }

    /**
     * Sets the value of the deltaAction property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeltaActionSchemaType }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public void setDeltaAction(DeltaActionSchemaType value) {
        this.deltaAction = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="AreaCodeID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
     *       &lt;attribute name="DeltaAction" type="{http://www.un.org/sanctions/1.0}DeltaActionSchemaType" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public static class LocationAreaCode {

        @XmlAttribute(name = "AreaCodeID", required = true)
        @XmlSchemaType(name = "nonNegativeInteger")
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        protected BigInteger areaCodeID;
        @XmlAttribute(name = "DeltaAction")
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        protected DeltaActionSchemaType deltaAction;

        /**
         * Gets the value of the areaCodeID property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        public BigInteger getAreaCodeID() {
            return areaCodeID;
        }

        /**
         * Sets the value of the areaCodeID property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        public void setAreaCodeID(BigInteger value) {
            this.areaCodeID = value;
        }

        /**
         * Gets the value of the deltaAction property.
         * 
         * @return
         *     possible object is
         *     {@link DeltaActionSchemaType }
         *     
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        public DeltaActionSchemaType getDeltaAction() {
            return deltaAction;
        }

        /**
         * Sets the value of the deltaAction property.
         * 
         * @param value
         *     allowed object is
         *     {@link DeltaActionSchemaType }
         *     
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        public void setDeltaAction(DeltaActionSchemaType value) {
            this.deltaAction = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="CountryID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
     *       &lt;attribute name="CountryRelevanceID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
     *       &lt;attribute name="DeltaAction" type="{http://www.un.org/sanctions/1.0}DeltaActionSchemaType" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public static class LocationCountry {

        @XmlAttribute(name = "CountryID", required = true)
        @XmlSchemaType(name = "nonNegativeInteger")
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        protected BigInteger countryID;
        @XmlAttribute(name = "CountryRelevanceID", required = true)
        @XmlSchemaType(name = "nonNegativeInteger")
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        protected BigInteger countryRelevanceID;
        @XmlAttribute(name = "DeltaAction")
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        protected DeltaActionSchemaType deltaAction;

        /**
         * Gets the value of the countryID property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        public BigInteger getCountryID() {
            return countryID;
        }

        /**
         * Sets the value of the countryID property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        public void setCountryID(BigInteger value) {
            this.countryID = value;
        }

        /**
         * Gets the value of the countryRelevanceID property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        public BigInteger getCountryRelevanceID() {
            return countryRelevanceID;
        }

        /**
         * Sets the value of the countryRelevanceID property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        public void setCountryRelevanceID(BigInteger value) {
            this.countryRelevanceID = value;
        }

        /**
         * Gets the value of the deltaAction property.
         * 
         * @return
         *     possible object is
         *     {@link DeltaActionSchemaType }
         *     
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        public DeltaActionSchemaType getDeltaAction() {
            return deltaAction;
        }

        /**
         * Sets the value of the deltaAction property.
         * 
         * @param value
         *     allowed object is
         *     {@link DeltaActionSchemaType }
         *     
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        public void setDeltaAction(DeltaActionSchemaType value) {
            this.deltaAction = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="LocationPartValue" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element ref="{http://www.un.org/sanctions/1.0}Comment" minOccurs="0"/>
     *                   &lt;element name="Value">
     *                     &lt;complexType>
     *                       &lt;simpleContent>
     *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *                           &lt;attribute name="DeltaAction" type="{http://www.un.org/sanctions/1.0}DeltaActionSchemaType" />
     *                         &lt;/extension>
     *                       &lt;/simpleContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *                 &lt;attribute name="Primary" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *                 &lt;attribute name="LocPartValueTypeID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
     *                 &lt;attribute name="LocPartValueStatusID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
     *                 &lt;attribute name="DeltaAction" type="{http://www.un.org/sanctions/1.0}DeltaActionSchemaType" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="LocPartTypeID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
     *       &lt;attribute name="DeltaAction" type="{http://www.un.org/sanctions/1.0}DeltaActionSchemaType" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "locationPartValue"
    })
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public static class LocationPart {

        @XmlElement(name = "LocationPartValue", namespace = "http://www.un.org/sanctions/1.0", required = true)
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        protected List<LocationSchemaType.LocationPart.LocationPartValue> locationPartValue;
        @XmlAttribute(name = "LocPartTypeID", required = true)
        @XmlSchemaType(name = "nonNegativeInteger")
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        protected BigInteger locPartTypeID;
        @XmlAttribute(name = "DeltaAction")
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        protected DeltaActionSchemaType deltaAction;

        /**
         * Gets the value of the locationPartValue property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the locationPartValue property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getLocationPartValue().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link LocationSchemaType.LocationPart.LocationPartValue }
         * 
         * 
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        public List<LocationSchemaType.LocationPart.LocationPartValue> getLocationPartValue() {
            if (locationPartValue == null) {
                locationPartValue = new ArrayList<LocationSchemaType.LocationPart.LocationPartValue>();
            }
            return this.locationPartValue;
        }

        /**
         * Gets the value of the locPartTypeID property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        public BigInteger getLocPartTypeID() {
            return locPartTypeID;
        }

        /**
         * Sets the value of the locPartTypeID property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        public void setLocPartTypeID(BigInteger value) {
            this.locPartTypeID = value;
        }

        /**
         * Gets the value of the deltaAction property.
         * 
         * @return
         *     possible object is
         *     {@link DeltaActionSchemaType }
         *     
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        public DeltaActionSchemaType getDeltaAction() {
            return deltaAction;
        }

        /**
         * Sets the value of the deltaAction property.
         * 
         * @param value
         *     allowed object is
         *     {@link DeltaActionSchemaType }
         *     
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        public void setDeltaAction(DeltaActionSchemaType value) {
            this.deltaAction = value;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element ref="{http://www.un.org/sanctions/1.0}Comment" minOccurs="0"/>
         *         &lt;element name="Value">
         *           &lt;complexType>
         *             &lt;simpleContent>
         *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
         *                 &lt;attribute name="DeltaAction" type="{http://www.un.org/sanctions/1.0}DeltaActionSchemaType" />
         *               &lt;/extension>
         *             &lt;/simpleContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *       &lt;attribute name="Primary" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
         *       &lt;attribute name="LocPartValueTypeID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
         *       &lt;attribute name="LocPartValueStatusID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
         *       &lt;attribute name="DeltaAction" type="{http://www.un.org/sanctions/1.0}DeltaActionSchemaType" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "comment",
            "value"
        })
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        public static class LocationPartValue {

            @XmlElement(name = "Comment", namespace = "http://www.un.org/sanctions/1.0")
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
            protected Comment comment;
            @XmlElement(name = "Value", namespace = "http://www.un.org/sanctions/1.0", required = true)
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
            protected LocationSchemaType.LocationPart.LocationPartValue.Value value;
            @XmlAttribute(name = "Primary", required = true)
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
            protected boolean primary;
            @XmlAttribute(name = "LocPartValueTypeID", required = true)
            @XmlSchemaType(name = "nonNegativeInteger")
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
            protected BigInteger locPartValueTypeID;
            @XmlAttribute(name = "LocPartValueStatusID", required = true)
            @XmlSchemaType(name = "nonNegativeInteger")
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
            protected BigInteger locPartValueStatusID;
            @XmlAttribute(name = "DeltaAction")
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
            protected DeltaActionSchemaType deltaAction;

            /**
             * Gets the value of the comment property.
             * 
             * @return
             *     possible object is
             *     {@link Comment }
             *     
             */
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
            public Comment getComment() {
                return comment;
            }

            /**
             * Sets the value of the comment property.
             * 
             * @param value
             *     allowed object is
             *     {@link Comment }
             *     
             */
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
            public void setComment(Comment value) {
                this.comment = value;
            }

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link LocationSchemaType.LocationPart.LocationPartValue.Value }
             *     
             */
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
            public LocationSchemaType.LocationPart.LocationPartValue.Value getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link LocationSchemaType.LocationPart.LocationPartValue.Value }
             *     
             */
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
            public void setValue(LocationSchemaType.LocationPart.LocationPartValue.Value value) {
                this.value = value;
            }

            /**
             * Gets the value of the primary property.
             * 
             */
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
            public boolean isPrimary() {
                return primary;
            }

            /**
             * Sets the value of the primary property.
             * 
             */
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
            public void setPrimary(boolean value) {
                this.primary = value;
            }

            /**
             * Gets the value of the locPartValueTypeID property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
            public BigInteger getLocPartValueTypeID() {
                return locPartValueTypeID;
            }

            /**
             * Sets the value of the locPartValueTypeID property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
            public void setLocPartValueTypeID(BigInteger value) {
                this.locPartValueTypeID = value;
            }

            /**
             * Gets the value of the locPartValueStatusID property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
            public BigInteger getLocPartValueStatusID() {
                return locPartValueStatusID;
            }

            /**
             * Sets the value of the locPartValueStatusID property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
            public void setLocPartValueStatusID(BigInteger value) {
                this.locPartValueStatusID = value;
            }

            /**
             * Gets the value of the deltaAction property.
             * 
             * @return
             *     possible object is
             *     {@link DeltaActionSchemaType }
             *     
             */
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
            public DeltaActionSchemaType getDeltaAction() {
                return deltaAction;
            }

            /**
             * Sets the value of the deltaAction property.
             * 
             * @param value
             *     allowed object is
             *     {@link DeltaActionSchemaType }
             *     
             */
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
            public void setDeltaAction(DeltaActionSchemaType value) {
                this.deltaAction = value;
            }


            /**
             * <p>Java class for anonymous complex type.
             * 
             * <p>The following schema fragment specifies the expected content contained within this class.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;simpleContent>
             *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
             *       &lt;attribute name="DeltaAction" type="{http://www.un.org/sanctions/1.0}DeltaActionSchemaType" />
             *     &lt;/extension>
             *   &lt;/simpleContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "value"
            })
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
            public static class Value {

                @XmlValue
                @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
                protected String value;
                @XmlAttribute(name = "DeltaAction")
                @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
                protected DeltaActionSchemaType deltaAction;

                /**
                 * Gets the value of the value property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
                public String getValue() {
                    return value;
                }

                /**
                 * Sets the value of the value property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
                public void setValue(String value) {
                    this.value = value;
                }

                /**
                 * Gets the value of the deltaAction property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link DeltaActionSchemaType }
                 *     
                 */
                @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
                public DeltaActionSchemaType getDeltaAction() {
                    return deltaAction;
                }

                /**
                 * Sets the value of the deltaAction property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link DeltaActionSchemaType }
                 *     
                 */
                @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
                public void setDeltaAction(DeltaActionSchemaType value) {
                    this.deltaAction = value;
                }

            }

        }

    }

}
