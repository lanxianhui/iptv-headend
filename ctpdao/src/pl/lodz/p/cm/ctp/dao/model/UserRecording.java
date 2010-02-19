package pl.lodz.p.cm.ctp.dao.model;

public class UserRecording {
	
	private Long userId;
	private Long recordingId;
	
	public UserRecording() {
		
	}
	
	public UserRecording(Long userId, Long recordingId) {
		this.userId = userId;
		this.recordingId = recordingId;
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
