
package com.generalbytes.batm.server.extensions.extra.watchlists.ofac.tags;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DeltaActionSchemaType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DeltaActionSchemaType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="add"/>
 *     &lt;enumeration value="amend"/>
 *     &lt;enumeration value="delete"/>
 *     &lt;enumeration value="revise"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DeltaActionSchemaType", namespace = "http://www.un.org/sanctions/1.0")
@XmlEnum
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
public enum DeltaActionSchemaType {

    @XmlEnumValue("add")
    ADD("add"),
    @XmlEnumValue("amend")
    AMEND("amend"),
    @XmlEnumValue("delete")
    DELETE("delete"),
    @XmlEnumValue("revise")
    REVISE("revise");
    private final String value;

    DeltaActionSchemaType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DeltaActionSchemaType fromValue(String v) {
        for (DeltaActionSchemaType c: DeltaActionSchemaType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
