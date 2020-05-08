package org.gamc.spmi.iwxxmConverter.airmetconverter;

import java.util.Optional;

import org.gamc.spmi.iwxxmConverter.iwxxmenums.LENGTH_UNITS;

/**Describes vertical location of the sigmet phenomenon e.g. from surface to FL or between two FLs*/
public class AirmetVerticalPhenomenonLocation {

	private boolean topMarginAboveFl = false;
	private boolean topMarginBelowFl = false;
	private boolean bottomMarginOnSurface = false;
	
	private Optional<Integer> bottomFL = Optional.empty();
	private Optional<Integer> topFL= Optional.empty();
	private Optional<Integer> topMarginMeters= Optional.empty();
	
	private LENGTH_UNITS units = LENGTH_UNITS.M;
	
	public boolean isTopMarginAboveFl() {
		return topMarginAboveFl;
	}
	public void setTopMarginAboveFl(boolean topMarginAboveFl) {
		this.topMarginAboveFl = topMarginAboveFl;
	}
	public boolean isBottomMarginOnSurface() {
		return bottomMarginOnSurface;
	}
	public void setBottomMarginOnSurface(boolean bottomMarginOnSurface) {
		this.bottomMarginOnSurface = bottomMarginOnSurface;
	}
	public Optional<Integer> getBottomFL() {
		return bottomFL;
	}
	public void setBottomFL(Optional<Integer> bottomFL) {
		this.bottomFL = bottomFL;
	}
	public Optional<Integer> getTopFL() {
		return topFL;
	}
	public void setTopFL(Optional<Integer> topFL) {
		this.topFL = topFL;
	}
	public Optional<Integer> getTopMarginMeters() {
		return topMarginMeters;
	}
	public void setTopMarginMeters(Optional<Integer> topMarginMeters) {
		this.topMarginMeters = topMarginMeters;
	}
	public LENGTH_UNITS getUnits() {
		return units;
	}
	public void setUnits(LENGTH_UNITS units) {
		this.units = units;
	}
	public boolean isTopMarginBelowFl() {
		return topMarginBelowFl;
	}
	public void setTopMarginBelowFl(boolean topMarginBelowFl) {
		this.topMarginBelowFl = topMarginBelowFl;
	}
	
	
}
