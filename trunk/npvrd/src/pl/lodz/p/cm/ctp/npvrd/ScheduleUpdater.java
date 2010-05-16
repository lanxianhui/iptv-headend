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
	 * Generates a unique file name for a program. Uses a MD5 digest on begining and
	 * end times of the task and the task name.
	 * @param task Task to generate the name for.
	 * @return A unique file name.
	 */
	private static String generateFileName(ProgramRecording pr) {
		String plaintext = pr.program.toString() + pr.recording.toString();
		return DAOUtil.hashMD5(plaintext) + ".ts";
	}
	
	private static ProgramRecordingSink findPRSInListBySink(LinkedList<ProgramRecordingSink> prsList, Sink sink) {
		ProgramRecordingSink foundPrs = null;
		for (ProgramRecordingSink tempPrs : prsList) {
			if (tempPrs.sink.equals(sink)) {
				foundPrs = tempPrs;
			}
		}
		return foundPrs;
	}
	
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
		
		Npvrd.log(parentChannel.getTvChannel().getIpAdress() + "/SU: Creating a VLM manager.");
        VlmManager vlm = new VlmManager(Npvrd.config.vlm);
        String path = Npvrd.config.recordings;
        // Pre- and Post-roll in the database is given in seconds, convert to millis
        int channelPreRollMillis = parentChannel.getTvChannel().getPreRoll() * 1000;
        int channelPostRollMillis = parentChannel.getTvChannel().getPostRoll() * 1000;
        // The window of opportunity is 2 seconds
        int waitTimeMillis = 2 * 1000;
		
        // We run this thread while our parent's RunMode is set tu Run
		while (parentChannel.getRunMode().equals(ChannelListener.RunMode.RUN)) {
			long nextRefreshAt = Long.MAX_VALUE;
			// We keep a now Timestamp as it's handy for comparisons
			Timestamp now = new Timestamp(System.currentTimeMillis());
	        
			// First we check if there are new sinks to be added
	        try {
	        	// Get 5 top ProgramRecordings which end is after now
				List<ProgramRecording> topProgramSchedule = programDvrScheduleDAO.listOlderThanDate(parentChannel.getTvChannel().getId(), lastAdded, 5);
				List<Sink> tempSinks = new LinkedList<Sink>();
				
				// If any where found
				if (topProgramSchedule.size() > 0) {
					// Flag indicating we've found a recording scheduled, that's about to start
					for (ProgramRecording cpr : topProgramSchedule) {
						// We check if given PR is about to begin, if it is, we set smthUseful flag to true
						long beginMillis = cpr.program.getBegin().getTime() - channelPreRollMillis;
						if (beginMillis >= System.currentTimeMillis() - waitTimeMillis) {
							long endMillis = cpr.program.getEnd().getTime() + channelPostRollMillis;
							String fileName = generateFileName(cpr);
							lastAdded = cpr.program.getEnd();
							
							try {
								// We create and add a new sink to the temporary sink list
								Sink tempSink = new FileSink(new BufferedOutputStream(new FileOutputStream(path + fileName)), beginMillis, endMillis);
								tempSinks.add(tempSink);
								// We set the status of a given recording to Processing and we set the fileName
								cpr.recording.setMode(Mode.PROCESSING);
								cpr.recording.setFileName(fileName);
								// We save the data to the database
								recordingDAO.save(cpr.recording);
								// We store the sink in the store to know which recording to update
								prsStore.add(new ProgramRecordingSink(cpr, tempSink));
								// Generally programs happen one after another, so it stands to reason to expect we should refresh around that time
								if (nextRefreshAt > endMillis - channelPostRollMillis - waitTimeMillis - channelPreRollMillis) {
									nextRefreshAt = endMillis - channelPostRollMillis - waitTimeMillis - channelPreRollMillis;
								}
							} catch (FileNotFoundException e) {
								// We cannot add this sink
								Npvrd.error("FileNotFoundException: " + e.getMessage());
							}
						}
					}
					
					// If we've created any useful sinks, it's worth locking the sink pool
					if (tempSinks.size() > 0) {
						parentChannel.getSinksLock().lock();
						
						// We add all sinks in the temporary sinks' list
						parentChannel.getSinks().addAll(tempSinks);
						
						parentChannel.getSinksLock().unlock();
					}
				}
			} catch (DAOException e) {
				Npvrd.error(parentChannel.getTvChannel().getIpAdress() + "/SU: Database error: " + e.getMessage());
			}
			
			// Now we clean timeouted sinks from the sink pool
			List<Sink> tempSinks = new LinkedList<Sink>();
			for (Sink cs : parentChannel.getSinks()) {
				if (!cs.isActive()) {
					tempSinks.add(cs);
				} else {
					try {
						FileSink tfs = FileSink.class.cast(cs);
						if (nextRefreshAt > tfs.getEnd() - channelPostRollMillis - waitTimeMillis - channelPreRollMillis) {
							nextRefreshAt = tfs.getEnd() - channelPostRollMillis - waitTimeMillis - channelPreRollMillis;
						}
					} catch (ClassCastException cce) {
						
					}
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
						// If an error occured to the sink, we set it as unavailable
						if (cs.isError()) {
							cprs.recording.setMode(Mode.UNAVAILABLE);	
						} else {
							cprs.recording.setMode(Mode.AVAILABLE);
						}
						
						try {
							// We save the modified mode to the database
							recordingDAO.save(cprs.recording);
						} catch (DAOException e) {
							Npvrd.error("Unable to set new mode for recording: " + e.getMessage());
						}
						
						// We remove the PRS from the store
						prsStore.remove(cprs);
					}
				}
			}

			try {
				// We sleep as long as it should take to the next update
				Thread.sleep(nextRefreshAt - System.currentTimeMillis());
			} catch (InterruptedException e) {
				
			}
		}
		
		Npvrd.log(parentChannel.getTvChannel().getIpAdress() + "/SU: shutting down.");
	}

}
