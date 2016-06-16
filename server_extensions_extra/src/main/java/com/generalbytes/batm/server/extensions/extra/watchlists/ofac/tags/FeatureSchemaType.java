
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
 * <p>Java class for FeatureSchemaType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FeatureSchemaType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FeatureVersion" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.un.org/sanctions/1.0}Comment" minOccurs="0"/>
 *                   &lt;element ref="{http://www.un.org/sanctions/1.0}DatePeriod" maxOccurs="unbounded" minOccurs="0"/>
 *                   &lt;element name="VersionDetail" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                           &lt;attribute name="DetailTypeID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *                           &lt;attribute name="DetailReferenceID" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *                           &lt;attribute name="DeltaAction" type="{http://www.un.org/sanctions/1.0}DeltaActionSchemaType" />
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="VersionLocation" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="LocationID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *                           &lt;attribute name="DeltaAction" type="{http://www.un.org/sanctions/1.0}DeltaActionSchemaType" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element ref="{http://www.un.org/sanctions/1.0}IDRegDocumentReference" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="ID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *                 &lt;attribute name="ReliabilityID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *                 &lt;attribute name="DeltaAction" type="{http://www.un.org/sanctions/1.0}DeltaActionSchemaType" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="IdentityReference" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="IdentityID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *                 &lt;attribute name="IdentityFeatureLinkTypeID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *                 &lt;attribute name="DeltaAction" type="{http://www.un.org/sanctions/1.0}DeltaActionSchemaType" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="ID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="FeatureTypeID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="DeltaAction" type="{http://www.un.org/sanctions/1.0}DeltaActionSchemaType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FeatureSchemaType", namespace = "http://www.un.org/sanctions/1.0", propOrder = {
    "featureVersion",
    "identityReference"
})
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
public class FeatureSchemaType {

    @XmlElement(name = "FeatureVersion", namespace = "http://www.un.org/sanctions/1.0", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    protected List<FeatureSchemaType.FeatureVersion> featureVersion;
    @XmlElement(name = "IdentityReference", namespace = "http://www.un.org/sanctions/1.0", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    protected List<FeatureSchemaType.IdentityReference> identityReference;
    @XmlAttribute(name = "ID", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    protected BigInteger id;
    @XmlAttribute(name = "FeatureTypeID", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    protected BigInteger featureTypeID;
    @XmlAttribute(name = "DeltaAction")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    protected DeltaActionSchemaType deltaAction;

    /**
     * Gets the value of the featureVersion property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the featureVersion property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFeatureVersion().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FeatureSchemaType.FeatureVersion }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public List<FeatureSchemaType.FeatureVersion> getFeatureVersion() {
        if (featureVersion == null) {
            featureVersion = new ArrayList<FeatureSchemaType.FeatureVersion>();
        }
        return this.featureVersion;
    }

    /**
     * Gets the value of the identityReference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the identityReference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIdentityReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FeatureSchemaType.IdentityReference }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public List<FeatureSchemaType.IdentityReference> getIdentityReference() {
        if (identityReference == null) {
            identityReference = new ArrayList<FeatureSchemaType.IdentityReference>();
        }
        return this.identityReference;
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
     * Gets the value of the featureTypeID property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public BigInteger getFeatureTypeID() {
        return featureTypeID;
    }

    /**
     * Sets the value of the featureTypeID property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public void setFeatureTypeID(BigInteger value) {
        this.featureTypeID = value;
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
     *         &lt;element ref="{http://www.un.org/sanctions/1.0}DatePeriod" maxOccurs="unbounded" minOccurs="0"/>
     *         &lt;element name="VersionDetail" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;simpleContent>
     *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *                 &lt;attribute name="DetailTypeID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
     *                 &lt;attribute name="DetailReferenceID" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
     *                 &lt;attribute name="DeltaAction" type="{http://www.un.org/sanctions/1.0}DeltaActionSchemaType" />
     *               &lt;/extension>
     *             &lt;/simpleContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="VersionLocation" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="LocationID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
     *                 &lt;attribute name="DeltaAction" type="{http://www.un.org/sanctions/1.0}DeltaActionSchemaType" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element ref="{http://www.un.org/sanctions/1.0}IDRegDocumentReference" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *       &lt;attribute name="ID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
     *       &lt;attribute name="ReliabilityID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
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
        "datePeriod",
        "versionDetail",
        "versionLocation",
        "idRegDocumentReference"
    })
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public static class FeatureVersion {

        @XmlElement(name = "Comment", namespace = "http://www.un.org/sanctions/1.0")
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        protected Comment comment;
        @XmlElement(name = "DatePeriod", namespace = "http://www.un.org/sanctions/1.0")
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        protected List<DatePeriod> datePeriod;
        @XmlElement(name = "VersionDetail", namespace = "http://www.un.org/sanctions/1.0")
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        protected List<FeatureSchemaType.FeatureVersion.VersionDetail> versionDetail;
        @XmlElement(name = "VersionLocation", namespace = "http://www.un.org/sanctions/1.0")
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        protected List<FeatureSchemaType.FeatureVersion.VersionLocation> versionLocation;
        @XmlElement(name = "IDRegDocumentReference", namespace = "http://www.un.org/sanctions/1.0")
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        protected List<IDRegDocumentReference> idRegDocumentReference;
        @XmlAttribute(name = "ID", required = true)
        @XmlSchemaType(name = "nonNegativeInteger")
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        protected BigInteger id;
        @XmlAttribute(name = "ReliabilityID", required = true)
        @XmlSchemaType(name = "nonNegativeInteger")
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        protected BigInteger reliabilityID;
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
         * Gets the value of the datePeriod property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the datePeriod property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getDatePeriod().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link DatePeriod }
         * 
         * 
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        public List<DatePeriod> getDatePeriod() {
            if (datePeriod == null) {
                datePeriod = new ArrayList<DatePeriod>();
            }
            return this.datePeriod;
        }

        /**
         * Gets the value of the versionDetail property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the versionDetail property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getVersionDetail().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link FeatureSchemaType.FeatureVersion.VersionDetail }
         * 
         * 
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        public List<FeatureSchemaType.FeatureVersion.VersionDetail> getVersionDetail() {
            if (versionDetail == null) {
                versionDetail = new ArrayList<FeatureSchemaType.FeatureVersion.VersionDetail>();
            }
            return this.versionDetail;
        }

        /**
         * Gets the value of the versionLocation property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the versionLocation property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getVersionLocation().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link FeatureSchemaType.FeatureVersion.VersionLocation }
         * 
         * 
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        public List<FeatureSchemaType.FeatureVersion.VersionLocation> getVersionLocation() {
            if (versionLocation == null) {
                versionLocation = new ArrayList<FeatureSchemaType.FeatureVersion.VersionLocation>();
            }
            return this.versionLocation;
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
         * Gets the value of the reliabilityID property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        public BigInteger getReliabilityID() {
            return reliabilityID;
        }

        /**
         * Sets the value of the reliabilityID property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        public void setReliabilityID(BigInteger value) {
            this.reliabilityID = value;
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
         *       &lt;attribute name="DetailTypeID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
         *       &lt;attribute name="DetailReferenceID" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
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
        public static class VersionDetail {

            @XmlValue
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
            protected String value;
            @XmlAttribute(name = "DetailTypeID", required = true)
            @XmlSchemaType(name = "nonNegativeInteger")
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
            protected BigInteger detailTypeID;
            @XmlAttribute(name = "DetailReferenceID")
            @XmlSchemaType(name = "nonNegativeInteger")
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
            protected BigInteger detailReferenceID;
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
             * Gets the value of the detailTypeID property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
            public BigInteger getDetailTypeID() {
                return detailTypeID;
            }

            /**
             * Sets the value of the detailTypeID property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
            public void setDetailTypeID(BigInteger value) {
                this.detailTypeID = value;
            }

            /**
             * Gets the value of the detailReferenceID property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
            public BigInteger getDetailReferenceID() {
                return detailReferenceID;
            }

            /**
             * Sets the value of the detailReferenceID property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
            public void setDetailReferenceID(BigInteger value) {
                this.detailReferenceID = value;
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
         *       &lt;attribute name="LocationID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
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
        public static class VersionLocation {

            @XmlAttribute(name = "LocationID", required = true)
            @XmlSchemaType(name = "nonNegativeInteger")
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
            protected BigInteger locationID;
            @XmlAttribute(name = "DeltaAction")
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
            protected DeltaActionSchemaType deltaAction;

            /**
             * Gets the value of the locationID property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
            public BigInteger getLocationID() {
                return locationID;
            }

            /**
             * Sets the value of the locationID property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
            public void setLocationID(BigInteger value) {
                this.locationID = value;
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


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="IdentityID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
     *       &lt;attribute name="IdentityFeatureLinkTypeID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
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
    public static class IdentityReference {

        @XmlAttribute(name = "IdentityID", required = true)
        @XmlSchemaType(name = "nonNegativeInteger")
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        protected BigInteger identityID;
        @XmlAttribute(name = "IdentityFeatureLinkTypeID", required = true)
        @XmlSchemaType(name = "nonNegativeInteger")
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        protected BigInteger identityFeatureLinkTypeID;
        @XmlAttribute(name = "DeltaAction")
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        protected DeltaActionSchemaType deltaAction;

        /**
         * Gets the value of the identityID property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        public BigInteger getIdentityID() {
            return identityID;
        }

        /**
         * Sets the value of the identityID property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        public void setIdentityID(BigInteger value) {
            this.identityID = value;
        }

        /**
         * Gets the value of the identityFeatureLinkTypeID property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        public BigInteger getIdentityFeatureLinkTypeID() {
            return identityFeatureLinkTypeID;
        }

        /**
         * Sets the value of the identityFeatureLinkTypeID property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
        public void setIdentityFeatureLinkTypeID(BigInteger value) {
            this.identityFeatureLinkTypeID = value;
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
