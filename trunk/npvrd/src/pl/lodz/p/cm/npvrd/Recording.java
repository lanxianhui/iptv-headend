package pl.lodz.p.cm.npvrd;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Date;

public class Recording implements Runnable {
	
	private String groupIp;
	private int groupPort;
	private String fileName;
	private Date poczatekNagrywania;
	private Date koniecNagrywania;
	
	public Recording(String groupIp, int groupPort, String fileName, Date poczatekNagrywania, Date koniecNagrywania) {
		this.groupIp = groupIp;
		this.groupPort = groupPort;
		this.fileName = fileName;
		this.poczatekNagrywania = poczatekNagrywania;
		this.koniecNagrywania = koniecNagrywania;
	}

	public String getGroupIp() {
		return groupIp;
	}

	public void setGroupIp(String groupIp) {
		this.groupIp = groupIp;
	}

	public int getGroupPort() {
		return groupPort;
	}

	public void setGroupPort(int groupPort) {
		this.groupPort = groupPort;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getPoczatekNagrywania() {
		return poczatekNagrywania;
	}

	public void setPoczatekNagrywania(Date poczatekNagrywania) {
		this.poczatekNagrywania = poczatekNagrywania;
	}

	public Date getKoniecNagrywania() {
		return koniecNagrywania;
	}

	public void setKoniecNagrywania(Date koniecNagrywania) {
		this.koniecNagrywania = koniecNagrywania;
	}

	@Override
	public void run() {
		try {
			InetAddress group = InetAddress.getByName(groupIp);
			MulticastSocket sock = new MulticastSocket(groupPort);
			sock.joinGroup(group);
			
			try {
				FileOutputStream fos = new FileOutputStream(fileName);
				
				byte[] buf = new byte[sock.getReceiveBufferSize()];
				DatagramPacket recv = new DatagramPacket(buf, buf.length);
				
				System.out.println(fileName + ": Czekam na: " + poczatekNagrywania.toGMTString());
				
				while (System.currentTimeMillis() < poczatekNagrywania.getTime())
				{
					
				}
				
				System.out.println(fileName + ": Rozpoczynam nagrywanie");
				
				while (System.currentTimeMillis() < koniecNagrywania.getTime())
				{
					sock.receive(recv);
					
					recv.getData();
					fos.write(recv.getData());
				}
				
				System.out.println(fileName + ": Jest już: " + koniecNagrywania.toGMTString());			
				System.out.println(fileName + ": Koniec nagrywania");
				
				fos.close();
			} catch (IOException e) {
				System.err.println(fileName + ": Plik docelowy jest niedostępny");
			}
			sock.leaveGroup(group);
			sock.close();
		} catch (UnknownHostException e) {
			System.err.println(fileName + ": Błędnie podany adres nasłuchowy");
		} catch (IOException e) {
			System.err.println(fileName + ": Port nasłuchowy zajęty");
		}
	}

}
