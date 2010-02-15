package pl.lodz.p.cm.ctp.epgd;

import java.io.*;
import java.sql.Timestamp;
import pl.lodz.p.cm.ctp.dao.*;
import pl.lodz.p.cm.ctp.dao.model.Program;

import com.thoughtworks.xstream.XStream;

public class ProgramUpdater implements Runnable {
	
	public ProgramUpdater() {
		
	}

	@Override
	public void run() {
		try {
			String[] cmd = Epgd.config.xmlTvGrabber.commandLine.split(" ");
			
			try {
				Process p = Runtime.getRuntime().exec(cmd);
				p.waitFor();
			} catch (InterruptedException e1) {
				System.err.println("Woken up?");
				e1.printStackTrace();
			} catch (IOException io) {
				System.err.println("Unable to start xmltv grabber." + io.getMessage());
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
			
			ObjectInputStream ois = xs.createObjectInputStream(new FileInputStream(Epgd.config.xmlTvGrabber.resultFile));
			try {
				while(true) {
					try {
						Object ro = ois.readObject();
						if (ro instanceof XMLChannel) {
							
						} else if (ro instanceof XMLProgram) {
							XMLProgram rp = (XMLProgram)ro;
							
							String extChannelId = rp.getChannelId();
							Long mappedId = Epgd.channelMap.get(extChannelId);
							
							if (mappedId != null) {
								Program prog = new Program(null, mappedId, rp.getTitle(), rp.getDescription(), new Timestamp(rp.getStartDate().getTime()), new Timestamp(rp.getStopDate().getTime()));
								try {
									programDAO.save(prog);
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
			
			File usedXmltv = new File(Epgd.config.xmlTvGrabber.resultFile);
			usedXmltv.delete();
		} catch (FileNotFoundException e) {
			System.err.println("XMLTV result file could not be opened." + e.getMessage());
		} catch (IOException e) {
			System.err.println("There is a problem with the XMLTV file: " + e.getMessage());
		}		
	}
	
}
