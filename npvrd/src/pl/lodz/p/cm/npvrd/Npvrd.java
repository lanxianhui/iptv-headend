package pl.lodz.p.cm.npvrd;

import java.io.FileNotFoundException;
import org.apache.commons.daemon.*;

import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.thoughtworks.xstream.*;

public class Npvrd implements Daemon {
	
	static DatabaseConfiguration config;
	static ArrayList<ChannelRecorder> channelRecorders; 
	static ArrayList<Thread> recorderThreads;

	/**
	 * @param args Arguments passed to the program in the command line.
	 */
	public static void main(String[] args) {
		String configFile = "config.xml";
		String groupIp = "224.0.0.1";
		int groupPort = 1234;
		Date recordingBegin = null;
		Date recordingEnd = null;
		Calendar cal = Calendar.getInstance();
		
		channelRecorders = new ArrayList<ChannelRecorder>();
		
		recordingBegin = cal.getTime();
		recordingEnd = new Date(recordingBegin.getTime());
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-c")) {
				configFile = args[++i];
			}
			else if (args[i].equals("-g")) {
				groupIp = args[++i];
			}
			else if (args[i].equals("-p")) {
				groupPort = Integer.parseInt(args[++i]);
			}
			else if (args[i].equals("-b")) {
				try {
					recordingBegin = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT).parse(args[++i]);
				} catch (ParseException e) {
					System.err.println(e.getMessage());
					System.err.println("Invalid date format.");
				}
			}
			else if (args[i].equals("-e")) {
				try {
					recordingEnd = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT).parse(args[++i]);
				} catch (ParseException e) {
					System.err.println(e.getMessage());
					System.err.println("Invalid date format.");
				}
			}
			else if (args[i].equals("-t")) {
				int seconds = Integer.parseInt(args[++i]);
				recordingEnd.setTime(recordingBegin.getTime() + (seconds * 1000));
			}
		}
		
		try {
			XStream xs = new XStream();
			FileInputStream fis = new FileInputStream(configFile);
			xs.alias("config", DatabaseConfiguration.class);
			
			config = (DatabaseConfiguration)xs.fromXML(fis);
		} catch (FileNotFoundException e) {
			System.err.println("Configuration file not found!");
		}
		
		ChannelRecorder NowyKanal = new ChannelRecorder(groupIp, groupPort);
		
		channelRecorders.add(NowyKanal);
		
		Thread RecordingThread = new Thread(NowyKanal);
		
		recorderThreads.add(RecordingThread);
		
		RecordingThread.start();
		
		while (RecordingThread.isAlive())
		{
			
		}
		
		System.out.println("Finished");
	}

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

}
