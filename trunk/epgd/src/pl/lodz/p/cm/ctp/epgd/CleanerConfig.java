package pl.lodz.p.cm.ctp.epgd;

import java.io.Serializable;

public class CleanerConfig implements Serializable {

	private static final long serialVersionUID = 1400505201105802774L;
	
	public String schedule;
	public Long olderThanDays;

}
