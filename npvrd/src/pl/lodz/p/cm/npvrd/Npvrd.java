package pl.lodz.p.cm.npvrd;

import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;

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
		
		for (int i = 0; i < args.length; i++) {
			System.out.println(args[i]);
			if (args[i].equals("-c")) {
				System.out.println("Plik konfiguracyjny: " + args[++i]);
				configFile = args[i];
			}
			if (args[i].equals("-g")) {
				System.out.println("Wybrana grupa: " + args[++i]);
				groupIp = args[i];
			}
			if (args[i].equals("-p")) {
				System.out.println("Wybrany port: " + args[++i]);
				groupPort = Integer.parseInt(args[i]);
			}
			if (args[i].equals("-o")) {
				System.out.println("Wybrany plik: " + args[++i]);
				targetFile = args[i];
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
		
		int counter = 0;
		try {
			InetAddress group = InetAddress.getByName(groupIp);
			MulticastSocket sock = new MulticastSocket(groupPort);
			sock.joinGroup(group);
			
			FileOutputStream fos = new FileOutputStream(targetFile);
			
			System.out.println("Odbieram strumien:");
			byte[] buf = new byte[sock.getReceiveBufferSize()];
			DatagramPacket recv = new DatagramPacket(buf, buf.length);
			
			while (counter < 100000000)
			{
				sock.receive(recv);
				
				recv.getData();
				fos.write(recv.getData());
				
				counter += buf.length;
				System.out.println(counter);
			}
			
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
