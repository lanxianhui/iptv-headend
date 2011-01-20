package pl.lodz.p.cm.ctp.dao.model;

import java.util.Date;

public class UserRecording {
	
	private Long userId;
	private Long recordingId;
	private Date createdOn;
	
	public UserRecording() {
		
	}
	
	public UserRecording(Long userId, Long recordingId, Date createdOn) {
		this.userId = userId;
		this.recordingId = recordingId;
		this.createdOn = createdOn;
	}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getRecordingId() {
		return recordingId;
	}

	public void setRecordingId(Long recordingId) {
		this.recordingId = recordingId;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public boolean equals(Object other) {
		if ((other instanceof UserRecording) && (userId != null) && (recordingId != null)) {
			UserRecording otherp = (UserRecording) other;
			return (userId.equals(otherp.userId) && recordingId.equals(otherp.recordingId));
		} else {
			return (other == this);
		}
	}
	
	public int hashCode() {
        return ((userId != null) && (recordingId != null)) ? (this.getClass().hashCode() + userId.hashCode() + recordingId.hashCode()) : super.hashCode();
    }
}
