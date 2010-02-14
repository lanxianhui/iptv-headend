package pl.lodz.p.cm.ctp.npvrd;

import java.util.List;

import pl.lodz.p.cm.ctp.dao.*;
import pl.lodz.p.cm.ctp.dao.model.*;

public class ScheduleUpdater implements Runnable {
	
	private ChannelRecorder parentChannel;
	private Thread parentThread;

	public ScheduleUpdater(ChannelRecorder parentChannel, Thread parentThread) {
		this.parentChannel = parentChannel;
		this.parentThread = parentThread;
	}
	
	@Override
	public void run() {
		while (parentChannel.getRunMode().equals(ChannelRecorder.RunMode.RUN)) {
			DAOFactory dbase = DAOFactory.getInstance(Npvrd.config.database);
			ProgramDvrScheduleDAO programDvrScheduleDAO = dbase.getProgramDvrScheduleDAO();
	        
	        try {
				List<ProgramDvrSchedule> programDvrScheduleList = programDvrScheduleDAO.listOlderThanNow(parentChannel.getChannelId());
				
				if (programDvrScheduleList.size() > 0) {
					System.out.println(parentChannel.getGroupIp() + "/SU: Got " + programDvrScheduleList.size() + " schedule items.");
					
					parentChannel.lockSchedule();
					try {
						parentChannel.clear();
						for (ProgramDvrSchedule programDvrSchedule : programDvrScheduleList) {
							parentChannel.add(new RecordingTask(programDvrSchedule));	
						}
					} finally {
						parentChannel.unlockSchedule();
					}
					parentChannel.setRecheckSchedule(true);
					parentThread.interrupt();
				}
			} catch (DAOException e) {
				System.err.println("Database error: " + e.getMessage());
				System.err.println("This is a critical error. Terminating.");
				System.exit(1);
				e.printStackTrace();
			}

			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				
			}
		}
		
		System.out.println(parentChannel.getGroupIp() + "/SU: Thread terminating.");
	}

}
