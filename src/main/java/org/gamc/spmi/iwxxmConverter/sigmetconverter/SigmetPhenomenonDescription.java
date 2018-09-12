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
		ISOL("ISOL"), OBS("OBSC"), SQL("SQL"), EMBD("EMBD"), FRQ("FRQ"), SEV("SEV"),
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

	private String phenomenon;
	private Severity phenomenonSeverity = Severity.NOTSET;
	private ObservationType phenomenonObservation = ObservationType.NOTSET;
	private DateTime phenomenonTimeStamp;

	private boolean isMoving = false;
	private RUMB_UNITS movingDirection;
	private int movingSpeed;
	private SPEED_UNITS speedUnits;

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

	public boolean isMoving() {
		return isMoving;
	}

	public void setMoving(boolean isMoving) {
		this.isMoving = isMoving;
	}

	public RUMB_UNITS getMovingDirection() {
		return movingDirection;
	}

	public void setMovingDirection(RUMB_UNITS movingDirection) {
		this.movingDirection = movingDirection;
	}

	public int getMovingSpeed() {
		return movingSpeed;
	}

	public void setMovingSpeed(int movingSpeed) {
		this.movingSpeed = movingSpeed;
	}

	public SPEED_UNITS getSpeedUnits() {
		return speedUnits;
	}

	public void setSpeedUnits(SPEED_UNITS speedUnits) {
		this.speedUnits = speedUnits;
	}

}
