package org.gamc.spmi.iwxxmConverter.common;

import java.io.Serializable;

import org.gamc.spmi.gis.model.GTDirectionFromLine;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.RUMB_UNITS;

/**Describes direction from certain line, e.g 'NE OF LINE ...'*/	
public class DirectionFromLine implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1937563107841308842L;
	private RUMB_UNITS direction;
	private Line line;
	
	public DirectionFromLine() {};
	
	public DirectionFromLine(RUMB_UNITS azimuth, Line line) {
		this.direction=azimuth;
		this.line = line;
	}
	
	public RUMB_UNITS getDirection() {
		return direction;
	}
	public void setDirection(RUMB_UNITS direction) {
		this.direction = direction;
	}
	public Line getLine() {
		return line;
	}
	public void setLine(Line line) {
		this.line = line;
	}
	

	public DirectionFromLine withDirection(RUMB_UNITS direction) {
		this.setDirection(direction);
		return this;
	}
	
	public DirectionFromLine withLine(Line line) {
		this.setLine(line);
		return this;
	}
	
	public static DirectionFromLine build() {
		return new DirectionFromLine();
		
	}
	
	@Override
	public String toString() {
		return direction +" "+this.line.toString();
	}
	
	/**Convert to GTDirectionFromLine for GIS calculations*/
	public GTDirectionFromLine toGTDirectionFromLine() {
		return new GTDirectionFromLine(this.direction.name(), this.line.toGTLine());
	}
	
	
	
}
