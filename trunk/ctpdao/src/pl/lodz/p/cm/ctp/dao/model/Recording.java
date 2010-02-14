package pl.lodz.p.cm.ctp.dao.model;

public class Recording {
	
	public enum Mode { WAITING, PROCESSING, AVAILABLE, UNAVAILABLE };

	private Long id;
	private Long programId;
	private Mode mode;
	private String fileName;
	
	public Recording() {
		
	}
	
	public Recording(Long id, Long programId) {
		this.id = id;
		this.programId = programId;
	}
	
	public Recording(Long id, Long programId, Mode mode, String fileName) {
		this(id, programId);
		this.mode = mode;
		this.fileName = fileName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getProgramId() {
		return programId;
	}

	public void setProgramId(Long programId) {
		this.programId = programId;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public boolean equals(Object other) {
		return (other instanceof Recording) && (id != null) ? id.equals(((Recording) other).id) : (other == this);
	}
	
	public int hashCode() {
        return (id != null) ? (this.getClass().hashCode() + id.hashCode()) : super.hashCode();
    }
	
	public String toString() {
		return String.format("DvrSchedule[id=%d,programId=%d,mode=%s,fileName=%s]", 
	            id, programId, mode.toString(), fileName);
	}
}
