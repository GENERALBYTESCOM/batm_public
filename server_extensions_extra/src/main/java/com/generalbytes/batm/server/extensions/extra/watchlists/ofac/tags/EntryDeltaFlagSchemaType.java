
package com.generalbytes.batm.server.extensions.extra.watchlists.ofac.tags;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EntryDeltaFlagSchemaType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EntryDeltaFlagSchemaType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NewEntry"/>
 *     &lt;enumeration value="DeletedEntry"/>
 *     &lt;enumeration value="ModifiedEntry"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EntryDeltaFlagSchemaType", namespace = "http://www.un.org/sanctions/1.0")
@XmlEnum
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-02-08T09:26:29+01:00", comments = "JAXB RI v2.2.4-2")
public enum EntryDeltaFlagSchemaType {

    @XmlEnumValue("NewEntry")
    NEW_ENTRY("NewEntry"),
    @XmlEnumValue("DeletedEntry")
    DELETED_ENTRY("DeletedEntry"),
    @XmlEnumValue("ModifiedEntry")
    MODIFIED_ENTRY("ModifiedEntry");
    private final String value;

    EntryDeltaFlagSchemaType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EntryDeltaFlagSchemaType fromValue(String v) {
        for (EntryDeltaFlagSchemaType c: EntryDeltaFlagSchemaType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
