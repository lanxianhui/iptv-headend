package pl.lodz.p.cm.npvrd;

import java.util.Date;

public class RecordingTask {
	
	private String programName;
	private Date recordingBegin;
	private Date recordingEnd;
	
	/**
	 * Represents a single program to be recorded
	 * @param programName Program to be recorded
	 * @param recordingBegin Program's start time 
	 * @param koniecNagrywania Program's end time
	 */
	public RecordingTask(String programName, Date recordingBegin, Date recordingEnd) {
		this.programName = programName;
		this.recordingBegin = recordingBegin;
		this.recordingEnd = recordingEnd;
	}
	
	/**
	 * Creates a new recording task based on an old one
	 * @param oldTask Old task to be copied
	 */
	public RecordingTask(RecordingTask oldTask) {
		this.programName = oldTask.getProgramName();
		this.recordingBegin = oldTask.getRecordingBegin();
		this.recordingEnd = oldTask.getRecordingEnd();
	}

	/**
	 * Get task program name
	 * @return
	 */
	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public Date getRecordingBegin() {
		return recordingBegin;
	}

	public void setRecordingBegin(Date poczatekNagrywania) {
		this.recordingBegin = poczatekNagrywania;
	}

	public Date getRecordingEnd() {
		return recordingEnd;
	}

	public void setRecordingEnd(Date koniecNagrywania) {
		this.recordingEnd = koniecNagrywania;
	}

}
