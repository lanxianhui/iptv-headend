package pl.lodz.p.cm.ctp.epgd;

import java.io.*;
import java.util.Hashtable;

import com.thoughtworks.xstream.*;

import it.sauronsoftware.cron4j.Scheduler;

public class Epgd {
	
	static Configuration config;
	static volatile Hashtable<String, Long> channelMap;
	static volatile Scheduler scheduler;
	
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
		}
		
		// Setup the shutdown hook
		
		Thread runtimeHookThread = new Thread() {
			public void run() {
				shutdownHook();
			}
		};
		
		Runtime.getRuntime().addShutdownHook(runtimeHookThread);
		
		scheduler.schedule(Epgd.config.refresh, new ProgramUpdater());
		scheduler.start();
		
		try {
			while (true) {
				Thread.sleep(1000L * 60L * 10L);
				// We keep this thread running, so that the JVM will know that we are still running
			}
		} catch (Throwable t) {
			System.out.println("Exception " + t.getMessage());
		}
	}
	
	private static void shutdownHook() {
		scheduler.stop();
	}

}
