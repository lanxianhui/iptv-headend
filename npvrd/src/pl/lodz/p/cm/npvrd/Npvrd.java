package pl.lodz.p.cm.npvrd;

import java.io.FileNotFoundException;
import java.io.FileInputStream;
import com.thoughtworks.xstream.*;

public class Npvrd {

	/**
	 * @param args Argumenty przekazane do programu przez linię komend
	 */
	public static void main(String[] args) {
		XStream xs = new XStream();
		String configFile = "config.xml";
		
		for (int i = 0; i < args.length; i++) {
			System.out.println(args[i]);
			if (args[i].equals("-c")) {
				System.out.println("Plik konfiguracyjny: " + args[++i]);
				configFile = args[i];
			}
		}
		
		try {
			FileInputStream fis = new FileInputStream(configFile);
			xs.alias("config", Npvrd.class);
			
			DatabaseConfiguration dbc = (DatabaseConfiguration)xs.fromXML(fis);

	        System.out.println(dbc.toString());
		} catch (FileNotFoundException e) {
			System.out.println("Nie znalazłem pliku konfiguracyjnego!");
		}
		
	}

}
