//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.02.27 at 12:33:13 PM MSK 
//


package net.opengis.gml.v_3_2_1;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import _int.icao.iwxxm._2.AIRMETEvolvingConditionCollectionType;
import _int.icao.iwxxm._2.AIRMETEvolvingConditionType;
import _int.icao.iwxxm._2.ReportType;
import _int.icao.iwxxm._2.SIGMETEvolvingConditionCollectionType;
import _int.icao.iwxxm._2.SIGMETEvolvingConditionType;
import _int.icao.iwxxm._2.SIGMETPositionCollectionType;
import _int.icao.iwxxm._2.SIGMETPositionType;
import _int.icao.iwxxm._2.TropicalCycloneForecastConditionsType;
import _int.icao.iwxxm._2.TropicalCycloneObservedConditionsType;
import _int.icao.iwxxm._2.VolcanicAshCloudType;
import _int.icao.iwxxm._2.VolcanicAshConditionsType;
import _int.wmo.def.metce._2013.ProcessType;
import _int.wmo.def.metce._2013.TropicalCycloneType;
import _int.wmo.def.metce._2013.VolcanoType;
import net.opengis.om._2.OMObservationType;
import net.opengis.sampling._2.SFSamplingFeatureCollectionType;
import net.opengis.sampling._2.SFSamplingFeatureType;


/**
 * The basic feature model is given by the gml:AbstractFeatureType.
 * The content model for gml:AbstractFeatureType adds two specific properties suitable for geographic features to the content model defined in gml:AbstractGMLType. 
 * The value of the gml:boundedBy property describes an envelope that encloses the entire feature instance, and is primarily useful for supporting rapid searching for features that occur in a particular location. 
 * The value of the gml:location property describes the extent, position or relative location of the feature.
 * 
 * <p>Java class for AbstractFeatureType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractFeatureType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractGMLType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}boundedBy" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}location" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractFeatureType", propOrder = {
    "boundedBy",
    "location"
})
@XmlSeeAlso({
    SIGMETEvolvingConditionCollectionType.class,
    VolcanicAshConditionsType.class,
    SIGMETEvolvingConditionType.class,
    AIRMETEvolvingConditionType.class,
    TropicalCycloneObservedConditionsType.class,
    VolcanicAshCloudType.class,
    SIGMETPositionCollectionType.class,
    SIGMETPositionType.class,
    TropicalCycloneForecastConditionsType.class,
    ReportType.class,
    AIRMETEvolvingConditionCollectionType.class,
    ObservationType.class,
    AbstractCoverageType.class,
    AbstractFeatureCollectionType.class,
    BoundedFeatureType.class,
    DynamicFeatureType.class,
    OMObservationType.class,
    VolcanoType.class,
    ProcessType.class,
    TropicalCycloneType.class,
    SFSamplingFeatureType.class,
    SFSamplingFeatureCollectionType.class
})
public abstract class AbstractFeatureType
    extends AbstractGMLType
{

    @XmlElement(nillable = true)
    protected BoundingShapeType boundedBy;
    @XmlElementRef(name = "location", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class, required = false)
    protected JAXBElement<? extends LocationPropertyType> location;

    /**
     * Gets the value of the boundedBy property.
     * 
     * @return
     *     possible object is
     *     {@link BoundingShapeType }
     *     
     */
    public BoundingShapeType getBoundedBy() {
        return boundedBy;
    }

    /**
     * Sets the value of the boundedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link BoundingShapeType }
     *     
     */
    public void setBoundedBy(BoundingShapeType value) {
        this.boundedBy = value;
    }

    public boolean isSetBoundedBy() {
        return (this.boundedBy!= null);
    }

    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link LocationPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PriorityLocationPropertyType }{@code >}
     *     
     */
    public JAXBElement<? extends LocationPropertyType> getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link LocationPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PriorityLocationPropertyType }{@code >}
     *     
     */
    public void setLocation(JAXBElement<? extends LocationPropertyType> value) {
        this.location = value;
    }

    public boolean isSetLocation() {
        return (this.location!= null);
    }

}
