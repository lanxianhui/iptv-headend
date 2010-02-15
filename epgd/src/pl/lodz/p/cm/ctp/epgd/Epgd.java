package pl.lodz.p.cm.ctp.epgd;

import java.io.*;

import org.apache.commons.daemon.*;
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
		}
		
		/* System.out.println(config.xmlTvGrabber);
		
		String arguments[] = config.xmlTvGrabber.arguments.split(" ");
		for (String argument : arguments) {
			System.out.println(argument);
		} */
		
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
			
			ObjectInputStream ois = xs.createObjectInputStream(new FileInputStream("programtv.xml"));
			try {
				while(true) {
					try {
						Object ro = ois.readObject();
						if (ro instanceof XMLChannel) {
							XMLChannel rc = (XMLChannel)ro;
							System.out.println("New channel found: " + rc.getDisplayName());
						} else if (ro instanceof XMLProgram) {
							XMLProgram rp = (XMLProgram)ro;
							System.out.println("New program found: " + rp.getTitle());
							System.out.println("Start: " + rp.getStartDate().toGMTString() + ", End: " + rp.getStopDate().toGMTString());
							System.out.println();
						}
					} catch (ClassNotFoundException e) {
						System.err.println("Unknown object in XMLTV file: " + e.getMessage());
					}
				}
			} catch (EOFException eof) {
				System.out.println("File ended.");
			}
			ois.close();
		} catch (FileNotFoundException e) {
			System.err.println("XMLTV result file could not be opened." + e.getMessage());
		} catch (IOException e) {
			System.err.println("There is a problem with the XMLTV file: " + e.getMessage());
		} finally {
			
		}
		
	}

}
