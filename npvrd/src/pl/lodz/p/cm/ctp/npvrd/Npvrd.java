package pl.lodz.p.cm.ctp.npvrd;

import java.io.*;

import org.apache.commons.daemon.*;
import java.util.*;
import com.thoughtworks.xstream.*;

public class Npvrd implements Daemon {
	
	static Configuration config;
	static ArrayList<Thread> recorderThreads;
	static ArrayList<ChannelRecorder> channelRecorders;
	
	public static boolean isAnyRecorderAlive() {
		Iterator<Thread> recordersIterator = recorderThreads.iterator();
		while(recordersIterator.hasNext()) {
			if (recordersIterator.next().isAlive())
				return true;
		}
		return false;
	}
	
	public static void setRunModesRecorders(ChannelRecorder.RunMode newMode) {
		Iterator<ChannelRecorder> channelsIterator = channelRecorders.iterator();
		while(channelsIterator.hasNext()) {
			channelsIterator.next().setRunMode(newMode);
		}
	}

	/**
	 * @param args Arguments passed to the program in the command line.
	 */
	public static void main(String[] args) {
		String configFile = "config.xml";
		
		recorderThreads = new ArrayList<Thread>();
		channelRecorders = new ArrayList<ChannelRecorder>();
		
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
		
		// TODO Read all recordable channels from the database and create ChannelRecorders for them.
		
		ChannelRecorder NewChannel = new ChannelRecorder("239.192.0.1", 1234);
		channelRecorders.add(NewChannel);
		Thread RecordingThread = new Thread(NewChannel);
		recorderThreads.add(RecordingThread);
		
		RecordingThread.start();
		
		String curLine = "";
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader console = new BufferedReader(converter);
		
		System.out.println("npvrd in interactive command-line mode:");
		
		while (isAnyRecorderAlive())
		{
			/* try {
				Thread.sleep(10000); // Sleeping for 10 secs.
			} catch (InterruptedException e) {
				System.err.println("Monitor thread: woken up for no apparent reason?");
			} */
			
			while (!(curLine.equals("quit"))) {
				try {
					System.out.print("#: ");
					curLine = console.readLine();
					if (curLine.equals("quit")) {
						System.out.println("Terminating all threads...");
						setRunModesRecorders(ChannelRecorder.RunMode.STOP);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}            
			}

		}
		
		System.out.println("Finished. All recorder threads dead.");
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
