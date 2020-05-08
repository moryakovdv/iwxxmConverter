package org.gamc.spmi.iwxxmConverter.airmetconverter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

import org.gamc.spmi.iwxxmConverter.common.CoordPoint;
import org.gamc.spmi.iwxxmConverter.common.Line;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.RUMB_UNITS;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.SPEED_UNITS;
import org.gamc.spmi.iwxxmConverter.tac.TacSectionImpl;
import org.joda.time.DateTime;

/**Description of sigmet's phenomenon forecasted time and position*/
public class AirmetMovingSection extends TacSectionImpl implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4229832662872170289L;

	public AirmetMovingSection(String initialTacSection) {
		super(initialTacSection);
		
	}

	private boolean isMoving = false;
	private RUMB_UNITS movingDirection;
	private int movingSpeed;
	private SPEED_UNITS speedUnits;

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
