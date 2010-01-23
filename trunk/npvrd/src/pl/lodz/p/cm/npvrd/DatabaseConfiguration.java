package pl.lodz.p.cm.npvrd;

public class DatabaseConfiguration {
	
	String userName = null;
	String password = null;
	String databaseLocator = null;
	
	@Override
	public String toString() {
		return userName + ":" + password + " " + databaseLocator;
	}

}
