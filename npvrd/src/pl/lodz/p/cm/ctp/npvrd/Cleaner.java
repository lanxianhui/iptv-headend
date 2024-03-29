package pl.lodz.p.cm.ctp.npvrd;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
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
	private UserRecordingDAO userRecordingDAO;
	
	public Cleaner() {
		Npvrd.log(logPrefix + "Starting up");
		DAOFactory dbase = DAOFactory.getInstance(Npvrd.config.database);
		programDvrScheduleDAO = dbase.getProgramRecordingDAO();
		tvChannelDAO = dbase.getTvChannelDAO();
		recordingDAO = dbase.getRecordingDAO();
		userRecordingDAO = dbase.getUserRecordingDAO();
		try {
			tvChannels = tvChannelDAO.list();
		} catch (DAOException e) {
			Npvrd.error(logPrefix + "Unable to get channel list. Message: " + e.getMessage());
		}
	}

	@Override
	public void run() {
		int counter = 0;
		for (TvChannel tc : tvChannels) {
			try {
				List<ProgramRecording> forDeletion = programDvrScheduleDAO.listOlderThanHours(tc.getId(), Npvrd.config.cleanerTolerance);
				List<UserRecording> subscriptionsToLetGo = new LinkedList<UserRecording>();
				int maxHold = Npvrd.config.cleanerMaxHold;
				Date now = new Date();
				
				for (ProgramRecording cpr : forDeletion) {
					boolean deletable = true;
					
					try {
						List<UserRecording> subscribers = userRecordingDAO.listByRecordingId(cpr.getRecording().getId());
						for (UserRecording sub : subscribers) {
							Calendar cal = Calendar.getInstance();
							if (sub.getCreatedOn().before(cpr.getProgram().getEnd())) {
								//Timestamp EndPlusHold = (Timestamp)cpr.getProgram().getEnd().clone();
								Date endPlusHold = new Date(cpr.getProgram().getEnd().getTime());
								cal.setTime(endPlusHold);
								cal.add(Calendar.HOUR_OF_DAY, maxHold);
								if (cal.after(now)) {
									deletable = false;
								} else {
									subscriptionsToLetGo.add(sub);
								}
							} else {
								Date createdOnPlusHold = (Date)sub.getCreatedOn().clone();
								cal.setTime(createdOnPlusHold);
								cal.add(Calendar.HOUR_OF_DAY, maxHold);
								if (cal.after(now)) {
									deletable = false;
								} else {
									subscriptionsToLetGo.add(sub);
								}
							}
						}
						for (UserRecording sub : subscriptionsToLetGo) {
							userRecordingDAO.delete(sub);
						}
					} catch (DAOException subException) {
						Npvrd.error(logPrefix + "Exception in Cleaner, iterating through subscriptions. Message: " + subException.getMessage());
						Npvrd.error(logPrefix + "Stack trace: ");
						
						// TODO: Debug only
						subException.printStackTrace();
					}
					
					if (deletable) {
						try {
							File fileForDeletion = new File(Npvrd.config.recordings + cpr.getRecording().getFileName());
							recordingDAO.delete(cpr.getRecording());
							boolean result = fileForDeletion.delete();
							if (!result) {
								Npvrd.error(logPrefix + "Could not delete file " + cpr.getRecording().getFileName() + " from filesystem.");
							}
							counter++;
						} catch (DAOException fileException) {
							Npvrd.error(logPrefix + "Exception in Cleaner, deleting file. Message: " + fileException.getMessage());
							Npvrd.error(logPrefix + "Stack trace: ");

							// TODO: Debug only
							fileException.printStackTrace();
						}
					}
				}
			} catch (DAOException e) {
				Npvrd.error(logPrefix + "Exception in Cleaner, interating through channels. Message: " + e.getMessage());
				Npvrd.error(logPrefix + "Stack trace: ");
				
				// TODO: Debug only
				e.printStackTrace();
			}	
		}
		Npvrd.log(logPrefix + "Deleted " + counter + " recordings.");
	}

}
