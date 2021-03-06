//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.02.27 at 12:41:52 PM MSK 
//


package schemabindings21._int.icao.iwxxm._2;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import schemabindings21.aero.aixm.schema._5_1.AirspaceVolumePropertyType;
import schemabindings21.net.opengis.gml.v_3_2_1.AbstractFeatureType;
import schemabindings21.net.opengis.gml.v_3_2_1.SpeedType;


/**
 * <p>Java class for SIGMETEvolvingConditionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SIGMETEvolvingConditionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractFeatureType">
 *       &lt;sequence>
 *         &lt;element name="directionOfMotion" type="{http://icao.int/iwxxm/2.1}AngleWithNilReasonType" minOccurs="0"/>
 *         &lt;element name="geometry" type="{http://www.aixm.aero/schema/5.1.1}AirspaceVolumePropertyType"/>
 *         &lt;element name="geometryLowerLimitOperator" type="{http://icao.int/iwxxm/2.1}RelationalOperatorType" minOccurs="0"/>
 *         &lt;element name="geometryUpperLimitOperator" type="{http://icao.int/iwxxm/2.1}RelationalOperatorType" minOccurs="0"/>
 *         &lt;element name="speedOfMotion" type="{http://www.opengis.net/gml/3.2}SpeedType" minOccurs="0"/>
 *         &lt;element name="extension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="intensityChange" type="{http://icao.int/iwxxm/2.1}ExpectedIntensityChangeType" />
 *       &lt;attribute name="approximateLocation" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SIGMETEvolvingConditionType", propOrder = {
    "directionOfMotion",
    "geometry",
    "geometryLowerLimitOperator",
    "geometryUpperLimitOperator",
    "speedOfMotion",
    "extension"
})
public class SIGMETEvolvingConditionType
    extends AbstractFeatureType
{

    @XmlElementRef(name = "directionOfMotion", namespace = "http://icao.int/iwxxm/2.1", type = JAXBElement.class, required = false)
    protected JAXBElement<AngleWithNilReasonType> directionOfMotion;
    @XmlElement(required = true)
    protected AirspaceVolumePropertyType geometry;
    @XmlSchemaType(name = "string")
    protected RelationalOperatorType geometryLowerLimitOperator;
    @XmlSchemaType(name = "string")
    protected RelationalOperatorType geometryUpperLimitOperator;
    protected SpeedType speedOfMotion;
    protected List<Object> extension;
    @XmlAttribute(name = "intensityChange")
    protected ExpectedIntensityChangeType intensityChange;
    @XmlAttribute(name = "approximateLocation")
    protected Boolean approximateLocation;

    /**
     * Gets the value of the directionOfMotion property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AngleWithNilReasonType }{@code >}
     *     
     */
    public JAXBElement<AngleWithNilReasonType> getDirectionOfMotion() {
        return directionOfMotion;
    }

    /**
     * Sets the value of the directionOfMotion property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AngleWithNilReasonType }{@code >}
     *     
     */
    public void setDirectionOfMotion(JAXBElement<AngleWithNilReasonType> value) {
        this.directionOfMotion = value;
    }

    public boolean isSetDirectionOfMotion() {
        return (this.directionOfMotion!= null);
    }

    /**
     * Gets the value of the geometry property.
     * 
     * @return
     *     possible object is
     *     {@link AirspaceVolumePropertyType }
     *     
     */
    public AirspaceVolumePropertyType getGeometry() {
        return geometry;
    }

    /**
     * Sets the value of the geometry property.
     * 
     * @param value
     *     allowed object is
     *     {@link AirspaceVolumePropertyType }
     *     
     */
    public void setGeometry(AirspaceVolumePropertyType value) {
        this.geometry = value;
    }

    public boolean isSetGeometry() {
        return (this.geometry!= null);
    }

    /**
     * Gets the value of the geometryLowerLimitOperator property.
     * 
     * @return
     *     possible object is
     *     {@link RelationalOperatorType }
     *     
     */
    public RelationalOperatorType getGeometryLowerLimitOperator() {
        return geometryLowerLimitOperator;
    }

    /**
     * Sets the value of the geometryLowerLimitOperator property.
     * 
     * @param value
     *     allowed object is
     *     {@link RelationalOperatorType }
     *     
     */
    public void setGeometryLowerLimitOperator(RelationalOperatorType value) {
        this.geometryLowerLimitOperator = value;
    }

    public boolean isSetGeometryLowerLimitOperator() {
        return (this.geometryLowerLimitOperator!= null);
    }

    /**
     * Gets the value of the geometryUpperLimitOperator property.
     * 
     * @return
     *     possible object is
     *     {@link RelationalOperatorType }
     *     
     */
    public RelationalOperatorType getGeometryUpperLimitOperator() {
        return geometryUpperLimitOperator;
    }

    /**
     * Sets the value of the geometryUpperLimitOperator property.
     * 
     * @param value
     *     allowed object is
     *     {@link RelationalOperatorType }
     *     
     */
    public void setGeometryUpperLimitOperator(RelationalOperatorType value) {
        this.geometryUpperLimitOperator = value;
    }

    public boolean isSetGeometryUpperLimitOperator() {
        return (this.geometryUpperLimitOperator!= null);
    }

    /**
     * Gets the value of the speedOfMotion property.
     * 
     * @return
     *     possible object is
     *     {@link SpeedType }
     *     
     */
    public SpeedType getSpeedOfMotion() {
        return speedOfMotion;
    }

    /**
     * Sets the value of the speedOfMotion property.
     * 
     * @param value
     *     allowed object is
     *     {@link SpeedType }
     *     
     */
    public void setSpeedOfMotion(SpeedType value) {
        this.speedOfMotion = value;
    }

    public boolean isSetSpeedOfMotion() {
        return (this.speedOfMotion!= null);
    }

    /**
     * Gets the value of the extension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the extension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getExtension() {
        if (extension == null) {
            extension = new ArrayList<Object>();
        }
        return this.extension;
    }

    public boolean isSetExtension() {
        return ((this.extension!= null)&&(!this.extension.isEmpty()));
    }

    public void unsetExtension() {
        this.extension = null;
    }

    /**
     * Gets the value of the intensityChange property.
     * 
     * @return
     *     possible object is
     *     {@link ExpectedIntensityChangeType }
     *     
     */
    public ExpectedIntensityChangeType getIntensityChange() {
        return intensityChange;
    }

    /**
     * Sets the value of the intensityChange property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExpectedIntensityChangeType }
     *     
     */
    public void setIntensityChange(ExpectedIntensityChangeType value) {
        this.intensityChange = value;
    }

    public boolean isSetIntensityChange() {
        return (this.intensityChange!= null);
    }

    /**
     * Gets the value of the approximateLocation property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isApproximateLocation() {
        return approximateLocation;
    }

    /**
     * Sets the value of the approximateLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setApproximateLocation(boolean value) {
        this.approximateLocation = value;
    }

    public boolean isSetApproximateLocation() {
        return (this.approximateLocation!= null);
    }

    public void unsetApproximateLocation() {
        this.approximateLocation = null;
    }

}
