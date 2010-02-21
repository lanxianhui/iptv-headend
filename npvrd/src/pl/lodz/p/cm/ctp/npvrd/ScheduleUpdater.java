package pl.lodz.p.cm.ctp.npvrd;

import java.util.List;

import pl.lodz.p.cm.ctp.dao.*;
import pl.lodz.p.cm.ctp.dao.model.*;

public class ScheduleUpdater implements Runnable {
	
	private ChannelRecorder parentChannel;
	private Thread parentThread;
	private ProgramRecording lastProgram = null;

	public ScheduleUpdater(ChannelRecorder parentChannel, Thread parentThread) {
		this.parentChannel = parentChannel;
		this.parentThread = parentThread;
	}
	
	@Override
	public void run() {
		while (parentChannel.getRunMode().equals(ChannelRecorder.RunMode.RUN)) {
			DAOFactory dbase = DAOFactory.getInstance(Npvrd.config.database);
			ProgramRecordingDAO programDvrScheduleDAO = dbase.getProgramRecordingDAO();
	        
	        try {
				List<ProgramRecording> programDvrScheduleList = programDvrScheduleDAO.listOlderThanNow(parentChannel.getChannelId());
				
				if (programDvrScheduleList.size() > 0) {
					//System.out.println(parentChannel.getGroupIp() + "/SU: Got " + programDvrScheduleList.size() + " schedule items.");
					
					ProgramRecording newFirstProgram = null;
					parentChannel.lockSchedule();
					try {
						parentChannel.clear();
						for (ProgramRecording programDvrSchedule : programDvrScheduleList) {
							if (newFirstProgram == null) newFirstProgram = programDvrSchedule;
							parentChannel.add(new RecordingTask(programDvrSchedule));	
						}
					} finally {
						parentChannel.unlockSchedule();
					}
					parentChannel.setRecheckSchedule(true);
					if (!newFirstProgram.equals(lastProgram))
					{
						lastProgram = newFirstProgram;
						parentThread.interrupt();
					}
				}
			} catch (DAOException e) {
				Npvrd.error(parentChannel.getGroupIp() + "/SU: Database error: " + e.getMessage());
				Npvrd.error(parentChannel.getGroupIp() + "/SU: This is a critical error, but we hope for the best and keep running.");
			}

			try {
				Thread.sleep(Npvrd.config.schedUpdaterInterval * 1000L);
			} catch (InterruptedException e) {
				
			}
		}
		
		Npvrd.log(parentChannel.getGroupIp() + "/SU: shutting down.");
	}

}
