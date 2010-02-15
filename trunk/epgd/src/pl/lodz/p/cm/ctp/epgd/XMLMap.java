package pl.lodz.p.cm.ctp.epgd;

import java.io.Serializable;

public class XMLMap implements Serializable {

	private static final long serialVersionUID = 6837841957224108637L;
	
	private String name;
	private String externalId;
	private long internalId;

	public XMLMap(String externalId, int internalId) {
		this.externalId = externalId;
		this.internalId = internalId;
	}
	public XMLMap(String name, String externalId, int internalId) {
		this.name = name;
		this.externalId = externalId;
		this.internalId = internalId;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getExternalId() {
		return externalId;
	}
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	public long getInternalId() {
		return internalId;
	}
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
}
