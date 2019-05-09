package org.gamc.spmi.iwxxmConverter.sigmetconverter;

import java.io.Serializable;

import org.gamc.spmi.iwxxmConverter.common.Line;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.RUMB_UNITS;

/**Describes direction from certain line, e.g 'NE OF LINE ...'*/	
public class DirectionFromLine implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1937563107841308842L;
	private RUMB_UNITS direction;
	private Line sigmetLine;
	
	public DirectionFromLine(RUMB_UNITS azimuth, Line line) {
		this.direction=azimuth;
		this.sigmetLine = line;
	}
	
	public RUMB_UNITS getDirection() {
		return direction;
	}
	public void setDirection(RUMB_UNITS direction) {
		this.direction = direction;
	}
	public Line getSigmetLine() {
		return sigmetLine;
	}
	public void setSigmetLine(Line sigmetLine) {
		this.sigmetLine = sigmetLine;
	}
	
	
}
