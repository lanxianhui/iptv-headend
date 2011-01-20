package pl.lodz.p.cm.ctp.dao.model;

public class ProgramRecording {
	private Program program;
	private Recording recording;
	
	public ProgramRecording(Program program, Recording dvrSchedule) {
		this.program = program;
		this.recording = dvrSchedule;
	}
	
	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
	}

	public Recording getRecording() {
		return recording;
	}

	public void setRecording(Recording recording) {
		this.recording = recording;
	}

	public boolean equals(Object o) {
		return (o instanceof ProgramRecording) ? (((ProgramRecording)o).program.equals(this.program) && ((ProgramRecording)o).recording.equals(this.recording)) : false; 
	}
	
	public String toString() {
		return String.format("ProgramRecording[program=\n%s\nrecording=\n%s]", 
	            program, recording);
	}
}