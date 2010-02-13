package pl.lodz.p.cm.ctp.dao.model;

public class TvChannel {

	private Long id;
	private String name;
	private String ipAdress;
	private Integer port;
	private String icon;
	
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

	public String getIcon() {
		return icon;
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
		return String.format("TvChannel[id=%d,name=%s,ipAdress=%s,port=%d,icon=%s]", 
	            id, name, ipAdress, port, icon);
	}
}
