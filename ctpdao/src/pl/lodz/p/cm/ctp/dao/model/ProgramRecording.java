package pl.lodz.p.cm.ctp.dao.model;

public class ProgramRecording {
	public Program program;
	public Recording recording;
	
	public ProgramRecording(Program program, Recording dvrSchedule) {
		this.program = program;
		this.recording = dvrSchedule;
	}
	
	public boolean equals(Object o) {
		return (o instanceof ProgramRecording) ? (((ProgramRecording)o).program.equals(this.program) && ((ProgramRecording)o).recording.equals(this.recording)) : false; 
	}
	
	public String toString() {
		return String.format("ProgramRecording[program=\n%s\nrecording=\n%s]", 
	            program, recording);
	}
}