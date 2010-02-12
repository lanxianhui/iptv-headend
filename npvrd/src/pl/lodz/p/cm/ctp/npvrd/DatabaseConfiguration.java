package pl.lodz.p.cm.ctp.npvrd;

public class DatabaseConfiguration {
	
	String userName = null;
	String password = null;
	String databaseLocator = null;
	
	@Override
	public String toString() {
		return userName + ":" + password + " " + databaseLocator;
	}

}
