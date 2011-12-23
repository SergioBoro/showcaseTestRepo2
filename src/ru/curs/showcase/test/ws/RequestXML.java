
package ru.curs.showcase.test.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
 *       &lt;sequence>
 *         &lt;element name="procName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="command">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://server.app.showcase.curs.ru/}context"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="type" use="required" type="{http://server.app.showcase.curs.ru/}commandType" />
 *                 &lt;attribute name="param" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element ref="{http://server.app.showcase.curs.ru/}context"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "procName",
    "command",
    "context"
})
@XmlRootElement(name = "requestXML")
public class RequestXML {

    @XmlElement(namespace = "", required = true)
    protected String procName;
    @XmlElement(namespace = "", required = true)
    protected RequestXML.Command command;
    @XmlElement(required = true)
    protected Context context;

    /**
     * Gets the value of the procName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcName() {
        return procName;
    }

    /**
     * Sets the value of the procName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcName(String value) {
        this.procName = value;
    }

    /**
     * Gets the value of the command property.
     * 
     * @return
     *     possible object is
     *     {@link RequestXML.Command }
     *     
     */
    public RequestXML.Command getCommand() {
        return command;
    }

    /**
     * Sets the value of the command property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestXML.Command }
     *     
     */
    public void setCommand(RequestXML.Command value) {
        this.command = value;
    }

    /**
     * Gets the value of the context property.
     * 
     * @return
     *     possible object is
     *     {@link Context }
     *     
     */
    public Context getContext() {
        return context;
    }

    /**
     * Sets the value of the context property.
     * 
     * @param value
     *     allowed object is
     *     {@link Context }
     *     
     */
    public void setContext(Context value) {
        this.context = value;
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
     *         &lt;element ref="{http://server.app.showcase.curs.ru/}context"/>
     *       &lt;/sequence>
     *       &lt;attribute name="type" use="required" type="{http://server.app.showcase.curs.ru/}commandType" />
     *       &lt;attribute name="param" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "context"
    })
    public static class Command {

        @XmlElement(required = true)
        protected Context context;
        @XmlAttribute(name = "type", required = true)
        protected CommandType type;
        @XmlAttribute(name = "param")
        protected String param;

        /**
         * Gets the value of the context property.
         * 
         * @return
         *     possible object is
         *     {@link Context }
         *     
         */
        public Context getContext() {
            return context;
        }

        /**
         * Sets the value of the context property.
         * 
         * @param value
         *     allowed object is
         *     {@link Context }
         *     
         */
        public void setContext(Context value) {
            this.context = value;
        }

        /**
         * Gets the value of the type property.
         * 
         * @return
         *     possible object is
         *     {@link CommandType }
         *     
         */
        public CommandType getType() {
            return type;
        }

        /**
         * Sets the value of the type property.
         * 
         * @param value
         *     allowed object is
         *     {@link CommandType }
         *     
         */
        public void setType(CommandType value) {
            this.type = value;
        }

        /**
         * Gets the value of the param property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getParam() {
            return param;
        }

        /**
         * Sets the value of the param property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setParam(String value) {
            this.param = value;
        }

    }

}
