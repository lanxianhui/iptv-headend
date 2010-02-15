package pl.lodz.p.cm.ctp.epgd;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;

import com.thoughtworks.xstream.XStream;

public class Epgd implements Daemon {
	
	static Configuration config;

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

	/**
	 * @param args
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
		}
		
		System.out.println(config.xmlTvGrabber);
		
	}

}
