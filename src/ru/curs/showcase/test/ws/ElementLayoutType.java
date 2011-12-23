
package ru.curs.showcase.test.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ElementLayoutType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ElementLayoutType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="VERTICAL"/>
 *     &lt;enumeration value="TABLE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ElementLayoutType")
@XmlEnum
public enum ElementLayoutType {

    VERTICAL,
    TABLE;

    public String value() {
        return name();
    }

    public static ElementLayoutType fromValue(String v) {
        return valueOf(v);
    }

}
