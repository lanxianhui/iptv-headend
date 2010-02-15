package pl.lodz.p.cm.ctp.epgd;

import java.io.Serializable;

public class XMLChannel implements Serializable {
	
	private static final long serialVersionUID = 4037760608294286048L;
	
	private String id;
	private String displayName;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
}
