package pl.lodz.p.cm.ctp.npvrd;

import java.util.List;
import java.io.*;

import pl.lodz.p.cm.ctp.dao.*;
import pl.lodz.p.cm.ctp.dao.model.*;

public class Cleaner implements Runnable {
	public enum RunMode { RUN, STOP };
	
	private RunMode runMode = RunMode.RUN;
	
	private String logPrefix = "Cleaner: ";
	
	public Cleaner() {
		Npvrd.log(logPrefix + "Starting up");
	}
	
	public RunMode getRunMode() {
		return this.runMode;
	}
	
	public void setRunMode(RunMode runModeIn) {
		this.runMode = runModeIn;
	}

	@Override
	public void run() {
		DAOFactory dbase = DAOFactory.getInstance(Npvrd.config.database);
		ProgramRecordingDAO programDvrScheduleDAO = dbase.getProgramRecordingDAO();
		TvChannelDAO tvChannelDAO = dbase.getTvChannelDAO();
		RecordingDAO recordingDAO = dbase.getRecordingDAO();
		List<TvChannel> tvChannels;
		try {
			tvChannels = tvChannelDAO.list();
			
			while (runMode.equals(RunMode.RUN)) {
				int counter = 0;
				for (TvChannel tc : tvChannels) {
					try {
						List<ProgramRecording> forDeletion = programDvrScheduleDAO.listOlderThanHours(tc.getId(), Npvrd.config.cleanerTolerance);
						for (ProgramRecording cpr : forDeletion) {
							File fileForDeletion = new File(Npvrd.config.recordings + cpr.recording.getFileName());
							recordingDAO.delete(cpr.recording);
							fileForDeletion.delete();
							counter++;
						}
					} catch (DAOException e) {
						Npvrd.error(logPrefix + "Problem working with the database in Cleaner");
					}	
				}
				Npvrd.log(logPrefix + "Deleted " + counter + " recordings.");
				
				try {
					Thread.sleep(Npvrd.config.cleanerResolution * 60 * 60 * 1000);
				} catch (InterruptedException e) {
					// Someone woke us up, perhaps to inform us about the new RunMode. No harm done
					// In executing another loop.
				}
			}
		} catch (DAOException e1) {
			Npvrd.error(logPrefix + "Unable to get TvChannels!");
		}
	}

}
