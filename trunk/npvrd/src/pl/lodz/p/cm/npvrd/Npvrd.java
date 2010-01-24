package pl.lodz.p.cm.npvrd;

import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.thoughtworks.xstream.*;

public class Npvrd {
	
	static DatabaseConfiguration config;

	/**
	 * @param args Argumenty przekazane do programu przez linię komend
	 */
	public static void main(String[] args) {
		XStream xs = new XStream();
		String configFile = "config.xml";
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
				koniecNagrywania.setTime(koniecNagrywania.getTime() + (seconds * 1000));
			}
		}
		
		try {
			FileInputStream fis = new FileInputStream(configFile);
			xs.alias("config", DatabaseConfiguration.class);
			
			config = (DatabaseConfiguration)xs.fromXML(fis);

	        System.out.println(config.toString());
		} catch (FileNotFoundException e) {
			System.out.println("Nie znalazłem pliku konfiguracyjnego!");
		}
		
		try {
			InetAddress group = InetAddress.getByName(groupIp);
			MulticastSocket sock = new MulticastSocket(groupPort);
			sock.joinGroup(group);
			
			FileOutputStream fos = new FileOutputStream(targetFile);
			
			byte[] buf = new byte[sock.getReceiveBufferSize()];
			DatagramPacket recv = new DatagramPacket(buf, buf.length);
			
			System.out.println("Czekam na: " + poczatekNagrywania.toGMTString());
			
			while (System.currentTimeMillis() < poczatekNagrywania.getTime())
			{
				
			}
			
			System.out.println("Rozpoczynam nagrywanie.");
			
			while (System.currentTimeMillis() < koniecNagrywania.getTime())
			{
				sock.receive(recv);
				
				recv.getData();
				fos.write(recv.getData());
			}
			
			System.out.println("Jest już: " + koniecNagrywania.toGMTString());
			
			System.out.println("Koniec nagrywania.");
			
			fos.close();
			sock.leaveGroup(group);
			sock.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.err.println("Błędnie podany adres nasłuchowy");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Port nasłuchowy zajęty");
		}
		
	}

}
