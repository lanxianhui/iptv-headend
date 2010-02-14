package pl.lodz.p.cm.ctp.npvrd;

import java.util.Date;

import pl.lodz.p.cm.ctp.dao.*;
import pl.lodz.p.cm.ctp.dao.model.*;
import pl.lodz.p.cm.ctp.dao.model.Recording.Mode;

public class RecordingTask implements Comparable<RecordingTask> {
	
	private String programName;
	private Date recordingBegin;
	private Date recordingEnd;
	private Mode state = Mode.WAITING;
	private String resultFileName = null;
	private ProgramRecording programRecording = null;
	
	/**
	 * Represents a single program to be recorded.
	 * @param programName Program to be recorded.
	 * @param recordingBegin Program's start time. 
	 * @param koniecNagrywania Program's end time.
	 */
	public RecordingTask(String programName, Date recordingBegin, Date recordingEnd) {
		this.programName = programName;
		this.recordingBegin = recordingBegin;
		this.recordingEnd = recordingEnd;
	}
	
	public RecordingTask(ProgramRecording programRecording) {
		this.programRecording = programRecording;
		this.programName = programRecording.program.getTitle();
		this.recordingBegin = programRecording.program.getBegin();
		this.recordingEnd = programRecording.program.getEnd();
		this.resultFileName = this.programRecording.recording.getFileName();
		this.state = this.programRecording.recording.getMode();
	}
	
	/**
	 * Creates a new recording task based on an old one.
	 * @param oldTask Old task to be copied.
	 */
	public RecordingTask(RecordingTask oldTask) {
		this.programName = oldTask.programName;
		this.recordingBegin = oldTask.recordingBegin;
		this.recordingEnd = oldTask.recordingEnd;
	}

	/**
	 * Get task program name.
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
	
	public void setState(Mode state) {
		this.state = state;
		if (programRecording != null) {
			this.programRecording.recording.setMode(this.state);
		}
	}
	
	public Mode getState() {
		return this.state;
	}
	
	public void setResultFileName(String fileName) {
		this.resultFileName = fileName;
		if (programRecording != null) {
			this.programRecording.recording.setFileName(fileName);
		}
	}
	
	public String getResultFileName() {
		return this.resultFileName;
	}
	
	public void saveToDatabase() {
		if (programRecording != null) {
			DAOFactory dbase = DAOFactory.getInstance(Npvrd.config.database);
			RecordingDAO dvrScheduleDAO = dbase.getRecordingDAO();
			try {
				dvrScheduleDAO.save(this.programRecording.recording);
			} catch (DAOException e) {
				System.err.println("Unable to save underlying DvrSchedule (" + this.programRecording.recording.getId() + ") to database: " + e.getMessage());
			}
		}
	}
	
	public boolean equals(Object o) {
		if (o instanceof RecordingTask) {
			return (((RecordingTask) o).programName.equals(this.programName) && ((RecordingTask) o).recordingBegin.equals(this.recordingBegin) && ((RecordingTask) o).recordingEnd.equals(this.recordingEnd));
		}
		return false;
	}

	@Override
	public int compareTo(RecordingTask o) {
		// TODO Auto-generated method stub
		return this.recordingBegin.compareTo(o.recordingBegin);
	}

}
