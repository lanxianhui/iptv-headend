package pl.lodz.p.cm.ctp.npvrd;

import java.io.Serializable;

public class DatabaseConfiguration implements Serializable {
	
	private static final long serialVersionUID = -5408929751345911022L;
	public String userName = null;
	public String password = null;
	public String databaseLocator = null;
	
	@Override
	public String toString() {
		return userName + ":" + password + " " + databaseLocator;
	}

}
