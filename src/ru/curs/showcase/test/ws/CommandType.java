
package ru.curs.showcase.test.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for commandType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="commandType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="getDP"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "commandType")
@XmlEnum
public enum CommandType {

    @XmlEnumValue("getDP")
    GET_DP("getDP");
    private final String value;

    CommandType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CommandType fromValue(String v) {
        for (CommandType c: CommandType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
