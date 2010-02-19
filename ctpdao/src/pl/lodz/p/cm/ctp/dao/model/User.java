package pl.lodz.p.cm.ctp.dao.model;

import java.util.Calendar;
import java.util.Date;

public class User {
	
	private Long id;
	private String userName;
	private String password;
	private String authToken;
	private Integer quota;
	private Boolean enabled;
	private String fullName;
	private Date createdOn;
	private Date lastLogin;
	
	public User() {
		
	}
	
	public User(Long id, String userName, String password, String authToken, Integer quota, Boolean enabled, String fullName, Date createdOn, Date lastLogin) {
		this.id = id;
		this.userName = userName;
		this.password = password;
		this.authToken = authToken;
		this.quota = quota;
		this.enabled = enabled;
		this.fullName = fullName;
		this.createdOn = createdOn;
		this.lastLogin = lastLogin;
	}
	
	public User(Long id, String userName, String authToken, Integer quota, String fullName) {
		this(id, userName, null, authToken, quota, false, fullName, Calendar.getInstance().getTime(), null);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public Integer getQuota() {
		return quota;
	}

	public void setQuota(Integer quota) {
		this.quota = quota;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public boolean equals(Object other) {
		return (other instanceof User) && (id != null) ? id.equals(((User) other).id) : (other == this);
	}
	
	public int hashCode() {
        return (id != null) ? (this.getClass().hashCode() + id.hashCode()) : super.hashCode();
    }
}
