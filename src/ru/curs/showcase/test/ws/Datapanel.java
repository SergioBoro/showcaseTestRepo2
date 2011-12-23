
package ru.curs.showcase.test.ws;

import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element name="tab" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://server.app.showcase.curs.ru/}element" maxOccurs="unbounded" minOccurs="0"/>
 *                   &lt;element ref="{http://server.app.showcase.curs.ru/}tr" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="layout" type="{http://server.app.showcase.curs.ru/}ElementLayoutType" />
 *                 &lt;attribute name="style" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="styleClass" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
    "tab"
})
@XmlRootElement(name = "datapanel")
public class Datapanel {

    @XmlElement(namespace = "", required = true)
    protected List<Datapanel.Tab> tab;

    /**
     * Gets the value of the tab property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tab property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTab().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Datapanel.Tab }
     * 
     * 
     */
    public List<Datapanel.Tab> getTab() {
        if (tab == null) {
            tab = new ArrayList<Datapanel.Tab>();
        }
        return this.tab;
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
     *         &lt;element ref="{http://server.app.showcase.curs.ru/}element" maxOccurs="unbounded" minOccurs="0"/>
     *         &lt;element ref="{http://server.app.showcase.curs.ru/}tr" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *       &lt;attribute name="layout" type="{http://server.app.showcase.curs.ru/}ElementLayoutType" />
     *       &lt;attribute name="style" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="styleClass" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "element",
        "tr"
    })
    public static class Tab {

        protected List<Element> element;
        protected List<Tr> tr;
        @XmlAttribute(name = "layout")
        protected ElementLayoutType layout;
        @XmlAttribute(name = "style")
        protected String style;
        @XmlAttribute(name = "styleClass")
        protected String styleClass;
        @XmlAttribute(name = "id", required = true)
        protected String id;
        @XmlAttribute(name = "name", required = true)
        protected String name;

        /**
         * Gets the value of the element property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the element property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getElement().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Element }
         * 
         * 
         */
        public List<Element> getElement() {
            if (element == null) {
                element = new ArrayList<Element>();
            }
            return this.element;
        }

        /**
         * Gets the value of the tr property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the tr property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getTr().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Tr }
         * 
         * 
         */
        public List<Tr> getTr() {
            if (tr == null) {
                tr = new ArrayList<Tr>();
            }
            return this.tr;
        }

        /**
         * Gets the value of the layout property.
         * 
         * @return
         *     possible object is
         *     {@link ElementLayoutType }
         *     
         */
        public ElementLayoutType getLayout() {
            return layout;
        }

        /**
         * Sets the value of the layout property.
         * 
         * @param value
         *     allowed object is
         *     {@link ElementLayoutType }
         *     
         */
        public void setLayout(ElementLayoutType value) {
            this.layout = value;
        }

        /**
         * Gets the value of the style property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getStyle() {
            return style;
        }

        /**
         * Sets the value of the style property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setStyle(String value) {
            this.style = value;
        }

        /**
         * Gets the value of the styleClass property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getStyleClass() {
            return styleClass;
        }

        /**
         * Sets the value of the styleClass property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setStyleClass(String value) {
            this.styleClass = value;
        }

        /**
         * Gets the value of the id property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getId() {
            return id;
        }

        /**
         * Sets the value of the id property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setId(String value) {
            this.id = value;
        }

        /**
         * Gets the value of the name property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the value of the name property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setName(String value) {
            this.name = value;
        }

    }

}
