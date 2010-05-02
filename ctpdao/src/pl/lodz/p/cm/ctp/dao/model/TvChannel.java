package pl.lodz.p.cm.ctp.dao.model;

public class TvChannel {

	private Long id;
	private String name;
	private String ipAdress;
	private Integer port;
	private String unicastUrl;
	private Integer lcn;
	private String icon;
	private Boolean enabled;
	
	public TvChannel() {
		
	}
	
	public TvChannel(Long id, String name, String ipAdress, Integer port) {
		this.id = id;
		this.name = name;
		this.ipAdress = ipAdress;
		this.port = port;
	}
	
	public TvChannel(Long id, String name, String ipAdress, Integer port, String icon) {
		this(id, name, ipAdress, port);
		this.icon = icon;
	}
	
	public TvChannel(Long id, String name, String ipAdress, Integer port, Integer lcn, String icon, Boolean enabled) {
		this(id, name, ipAdress, port, icon);
		this.lcn = lcn;
		this.enabled = enabled;
	}
	
	public TvChannel(Long id, String name, String ipAdress, Integer port, String unicastUrl, Integer lcn, String icon, Boolean enabled) {
		this(id, name, ipAdress, port, lcn, icon, enabled);
		this.unicastUrl = unicastUrl;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIpAdress() {
		return ipAdress;
	}

	public void setIpAdress(String ipAdress) {
		this.ipAdress = ipAdress;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}
	
	public String getUnicastUrl() {
		return unicastUrl;
	}

	public void setUnicastUrl(String unicastUrl) {
		this.unicastUrl = unicastUrl;
	}

	public String getIcon() {
		return icon;
	}

	public Integer getLCN() {
		return lcn;
	}

	public void setLCN(Integer lcn) {
		this.lcn = lcn;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	public boolean equals(Object other) {
		return (other instanceof TvChannel) && (id != null) ? id.equals(((TvChannel) other).id) : (other == this);
	}
	
	public int hashCode() {
        return (id != null) ? (this.getClass().hashCode() + id.hashCode()) : super.hashCode();
    }

	public String toString() {
		return String.format("TvChannel[id=%d,name=%s,ipAdress=%s,port=%d,lcn=%d,icon=%s,enabled=%b]", 
	            id, name, ipAdress, port, lcn, icon, enabled);
	}
}
