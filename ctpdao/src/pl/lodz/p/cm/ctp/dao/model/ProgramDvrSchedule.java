package pl.lodz.p.cm.ctp.dao.model;

public class ProgramDvrSchedule {
	public Program program;
	public DvrSchedule dvrSchedule;
	
	public ProgramDvrSchedule(Program program, DvrSchedule dvrSchedule) {
		this.program = program;
		this.dvrSchedule = dvrSchedule;
	}
	
	public String toString() {
		return String.format("ProgramDvrSchedule[program=\n%s\ndvrSchedule=\n%s]", 
	            program, dvrSchedule);
	}
}