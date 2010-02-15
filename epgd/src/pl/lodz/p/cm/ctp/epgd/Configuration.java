package pl.lodz.p.cm.ctp.epgd;

import java.io.Serializable;
import pl.lodz.p.cm.ctp.dao.DatabaseConfiguration;

public class Configuration implements Serializable {

	private static final long serialVersionUID = -323016552321966082L;
	
	public DatabaseConfiguration database;
	public XmlTvGrabber xmlTvGrabber;
	
}
