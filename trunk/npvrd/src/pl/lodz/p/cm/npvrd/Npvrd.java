package pl.lodz.p.cm.npvrd;

import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.thoughtworks.xstream.*;

public class Npvrd {
	
	static DatabaseConfiguration config;
	static ArrayList<Recording> recordingsList; 

	/**
	 * @param args Argumenty przekazane do programu przez linię komend
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		String configFile = "config.xml";
		String recordFile = "recordings.xml";
		String groupIp = "224.0.0.1";
		int groupPort = 1234;
		String targetFile = "stream.ts";
		Date poczatekNagrywania = null;
		Date koniecNagrywania = null;
		Calendar cal = Calendar.getInstance();
		
		poczatekNagrywania = cal.getTime();
		koniecNagrywania = new Date(poczatekNagrywania.getTime());
		
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
			else if (args[i].equals("-o")) {
				targetFile = args[++i];
			}
			else if (args[i].equals("-b")) {
				try {
					poczatekNagrywania = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT).parse(args[++i]);
				} catch (ParseException e) {
					System.err.println(e.getMessage());
					System.err.println("Błędny format daty rozpoczęcia nagrywania.");
				}
			}
			else if (args[i].equals("-e")) {
				try {
					koniecNagrywania = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT).parse(args[++i]);
				} catch (ParseException e) {
					System.err.println(e.getMessage());
					System.err.println("Błędny format daty zakończenia nagrywania.");
				}
			}
			else if (args[i].equals("-t")) {
				int seconds = Integer.parseInt(args[++i]);
				koniecNagrywania.setTime(poczatekNagrywania.getTime() + (seconds * 1000));
			}
		}
		
		try {
			XStream xs = new XStream();
			FileInputStream fis = new FileInputStream(configFile);
			xs.alias("config", DatabaseConfiguration.class);
			
			config = (DatabaseConfiguration)xs.fromXML(fis);
		} catch (FileNotFoundException e) {
			System.err.println("Nie znalazłem pliku konfiguracyjnego!");
		}
		
		Recording NoweNagranie = new Recording(groupIp, groupPort, targetFile, poczatekNagrywania, koniecNagrywania);
		
		Thread RecordingThread = new Thread(NoweNagranie);
		RecordingThread.start();
		
		while (RecordingThread.isAlive())
		{
			
		}
		
		System.out.println("Zakończone");
	}

}
