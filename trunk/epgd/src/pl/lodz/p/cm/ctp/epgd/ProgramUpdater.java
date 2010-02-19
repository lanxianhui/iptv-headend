package pl.lodz.p.cm.ctp.epgd;

import java.io.*;
import java.sql.Timestamp;
import java.util.Hashtable;

import pl.lodz.p.cm.ctp.dao.*;
import pl.lodz.p.cm.ctp.dao.model.Program;

import com.thoughtworks.xstream.XStream;

public class ProgramUpdater implements Runnable {
	
	private XmlTvGrabberConfig myConfig;
	private Hashtable<String, Long> channelMap;
	
	public ProgramUpdater(XmlTvGrabberConfig config) {
		this.myConfig = config;
		
		this.channelMap = new Hashtable<String, Long>();
		
		try {
			XStream xs = new XStream();
			xs.alias("map", XMLMap.class);
			xs.aliasAttribute(XMLMap.class, "externalId", "extId");
			xs.aliasAttribute(XMLMap.class, "internalId", "dbId");
			xs.aliasAttribute(XMLMap.class, "name", "name");
			
			ObjectInputStream ois = xs.createObjectInputStream(new FileInputStream(myConfig.mapFile));
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
			Epgd.error("XMLTV mapping file could not be opened." + e.getMessage());
		} catch (IOException e) {
			Epgd.error("There is a problem with the XMLTV mapping file: " + e.getMessage());
		}
	}

	@Override
	public void run() {
		try {
			Epgd.log("Starting XMLTV grabber");
			String[] cmd = myConfig.commandLine.split(" ");
			
			try {
				Process p = Runtime.getRuntime().exec(cmd);
				p.waitFor();
			} catch (InterruptedException e1) {
				Epgd.error("Woken up?");
				e1.printStackTrace();
			} catch (IOException io) {
				Epgd.error("Unable to start xmltv grabber." + io.getMessage());
				return;
			}
			
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
			
			ObjectInputStream ois = xs.createObjectInputStream(new FileInputStream(myConfig.resultFile));
			int progCounter = 0;
			try {
				while(true) {
					try {
						Object ro = ois.readObject();
						if (ro instanceof XMLChannel) {
							
						} else if (ro instanceof XMLProgram) {
							XMLProgram rp = (XMLProgram)ro;
							
							String extChannelId = rp.getChannelId();
							Long mappedId = channelMap.get(extChannelId);
							
							if (mappedId != null) {
								Program prog = new Program(null, mappedId, rp.getTitle(), rp.getDescription(), new Timestamp(rp.getStartDate().getTime()), new Timestamp(rp.getStopDate().getTime()));
								try {
									programDAO.save(prog);
									progCounter++;
								} catch (DAOException e) {
									Epgd.error("Database error: " + e.getMessage());
								}
							}
						}
					} catch (ClassNotFoundException e) {
						Epgd.error("Unknown object in XMLTV file: " + e.getMessage());
					}
				}
			} catch (EOFException eof) {
				
			}
			ois.close();
			
			Epgd.log("Successfully uploaded " + progCounter + " programs to EPG database.");
			
			File usedXmltv = new File(myConfig.resultFile);
			usedXmltv.delete();
		} catch (FileNotFoundException e) {
			Epgd.error("XMLTV result file could not be opened." + e.getMessage());
		} catch (IOException e) {
			Epgd.error("There is a problem with the XMLTV file: " + e.getMessage());
		}		
	}
	
}
