package pl.lodz.p.cm.ctp.epgd;

import pl.lodz.p.cm.ctp.dao.DAOException;
import pl.lodz.p.cm.ctp.dao.DAOFactory;
import pl.lodz.p.cm.ctp.dao.ProgramDAO;

public class Cleaner implements Runnable {
	
	private CleanerConfig myConfig;
	
	public Cleaner(CleanerConfig config) {
		this.myConfig = config;
	}

	@Override
	public void run() {
		DAOFactory dbase = DAOFactory.getInstance(Epgd.config.database);
		ProgramDAO programDAO = dbase.getProgramDAO();
		
		try {
			boolean result = programDAO.deleteOlderThan(myConfig.olderThanDays);
			if (result) {
				Epgd.log("Pomyślnie usunieto ostatnie " + myConfig.olderThanDays + " dni z EPG");
			}
		} catch (DAOException e) {
			Epgd.error("Wystapił błąd bazy: " + e.getMessage());
		}
	}

}
