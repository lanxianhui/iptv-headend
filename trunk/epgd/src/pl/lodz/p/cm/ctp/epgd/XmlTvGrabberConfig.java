package pl.lodz.p.cm.ctp.epgd;

import java.io.Serializable;

public class XmlTvGrabberConfig implements Serializable {

	private static final long serialVersionUID = 4224614237610436092L;
	public String commandLine;
	public String arguments;
	public String resultFile;
	public String mapFile;
	
	public String toString() {
		return commandLine + " " + arguments + " , " + resultFile;
	}

}
