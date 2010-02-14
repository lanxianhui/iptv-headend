package pl.lodz.p.cm.ctp.npvrd;

import java.util.List;

import pl.lodz.p.cm.ctp.dao.*;
import pl.lodz.p.cm.ctp.dao.model.*;

public class ScheduleUpdater implements Runnable {
	
	private ChannelRecorder channel;

	public ScheduleUpdater(ChannelRecorder channel) {
		this.channel = channel;
	}
	
	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			DAOFactory dbase = DAOFactory.getInstance(Npvrd.config.database);
			System.out.println("DAOFactory successfully obtained: " + dbase);

			ProgramDvrScheduleDAO programDvrScheduleDAO = dbase.getProgramDvrScheduleDAO();
	        System.out.println("ProgramDvrScheduleDAO successfully obtained: " + programDvrScheduleDAO);
	        
	        try {
				List<ProgramDvrSchedule> programDvrScheduleList = programDvrScheduleDAO.list();
				System.out.println("Got " + programDvrScheduleList.size() + " schedule items.");
				
				for (ProgramDvrSchedule programDvrSchedule : programDvrScheduleList) {
					programDvrSchedule.toString();
				}
			} catch (DAOException e1) {
				System.err.println("Database error: " + e1.getMessage());
				System.err.println("This is a critical error. Terminating.");
				System.exit(1);
				e1.printStackTrace();
			}

			System.out.println("Schedule updater going to sleep for 10s.");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				System.out.println("Someone interrupted us?");
			}
		}
	}

}
