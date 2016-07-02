
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


/**
 * <p>Java class for ProfileRelationshipSchemaType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProfileRelationshipSchemaType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.un.org/sanctions/1.0}Comment" minOccurs="0"/>
 *         &lt;element ref="{http://www.un.org/sanctions/1.0}DatePeriod" minOccurs="0"/>
 *         &lt;element ref="{http://www.un.org/sanctions/1.0}IDRegDocumentReference" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="ID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="From-ProfileID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="To-ProfileID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="RelationTypeID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="RelationQualityID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="Former" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="SanctionsEntryID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="DeltaAction" type="{http://www.un.org/sanctions/1.0}DeltaActionSchemaType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProfileRelationshipSchemaType", namespace = "http://www.un.org/sanctions/1.0", propOrder = {
    "comment",
    "datePeriod",
    "idRegDocumentReference"
})
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
public class ProfileRelationshipSchemaType {

    @XmlElement(name = "Comment", namespace = "http://www.un.org/sanctions/1.0")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    protected Comment comment;
    @XmlElement(name = "DatePeriod", namespace = "http://www.un.org/sanctions/1.0")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    protected DatePeriod datePeriod;
    @XmlElement(name = "IDRegDocumentReference", namespace = "http://www.un.org/sanctions/1.0")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    protected List<IDRegDocumentReference> idRegDocumentReference;
    @XmlAttribute(name = "ID", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    protected BigInteger id;
    @XmlAttribute(name = "From-ProfileID", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    protected BigInteger fromProfileID;
    @XmlAttribute(name = "To-ProfileID", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    protected BigInteger toProfileID;
    @XmlAttribute(name = "RelationTypeID", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    protected BigInteger relationTypeID;
    @XmlAttribute(name = "RelationQualityID", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    protected BigInteger relationQualityID;
    @XmlAttribute(name = "Former", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    protected boolean former;
    @XmlAttribute(name = "SanctionsEntryID", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    protected BigInteger sanctionsEntryID;
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
     * @return
     *     possible object is
     *     {@link DatePeriod }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public DatePeriod getDatePeriod() {
        return datePeriod;
    }

    /**
     * Sets the value of the datePeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link DatePeriod }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public void setDatePeriod(DatePeriod value) {
        this.datePeriod = value;
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
     * Gets the value of the fromProfileID property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public BigInteger getFromProfileID() {
        return fromProfileID;
    }

    /**
     * Sets the value of the fromProfileID property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public void setFromProfileID(BigInteger value) {
        this.fromProfileID = value;
    }

    /**
     * Gets the value of the toProfileID property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public BigInteger getToProfileID() {
        return toProfileID;
    }

    /**
     * Sets the value of the toProfileID property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public void setToProfileID(BigInteger value) {
        this.toProfileID = value;
    }

    /**
     * Gets the value of the relationTypeID property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public BigInteger getRelationTypeID() {
        return relationTypeID;
    }

    /**
     * Sets the value of the relationTypeID property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public void setRelationTypeID(BigInteger value) {
        this.relationTypeID = value;
    }

    /**
     * Gets the value of the relationQualityID property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public BigInteger getRelationQualityID() {
        return relationQualityID;
    }

    /**
     * Sets the value of the relationQualityID property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public void setRelationQualityID(BigInteger value) {
        this.relationQualityID = value;
    }

    /**
     * Gets the value of the former property.
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public boolean isFormer() {
        return former;
    }

    /**
     * Sets the value of the former property.
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public void setFormer(boolean value) {
        this.former = value;
    }

    /**
     * Gets the value of the sanctionsEntryID property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public BigInteger getSanctionsEntryID() {
        return sanctionsEntryID;
    }

    /**
     * Sets the value of the sanctionsEntryID property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public void setSanctionsEntryID(BigInteger value) {
        this.sanctionsEntryID = value;
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
