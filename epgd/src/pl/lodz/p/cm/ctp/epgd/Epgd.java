package pl.lodz.p.cm.ctp.epgd;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import com.thoughtworks.xstream.*;

import it.sauronsoftware.cron4j.Scheduler;

public class Epgd {
	
	static Configuration config;
	static volatile Hashtable<String, Long> channelMap;
	static volatile Scheduler scheduler;
	
	public static void main(String[] args) {
		String configFile = "config.xml";
		channelMap = new Hashtable<String, Long>();
		scheduler = new Scheduler();
		
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
			error("Configuration file not found! This is a critical problem, shuting down.");
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
			error("XMLTV mapping file could not be opened." + e.getMessage());
		} catch (IOException e) {
			error("There is a problem with the XMLTV mapping file: " + e.getMessage());
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
			error("Exception " + t.getMessage());
		}
	}
	
	private static void shutdownHook() {
		log("Shutting down at user request.");
		scheduler.stop();
	}
	
	static void error(String msg) {
		System.err.println(getTimeStamp() + " " + msg);
	}
	
	static void log(String msg) {
		System.out.println(getTimeStamp() + " " + msg);
	}

	private static String getTimeStamp() {
	   SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   return f.format(new Date());
	}

}
