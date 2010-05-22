package pl.lodz.p.cm.ctp.npvrd;

import java.io.*;
import java.sql.Timestamp;
import java.util.*;

import pl.lodz.p.cm.ctp.dao.*;
import pl.lodz.p.cm.ctp.dao.model.*;
import pl.lodz.p.cm.ctp.dao.model.Recording.Mode;

public class ScheduleUpdater implements Runnable {
	
	private ChannelListener parentChannel;
	
	private class ProgramRecordingSink {
		public Recording recording;
		public Program program;
		public Sink sink;
		
		@SuppressWarnings("unused")
		ProgramRecordingSink(Program program, Recording recording, Sink sink) {
			this.program = program;
			this.recording = recording;
			this.sink = sink;
		}
		
		ProgramRecordingSink(ProgramRecording programRecording, Sink sink) {
			this.program = programRecording.program;
			this.recording = programRecording.recording;
			this.sink = sink;
		}
	}

	public ScheduleUpdater(ChannelListener parentChannel, Thread parentThread) {
		this.parentChannel = parentChannel;
	}

	/**
	 * Generates a unique file name for a program. The result is an MD5 digest.
	 * @param pr ProgramRecording object describing the recording to be made
	 * @return A unique file name.
	 */
	private static String generateFileName(ProgramRecording pr) {
		String plaintext = pr.program.toString() + pr.recording.toString();
		return DAOUtil.hashMD5(plaintext) + ".ts";
	}
	
	/**
	 * Find a given sink in a haystack of ProgramRecordingSink list
	 * @param prsList The haystack to search through
	 * @param sink The sink to be found
	 * @return The sink found, or null if not found
	 */
	private static ProgramRecordingSink findPRSInListBySink(List<ProgramRecordingSink> prsList, Sink sink) {
		ProgramRecordingSink foundPrs = null;
		for (ProgramRecordingSink tempPrs : prsList) {
			if (tempPrs.sink.equals(sink)) {
				foundPrs = tempPrs;
			}
		}
		return foundPrs;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		// We create a storage containing links to all Sinks and their respective recordings
		LinkedList<ProgramRecordingSink> prsStore = new LinkedList<ProgramRecordingSink>();
		// We create a new link to the database
		DAOFactory dbase = DAOFactory.getInstance(Npvrd.config.database);
		ProgramRecordingDAO programDvrScheduleDAO = dbase.getProgramRecordingDAO();
		RecordingDAO recordingDAO = dbase.getRecordingDAO();
		// Last added - useful to know what was last added to the ChannelListener Sink pool
		Timestamp lastAdded = new Timestamp(System.currentTimeMillis());
		
		String logPrefix = parentChannel.getTvChannel().getIpAdress() + "/SU: ";
		
		Npvrd.log(logPrefix + "Creating a VLM manager.");
        VlmManager vlm = new VlmManager(Npvrd.config.vlm);
        String path = Npvrd.config.recordings;
        // Pre- and Post-roll in the database is given in seconds, convert to millis
        int channelPreRollMillis = parentChannel.getTvChannel().getPreRoll() * 1000;
        int channelPostRollMillis = parentChannel.getTvChannel().getPostRoll() * 1000;
        // The window of opportunity is 2 seconds
        int waitTimeMillis = Npvrd.config.prepTime * 1000;
		
        // We run this thread while our parent's RunMode is set tu Run
		while (parentChannel.getRunMode().equals(ChannelListener.RunMode.RUN)) {
	        // Default nextRefreshAt (set 10 minutes in the future)
	        long nextRefreshAt = System.currentTimeMillis() + 10 * 60 * 1000;
			// We keep a now Timestamp as it's handy for comparisons
			// Timestamp now = new Timestamp(System.currentTimeMillis());
			
			//Npvrd.log(logPrefix + "Next loop.");
	        
			// First we check if there are new sinks to be added
	        try {
	        	// Get 5 top ProgramRecordings which end is after now
				List<ProgramRecording> topProgramSchedule = programDvrScheduleDAO.listYoungerThanDate(parentChannel.getTvChannel().getId(), lastAdded, 5);
				List<Sink> tempSinks = new LinkedList<Sink>();
				
				// If any where found
				if (topProgramSchedule.size() > 0) {
					for (ProgramRecording cpr : topProgramSchedule) {
						// We check if given PR is about to begin, if it is, we set smthUseful flag to true
						// Npvrd.log("Program: " + cpr.program.getTitle() + " at " + cpr.program.getBegin().toGMTString());
						long beginMillis = cpr.program.getBegin().getTime() - channelPreRollMillis;
						long endMillis = cpr.program.getEnd().getTime() + channelPostRollMillis;
						if (beginMillis - waitTimeMillis - 100 < System.currentTimeMillis()) {
							String fileName = generateFileName(cpr);
							lastAdded = cpr.program.getEnd();
							
							try {
								// We create and add a new sink to the temporary sink list
								Sink tempSink = new FileSink(new BufferedOutputStream(new FileOutputStream(path + fileName)), beginMillis, endMillis);
								tempSinks.add(tempSink);
								Npvrd.log(logPrefix + "New sink: " + cpr.program.getTitle() + " at " + cpr.program.getBegin().toGMTString() + " (" + (new Timestamp(beginMillis)).toGMTString() + ")");
								// We set the status of a given recording to Processing and we set the fileName
								cpr.recording.setMode(Mode.PROCESSING);
								cpr.recording.setFileName(fileName);
								// We save the data to the database
								recordingDAO.save(cpr.recording);
								// We store the sink in the store to know which recording to update
								prsStore.add(new ProgramRecordingSink(cpr, tempSink));
							} catch (FileNotFoundException e) {
								// We cannot add this sink
								Npvrd.error(logPrefix + "FileNotFoundException: " + e.getMessage());
							}
							
							// Generally programs happen one after another, so it stands to reason to expect we should refresh around that time
							//Npvrd.log(logPrefix + "Compare End: " + (new Timestamp(nextRefreshAt)) + " (nra) to " + (new Timestamp(endMillis - channelPostRollMillis - waitTimeMillis - channelPreRollMillis)));
							if (nextRefreshAt > endMillis - channelPostRollMillis - waitTimeMillis - channelPreRollMillis) {
								nextRefreshAt = endMillis - channelPostRollMillis - waitTimeMillis - channelPreRollMillis - 100;
								//Npvrd.log(logPrefix + "New refresh at: " + (new Timestamp(nextRefreshAt)));
							}
						} else {
							// We should also check if a program following this one may start sooner than current nextRefreshAt
							//Npvrd.log(logPrefix + "Compare Begin: " + (new Timestamp(nextRefreshAt)) + " (nra) to " + (new Timestamp(endMillis - channelPostRollMillis - waitTimeMillis - channelPreRollMillis)));
							if (nextRefreshAt > beginMillis) {
								nextRefreshAt = beginMillis - 100;
								//Npvrd.log(logPrefix + "New refresh at: " + (new Timestamp(nextRefreshAt)));
							}
						}
					}
					
					// If we've created any useful sinks, it's worth locking the sink pool
					if (tempSinks.size() > 0) {
						parentChannel.getSinksLock().lock();
						
						// We add all sinks in the temporary sinks' list
						parentChannel.getSinks().addAll(tempSinks);
						
						parentChannel.getSinksLock().unlock();
						
						Npvrd.log(logPrefix + "Addeed new sinks");
					}
				}
			} catch (DAOException e) {
				Npvrd.error(logPrefix + "Database error: " + e.getMessage());
			}
			
			// Now we clean timeouted sinks from the sink pool
			List<Sink> tempSinks = new LinkedList<Sink>();
			for (Sink cs : parentChannel.getSinks()) {
				if (!cs.isActive()) {
					tempSinks.add(cs);
				}
			}
			
			// If any sinks should be removed, we lock the sink pool
			if (tempSinks.size() > 0) {
				parentChannel.getSinksLock().lock();
				
				parentChannel.getSinks().removeAll(tempSinks);
				
				parentChannel.getSinksLock().unlock();
				
				// Now we can close all removed sinks
				for (Sink cs : tempSinks) {
					// We look for our sink in our PRS store
					ProgramRecordingSink cprs = findPRSInListBySink(prsStore, cs);
					// We close the removed sink
					cs.close();
					
					// If the sink was found in the store (it should - there is no way it's gone!)
					if (cprs != null) {
						Npvrd.log(logPrefix + "Finished sink: " + cprs.program.getTitle() + " at " + cprs.program.getBegin().toGMTString());
						// If an error occured to the sink, we set it as unavailable
						if (cs.isError()) {
							cprs.recording.setMode(Mode.UNAVAILABLE);	
						} else {
							cprs.recording.setMode(Mode.AVAILABLE);
							vlm.createNewVod(cprs.recording.getFileName(), Npvrd.config.recordings + cprs.recording.getFileName());
						}
						
						try {
							// We save the modified mode to the database
							recordingDAO.save(cprs.recording);
						} catch (DAOException e) {
							Npvrd.error(logPrefix + "Unable to set new mode for recording: " + e.getMessage());
						}
						
						// We remove the PRS from the store
						prsStore.remove(cprs);
					}
				}
			}

			try {
				// We sleep as long as it should take to the next update
				//Npvrd.log(logPrefix + "Next loop in " + ((nextRefreshAt - System.currentTimeMillis()) / 1000) + " seconds");
				Thread.sleep(nextRefreshAt - System.currentTimeMillis());
			} catch (InterruptedException ie) {
				// We've been interrupted - perhaps as a result of a changing RunMode
				// Anyway, it's worth rechecking everything.
			} catch (IllegalArgumentException iae) {
				Npvrd.log(logPrefix + "Strange timeout value: " + (nextRefreshAt - System.currentTimeMillis()));
				//System.exit(1);
			}
		}
		
		// If any Sinks are outstanding, we should do something about them
		for (ProgramRecordingSink cprs : prsStore) {
			// If the sink is still Active or in Error we should mark the recording as Unavailable
			if (cprs.sink.isActive() | cprs.sink.isError()) {
				cprs.recording.setMode(Mode.UNAVAILABLE);
			} else {
				cprs.recording.setMode(Mode.AVAILABLE);
			}
			// Close the sink
			cprs.sink.close();
			try {
				// Save the new mode to database
				recordingDAO.save(cprs.recording);
			} catch (DAOException e) {
				Npvrd.error(logPrefix + "Unable to set new mode for recording: " + e.getMessage());
			}
		}
		
		Npvrd.log(logPrefix + "Shutting down.");
	}

}
