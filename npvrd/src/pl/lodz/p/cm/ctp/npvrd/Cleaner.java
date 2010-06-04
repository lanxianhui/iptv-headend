package pl.lodz.p.cm.ctp.npvrd;

import java.util.List;
import java.io.*;

import pl.lodz.p.cm.ctp.dao.*;
import pl.lodz.p.cm.ctp.dao.model.*;

public class Cleaner implements Runnable {
	private String logPrefix = "Cleaner: ";
	
	private List<TvChannel> tvChannels;
	private TvChannelDAO tvChannelDAO;
	private RecordingDAO recordingDAO;
	private ProgramRecordingDAO programDvrScheduleDAO;
	
	public Cleaner() {
		Npvrd.log(logPrefix + "Starting up");
		DAOFactory dbase = DAOFactory.getInstance(Npvrd.config.database);
		programDvrScheduleDAO = dbase.getProgramRecordingDAO();
		tvChannelDAO = dbase.getTvChannelDAO();
		recordingDAO = dbase.getRecordingDAO();
		try {
			tvChannels = tvChannelDAO.list();
		} catch (DAOException e) {
			Npvrd.error(logPrefix + "Unable to get channel list");
		}
	}

	@Override
	public void run() {
		int counter = 0;
		for (TvChannel tc : tvChannels) {
			try {
				List<ProgramRecording> forDeletion = programDvrScheduleDAO.listOlderThanHours(tc.getId(), Npvrd.config.cleanerTolerance);
				for (ProgramRecording cpr : forDeletion) {
					File fileForDeletion = new File(Npvrd.config.recordings + cpr.recording.getFileName());
					recordingDAO.delete(cpr.recording);
					boolean result = fileForDeletion.delete();
					if (!result) {
						Npvrd.error(logPrefix + "Could not delete file " + cpr.recording.getFileName() + ". Dropped nonetheless.");
					}
					counter++;
				}
			} catch (DAOException e) {
				Npvrd.error(logPrefix + "Problem working with the database in Cleaner");
			}	
		}
		Npvrd.log(logPrefix + "Deleted " + counter + " recordings.");
	}

}
