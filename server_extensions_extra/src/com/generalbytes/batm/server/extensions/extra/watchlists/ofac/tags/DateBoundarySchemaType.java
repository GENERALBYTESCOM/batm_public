
package com.generalbytes.batm.server.extensions.extra.watchlists.ofac.tags;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DateBoundarySchemaType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DateBoundarySchemaType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.un.org/sanctions/1.0}From"/>
 *           &lt;element ref="{http://www.un.org/sanctions/1.0}To" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;element ref="{http://www.un.org/sanctions/1.0}To"/>
 *       &lt;/choice>
 *       &lt;attribute name="Approximate" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="YearFixed" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="MonthFixed" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="DayFixed" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="DeltaAction" type="{http://www.un.org/sanctions/1.0}DeltaActionSchemaType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DateBoundarySchemaType", namespace = "http://www.un.org/sanctions/1.0", propOrder = {
    "content"
})
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
public class DateBoundarySchemaType {

    @XmlElementRefs({
        @XmlElementRef(name = "From", namespace = "http://www.un.org/sanctions/1.0", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "To", namespace = "http://www.un.org/sanctions/1.0", type = JAXBElement.class, required = false)
    })
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    protected List<JAXBElement<DatePointSchemaType>> content;
    @XmlAttribute(name = "Approximate", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    protected boolean approximate;
    @XmlAttribute(name = "YearFixed", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    protected boolean yearFixed;
    @XmlAttribute(name = "MonthFixed", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    protected boolean monthFixed;
    @XmlAttribute(name = "DayFixed", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    protected boolean dayFixed;
    @XmlAttribute(name = "DeltaAction")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    protected DeltaActionSchemaType deltaAction;

    /**
     * Gets the rest of the content model. 
     * 
     * <p>
     * You are getting this "catch-all" property because of the following reason: 
     * The field name "To" is used by two different parts of a schema. See: 
     * line 1228 of file:/home/b00lean/projects/batm/batm_main/server/src/com/generalbytes/batm/server/services/amlkyc/watchlist/ofac/tags/sdn_advanced.xsd
     * line 1226 of file:/home/b00lean/projects/batm/batm_main/server/src/com/generalbytes/batm/server/services/amlkyc/watchlist/ofac/tags/sdn_advanced.xsd
     * <p>
     * To get rid of this property, apply a property customization to one 
     * of both of the following declarations to change their names: 
     * Gets the value of the content property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the content property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link DatePointSchemaType }{@code >}
     * {@link JAXBElement }{@code <}{@link DatePointSchemaType }{@code >}
     * 
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public List<JAXBElement<DatePointSchemaType>> getContent() {
        if (content == null) {
            content = new ArrayList<JAXBElement<DatePointSchemaType>>();
        }
        return this.content;
    }

    /**
     * Gets the value of the approximate property.
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public boolean isApproximate() {
        return approximate;
    }

    /**
     * Sets the value of the approximate property.
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public void setApproximate(boolean value) {
        this.approximate = value;
    }

    /**
     * Gets the value of the yearFixed property.
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public boolean isYearFixed() {
        return yearFixed;
    }

    /**
     * Sets the value of the yearFixed property.
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public void setYearFixed(boolean value) {
        this.yearFixed = value;
    }

    /**
     * Gets the value of the monthFixed property.
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public boolean isMonthFixed() {
        return monthFixed;
    }

    /**
     * Sets the value of the monthFixed property.
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public void setMonthFixed(boolean value) {
        this.monthFixed = value;
    }

    /**
     * Gets the value of the dayFixed property.
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public boolean isDayFixed() {
        return dayFixed;
    }

    /**
     * Sets the value of the dayFixed property.
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
    public void setDayFixed(boolean value) {
        this.dayFixed = value;
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
