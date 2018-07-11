package org.gamc.spmi.iwxxmConverter.sigmetconverter;

import java.io.Serializable;


import org.gamc.spmi.iwxxmConverter.iwxxmenums.RUMB_UNITS;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.SPEED_UNITS;
import org.joda.time.DateTime;

/**Description of meteorological phenomenon in the SIGMET*/
public class SigmetPhenomenonDescription implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7906212608215801464L;
	
	
	
	public enum Severity {
		ISOLATED,OBSCURED,SQALL,EMBEDDED,FREQUENT,SEVERE,HEAVY, NOTSET;
	}
	
	public enum ObservationType {
		FORECAST, OBSERVE, NOTSET;
	}
	
	
	
	private String phenomenon;
	private Severity phenomenonSeverity=Severity.NOTSET;
	private ObservationType phenomenonObservation=ObservationType.NOTSET;
	private DateTime phenomenonTimeStamp;
	
	private boolean isMoving=false;
	private RUMB_UNITS  movingDirection;
	private int movingSpeed;
	private SPEED_UNITS speedUnits;
}
