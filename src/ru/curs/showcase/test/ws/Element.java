
package ru.curs.showcase.test.ws;

import java.math.BigInteger;
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
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="hideOnLoad" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="neverShowInPanel" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="proc" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="template" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="transform" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="cacheData" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="showLoadingMessage" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="refreshByTimer" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="refreshInterval" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *       &lt;attribute name="style" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="styleClass" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "element")
public class Element {

    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "type", required = true)
    protected String type;
    @XmlAttribute(name = "hideOnLoad")
    protected Boolean hideOnLoad;
    @XmlAttribute(name = "neverShowInPanel")
    protected Boolean neverShowInPanel;
    @XmlAttribute(name = "proc")
    protected String proc;
    @XmlAttribute(name = "template")
    protected String template;
    @XmlAttribute(name = "transform")
    protected String transform;
    @XmlAttribute(name = "cacheData")
    protected Boolean cacheData;
    @XmlAttribute(name = "showLoadingMessage")
    protected Boolean showLoadingMessage;
    @XmlAttribute(name = "refreshByTimer")
    protected Boolean refreshByTimer;
    @XmlAttribute(name = "refreshInterval")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger refreshInterval;
    @XmlAttribute(name = "style")
    protected String style;
    @XmlAttribute(name = "styleClass")
    protected String styleClass;

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
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the hideOnLoad property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isHideOnLoad() {
        return hideOnLoad;
    }

    /**
     * Sets the value of the hideOnLoad property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setHideOnLoad(Boolean value) {
        this.hideOnLoad = value;
    }

    /**
     * Gets the value of the neverShowInPanel property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNeverShowInPanel() {
        return neverShowInPanel;
    }

    /**
     * Sets the value of the neverShowInPanel property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNeverShowInPanel(Boolean value) {
        this.neverShowInPanel = value;
    }

    /**
     * Gets the value of the proc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProc() {
        return proc;
    }

    /**
     * Sets the value of the proc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProc(String value) {
        this.proc = value;
    }

    /**
     * Gets the value of the template property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Sets the value of the template property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTemplate(String value) {
        this.template = value;
    }

    /**
     * Gets the value of the transform property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransform() {
        return transform;
    }

    /**
     * Sets the value of the transform property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransform(String value) {
        this.transform = value;
    }

    /**
     * Gets the value of the cacheData property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCacheData() {
        return cacheData;
    }

    /**
     * Sets the value of the cacheData property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCacheData(Boolean value) {
        this.cacheData = value;
    }

    /**
     * Gets the value of the showLoadingMessage property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowLoadingMessage() {
        return showLoadingMessage;
    }

    /**
     * Sets the value of the showLoadingMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowLoadingMessage(Boolean value) {
        this.showLoadingMessage = value;
    }

    /**
     * Gets the value of the refreshByTimer property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRefreshByTimer() {
        return refreshByTimer;
    }

    /**
     * Sets the value of the refreshByTimer property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRefreshByTimer(Boolean value) {
        this.refreshByTimer = value;
    }

    /**
     * Gets the value of the refreshInterval property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRefreshInterval() {
        return refreshInterval;
    }

    /**
     * Sets the value of the refreshInterval property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRefreshInterval(BigInteger value) {
        this.refreshInterval = value;
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

}
