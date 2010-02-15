package pl.lodz.p.cm.ctp.epgd;

import java.io.*;
import java.sql.Timestamp;
import java.util.Hashtable;

import org.apache.commons.daemon.*;

import pl.lodz.p.cm.ctp.dao.*;
import pl.lodz.p.cm.ctp.dao.model.Program;

import com.thoughtworks.xstream.*;

public class Epgd implements Daemon {
	
	static Configuration config;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(DaemonContext arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void start() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String configFile = "config.xml";
		Hashtable<String, Long> channelMap = new Hashtable<String, Long>();
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-c")) {
				configFile = args[++i];
			}
		}
		
		try {
			XStream xs = new XStream();
			FileInputStream fis = new FileInputStream(configFile);
			xs.alias("config", Configuration.class);
			xs.aliasField("database", Configuration.class, "database");
			
			config = (Configuration)xs.fromXML(fis);
		} catch (FileNotFoundException e) {
			System.err.println("Configuration file not found!");
			System.exit(1);
		}
		
		try {
			XStream xs = new XStream();
			xs.alias("map", XMLMap.class);
			xs.aliasAttribute(XMLMap.class, "externalId", "extId");
			xs.aliasAttribute(XMLMap.class, "internalId", "dbId");
			xs.aliasAttribute(XMLMap.class, "name", "name");
			
			ObjectInputStream ois = xs.createObjectInputStream(new FileInputStream(config.xmlTvGrabber.mapFile));
			try {
				while(true) {
					try {
						Object ro = ois.readObject();
						if (ro instanceof XMLMap) {
							XMLMap rm = (XMLMap)ro;
							channelMap.put(rm.getExternalId(), rm.getInternalId());
						}
					} catch (ClassNotFoundException e) {
						System.err.println("Unknown object in XMLTV file: " + e.getMessage());
					}	
				}
				
			} catch (EOFException eof) {
				
			}
			
			ois.close();
		} catch (FileNotFoundException e) {
			System.err.println("XMLTV mapping file could not be opened." + e.getMessage());
		} catch (IOException e) {
			System.err.println("There is a problem with the XMLTV mapping file: " + e.getMessage());
		} finally {
			
		}
		
		try {
			XStream xs = new XStream();
			
			xs.alias("channel", XMLChannel.class);
			xs.aliasAttribute(XMLChannel.class, "displayName", "display-name");
			
			xs.alias("programme", XMLProgram.class);
			xs.aliasAttribute(XMLProgram.class, "start", "start");
			xs.aliasAttribute(XMLProgram.class, "stop", "stop");
			xs.aliasAttribute(XMLProgram.class, "channelId", "channel");
			xs.aliasAttribute(XMLProgram.class, "subTitle", "sub-title");
			xs.aliasAttribute(XMLProgram.class, "description", "desc");
			
			DAOFactory dbase = DAOFactory.getInstance(Epgd.config.database);
			ProgramDAO programDAO = dbase.getProgramDAO();
			Long lastId = 0L;
			
			ObjectInputStream ois = xs.createObjectInputStream(new FileInputStream("programtv.xml"));
			try {
				while(true) {
					try {
						Object ro = ois.readObject();
						if (ro instanceof XMLChannel) {
							XMLChannel rc = (XMLChannel)ro;
						} else if (ro instanceof XMLProgram) {
							XMLProgram rp = (XMLProgram)ro;
							
							String extChannelId = rp.getChannelId();
							Long mappedId = channelMap.get(extChannelId);
							
							if (mappedId != null) {
								Program prog = new Program(null, mappedId, rp.getTitle(), rp.getDescription(), new Timestamp(rp.getStartDate().getTime()), new Timestamp(rp.getStopDate().getTime()));
								try {
									programDAO.save(prog);
									lastId = prog.getId();
								} catch (DAOException e) {
									System.err.println("Database error: " + e.getMessage());
								}
							}
						}
					} catch (ClassNotFoundException e) {
						System.err.println("Unknown object in XMLTV file: " + e.getMessage());
					}
				}
			} catch (EOFException eof) {
				
			}
			ois.close();
		} catch (FileNotFoundException e) {
			System.err.println("XMLTV result file could not be opened." + e.getMessage());
		} catch (IOException e) {
			System.err.println("There is a problem with the XMLTV file: " + e.getMessage());
		}
	}

}
