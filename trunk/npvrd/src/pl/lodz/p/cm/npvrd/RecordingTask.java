package pl.lodz.p.cm.npvrd;

import java.util.Date;

public class RecordingTask {
	
	private String programName;
	private Date poczatekNagrywania;
	private Date koniecNagrywania;
	
	public RecordingTask(String programName, Date poczatekNagrywania, Date koniecNagrywania) {
		this.programName = programName;
		this.poczatekNagrywania = poczatekNagrywania;
		this.koniecNagrywania = koniecNagrywania;
	}
	
	public RecordingTask(RecordingTask oldTask) {
		this.programName = oldTask.getProgramName();
		this.poczatekNagrywania = oldTask.getPoczatekNagrywania();
		this.koniecNagrywania = oldTask.getKoniecNagrywania();
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public Date getPoczatekNagrywania() {
		return poczatekNagrywania;
	}

	public void setPoczatekNagrywania(Date poczatekNagrywania) {
		this.poczatekNagrywania = poczatekNagrywania;
	}

	public Date getKoniecNagrywania() {
		return koniecNagrywania;
	}

	public void setKoniecNagrywania(Date koniecNagrywania) {
		this.koniecNagrywania = koniecNagrywania;
	}

}
