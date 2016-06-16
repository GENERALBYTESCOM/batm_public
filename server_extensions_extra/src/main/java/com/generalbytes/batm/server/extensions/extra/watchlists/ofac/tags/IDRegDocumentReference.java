
package com.generalbytes.batm.server.extensions.extra.watchlists.ofac.tags;

import java.math.BigInteger;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="IDRegDocumentID" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
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
@XmlRootElement(name = "IDRegDocumentReference", namespace = "http://www.un.org/sanctions/1.0")
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
public class IDRegDocumentReference {

    @XmlAttribute(name = "IDRegDocumentID", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    protected BigInteger idRegDocumentID;
    @XmlAttribute(name = "DeltaAction")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    protected DeltaActionSchemaType deltaAction;

    /**
     * Gets the value of the idRegDocumentID property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public BigInteger getIDRegDocumentID() {
        return idRegDocumentID;
    }

    /**
     * Sets the value of the idRegDocumentID property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public void setIDRegDocumentID(BigInteger value) {
        this.idRegDocumentID = value;
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
