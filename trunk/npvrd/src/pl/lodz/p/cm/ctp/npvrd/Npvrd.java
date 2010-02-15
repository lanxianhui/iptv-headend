package pl.lodz.p.cm.ctp.npvrd;

import java.io.*;
import pl.lodz.p.cm.ctp.dao.*;
import pl.lodz.p.cm.ctp.dao.model.*;

import java.text.SimpleDateFormat;
import java.util.*;

import com.thoughtworks.xstream.*;

public class Npvrd {
	
	static class ChannelRecorderThread {
		public Thread thread;
		public ChannelRecorder channelRecorder;
		
		public ChannelRecorderThread(Thread thread, ChannelRecorder channelRecorder) {
			this.thread = thread;
			this.channelRecorder = channelRecorder;
		}
	}
	
	static Configuration config;
	static ArrayList<ChannelRecorderThread> channelRecorders;
	
	public Npvrd() {
		channelRecorders = new ArrayList<ChannelRecorderThread>();
	}
	
	public static boolean isAnyRecorderAlive() {
		for(ChannelRecorderThread threadRecorder : channelRecorders) {
			if (threadRecorder.thread.isAlive())
				return true;
		}
		return false;
	}
	
	public static void wakeUpAllRecorders() {
		for (ChannelRecorderThread threadRecorder : channelRecorders) {
			threadRecorder.thread.interrupt();
		}
	}
	
	public static void setRunModesRecorders(ChannelRecorder.RunMode newMode) {
		for (ChannelRecorderThread threadRecorder : channelRecorders) {
			threadRecorder.channelRecorder.setRunMode(newMode);
		}
	}

	/**
	 * @param args Arguments passed to the program in the command line.
	 */
	public static void main(String[] args) {
		String configFile = "config.xml";
		channelRecorders = new ArrayList<ChannelRecorderThread>();
		
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
		
		DAOFactory dbase = DAOFactory.getInstance(config.database);
		TvChannelDAO tvChannelDAO = dbase.getTvChannelDAO();
        
        try {
			List<TvChannel> tvChannelList = tvChannelDAO.list();
			log("Got " + tvChannelList.size() + " TV channels. Creating recorders for channels.");
			
			for (TvChannel tvChannel : tvChannelList) {
				ChannelRecorder NewChannel = new ChannelRecorder(tvChannel.getId(), tvChannel.getIpAdress(), tvChannel.getPort());
				Thread RecordingThread = new Thread(NewChannel);
				channelRecorders.add(new ChannelRecorderThread(RecordingThread, NewChannel));
				RecordingThread.start();
			}
		} catch (DAOException e1) {
			error("Database error: " + e1.getMessage());
			error("This is a critical error. Terminating.");
			System.exit(1);
		}
		
		// Setup the shutdown hook
		
		Thread runtimeHookThread = new Thread() {
			public void run() {
				shutdownHook();
			}
		};
		
		Runtime.getRuntime().addShutdownHook(runtimeHookThread);
		
		try {
			while (isAnyRecorderAlive()) {
				Thread.sleep(1000L * 60L * 10L);
				// We keep this thread running, so that the JVM will know that we are still running
			}
		} catch (Throwable t) {
			error("Exception " + t.getMessage());
		}
	}
	
	public static void shutdownHook() {
		log("Shutting down at user request.");
		setRunModesRecorders(ChannelRecorder.RunMode.STOP);
		wakeUpAllRecorders();
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
