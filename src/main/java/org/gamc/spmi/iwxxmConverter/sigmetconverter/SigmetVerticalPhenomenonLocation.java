package org.gamc.spmi.iwxxmConverter.sigmetconverter;

import java.util.Optional;

/**Describes vertical location of the sigmet phenomenon e.g. from surface to FL or between two FLs*/
public class SigmetVerticalPhenomenonLocation {

	private boolean topMarginAboveFl = false;
	private boolean bottomMarginOnSurface = false;
	
	private Optional<Integer> bottomFL;
	private Optional<Integer> topFL;
	private Optional<Integer> topMarginMeters;
	
	
}
