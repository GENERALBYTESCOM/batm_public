
package com.generalbytes.batm.server.extensions.extra.watchlists.ofac.tags;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReferenceSchemaType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ReferenceSchemaType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="RefersTo"/>
 *     &lt;enumeration value="ReferencedBy"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ReferenceSchemaType", namespace = "http://www.un.org/sanctions/1.0")
@XmlEnum
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
public enum ReferenceSchemaType {

    @XmlEnumValue("RefersTo")
    REFERS_TO("RefersTo"),
    @XmlEnumValue("ReferencedBy")
    REFERENCED_BY("ReferencedBy");
    private final String value;

    ReferenceSchemaType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ReferenceSchemaType fromValue(String v) {
        for (ReferenceSchemaType c: ReferenceSchemaType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
