package org.gamc.spmi.iwxxmConverter.sigmetconverter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;

import org.gamc.spmi.iwxxmConverter.common.CoordPoint;
import org.gamc.spmi.iwxxmConverter.common.Line;
import org.gamc.spmi.iwxxmConverter.iwxxmenums.RUMB_UNITS;


/**Describes location of the sigmet phenomenon inside FIR/UIR/CTA*/
public class SigmetHorizontalPhenomenonLocation {

	
	/**Describes direction from certain line, e.g 'NE OF LINE ...'*/	
	public static final class DirectionFromLine implements Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1937563107841308842L;
		private RUMB_UNITS direction;
		private Line sigmetLine;
		
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
	
	/**Phenomenon reported for entire FIR/UIR/CTA*/
	private boolean entireFIR = false;
	
	/**Phenomenon reported for polygon inside (WI token)*/
	private boolean inPolygon = false;
	
	/**Phenomenon reported for aisle(corridor) at both sides of certain line (e.g. WTN 45 NM OF LINE ...)*/
	private boolean withinCorridor = false;
	
	/**Wideness of the aisle(corridor) or radius from certain line*/
	private int wideness;
	
	/**Vertexes of polygon*/
	private LinkedList<CoordPoint> polygonPoints = new LinkedList<>();
	
	private LinkedList<DirectionFromLine> directionsFromLines = new LinkedList<>();
	
	
}
