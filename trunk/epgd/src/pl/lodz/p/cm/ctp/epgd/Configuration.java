package pl.lodz.p.cm.ctp.epgd;

import java.io.Serializable;
import java.util.ArrayList;

import pl.lodz.p.cm.ctp.dao.DatabaseConfiguration;

public class Configuration implements Serializable {

	private static final long serialVersionUID = -323016552321966082L;
	
	public DatabaseConfiguration database;
	public ArrayList<XmlTvGrabberConfig> xmlTvGrabbers;
	public boolean recordAllPrograms;
	public String runInfoDir = "/var/run/epgd/";
	public CleanerConfig cleaner;
	
}
