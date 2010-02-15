package pl.lodz.p.cm.ctp.npvrd;

import java.io.*;
import pl.lodz.p.cm.ctp.dao.*;
import pl.lodz.p.cm.ctp.dao.model.*;
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
		
		DAOFactory dbase = DAOFactory.getInstance(config.database);
		TvChannelDAO tvChannelDAO = dbase.getTvChannelDAO();
        
        try {
			List<TvChannel> tvChannelList = tvChannelDAO.list();
			System.out.println("Got " + tvChannelList.size() + " TV channels. Creating recorders for channels.");
			
			for (TvChannel tvChannel : tvChannelList) {
				ChannelRecorder NewChannel = new ChannelRecorder(tvChannel.getId(), tvChannel.getIpAdress(), tvChannel.getPort());
				Thread RecordingThread = new Thread(NewChannel);
				channelRecorders.add(new ChannelRecorderThread(RecordingThread, NewChannel));
				RecordingThread.start();
			}
		} catch (DAOException e1) {
			System.err.println("Database error: " + e1.getMessage());
			System.err.println("This is a critical error. Terminating.");
			System.exit(1);
		}
		
		String curLine = "";
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader console = new BufferedReader(converter);
		
		System.out.println("npvrd in interactive command-line mode:");
		
		while (isAnyRecorderAlive())
		{
			while (!(curLine.equals("quit"))) {
				try {
					System.out.print("#: ");
					curLine = console.readLine();
					if (curLine.equals("quit")) {
						System.out.println("Terminating all threads...");
						setRunModesRecorders(ChannelRecorder.RunMode.STOP);
						wakeUpAllRecorders();
					}
				} catch (IOException e) {

				}            
			}

		}
		
		System.out.println("Finished. All recorder threads dead.");
	}

}
