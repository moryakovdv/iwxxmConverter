package org.gamc.spmi.iwxxmConverter.wmo;

/**Class describes WMO registry entry to unify parsing and accessing to WMO registres*/
public class WMORegisterDescription {

	public WMORegisterDescription(String wmoUrl) {
		super();
		this.wmoUrl = wmoUrl;
	}

	public WMORegisterDescription(String wmoUrl, String description) {
		super();
		this.wmoUrl = wmoUrl;
		this.description = description;
	}
	
	public WMORegisterDescription(String wmoUrl, String description,String relatedUrl, String label) {
		super();
		this.wmoUrl = wmoUrl;
		this.description = description;
		this.relatedUrl=relatedUrl;
		this.label=label;
	}

	private String wmoUrl;
	private String relatedUrl;
	private String description;
	private String label;

	public String getWmoUrl() {
		return wmoUrl;
	}

	public String getRelatedUrl() {
		return relatedUrl;
	}

	public String getDescription() {
		return description;
	}

	public String getLabel() {
		return label;
	}

	public void setWmoUrl(String wmoUrl) {
		this.wmoUrl = wmoUrl;
	}

	public void setRelatedUrl(String relatedUrl) {
		this.relatedUrl = relatedUrl;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
