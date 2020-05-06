package org.gamc.spmi.iwxxmConverter.sigmetconverter;

import java.io.Serializable;

import org.gamc.spmi.iwxxmConverter.iwxxmenums.IwxxmEnum;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.RUMB_UNITS;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.SPEED_UNITS;
import org.gamc.spmi.iwxxmConverter.tac.TacSection;
import org.gamc.spmi.iwxxmConverter.tac.TacSectionImpl;
import org.joda.time.DateTime;

/** Description of meteorological phenomenon in the SIGMET */
public class SigmetPhenomenonDescription extends TacSectionImpl implements Serializable {

	public SigmetPhenomenonDescription(String initialTacSection) {
		super(initialTacSection);

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7906212608215801464L;

	public enum Severity implements IwxxmEnum {
		ISOL("ISOL"), OBSC("OBSC"), SQL("SQL"), EMBD("EMBD"), FRQ("FRQ"), SEV("SEV"),
		HVY("HVY"), NOTSET("");

		private String name;

		private Severity(String strName) {
			this.name = strName;
		}

		@Override
		public String getStringValue() {
			return name;
		}
	}

	public enum ObservationType implements IwxxmEnum  {
		FCST("FCST"), OBS("OBS"), NOTSET("");

		private String name;

		private ObservationType(String strName) {
			this.name=strName;
		}

		@Override
		public String getStringValue() {
			return name;
		}
	}
	
	public enum Intensity implements IwxxmEnum  {
		INTSF("INTSF"), WKN("WKN"),NC("NC");
		private String name;

		private Intensity(String strName) {
			this.name=strName;
		}

		@Override
		public String getStringValue() {
			return name;
		}
	}

	private String phenomenon;
	private Severity phenomenonSeverity = Severity.NOTSET;
	private ObservationType phenomenonObservation = ObservationType.NOTSET;
	private DateTime phenomenonTimeStamp;

	

	private Intensity intencity = Intensity.NC;
	
	private SigmetMovingSection movingSection;
	private SigmetForecastSection forecastSection;
	
	public String getPhenomenon() {
		return phenomenon;
	}

	public void setPhenomenon(String phenomenon) {
		this.phenomenon = phenomenon;
	}

	public Severity getPhenomenonSeverity() {
		return phenomenonSeverity;
	}

	public void setPhenomenonSeverity(Severity phenomenonSeverity) {
		this.phenomenonSeverity = phenomenonSeverity;
	}

	public ObservationType getPhenomenonObservation() {
		return phenomenonObservation;
	}

	public void setPhenomenonObservation(ObservationType phenomenonObservation) {
		this.phenomenonObservation = phenomenonObservation;
	}

	public DateTime getPhenomenonTimeStamp() {
		return phenomenonTimeStamp;
	}

	public void setPhenomenonTimeStamp(DateTime phenomenonTimeStamp) {
		this.phenomenonTimeStamp = phenomenonTimeStamp;
	}

	

	public SigmetForecastSection getForecastSection() {
		return forecastSection;
	}

	public void setForecastSection(SigmetForecastSection forecastSection) {
		this.forecastSection = forecastSection;
	}

	public Intensity getIntencity() {
		return intencity;
	}

	public void setIntencity(Intensity intencity) {
		this.intencity = intencity;
	}

	public SigmetMovingSection getMovingSection() {
		return movingSection;
	}

	public void setMovingSection(SigmetMovingSection movingSection) {
		this.movingSection = movingSection;
	}
	
	/**returns string to find the correcsponding link in thw WMO registry*/
	public String getPhenomenonForLink() {
		if (this.phenomenonSeverity.equals(Severity.NOTSET)) {
			return this.phenomenon;
		}
		else
			return this.phenomenonSeverity.name+"_"+this.phenomenon;
	}

}
