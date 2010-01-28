package pl.lodz.p.cm.npvrd;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class ChannelRecorder implements Runnable {
	
	private String groupIp;
	private int groupPort;
	public LinkedList<RecordingTask> programNagrywania;
	
	public ChannelRecorder(String groupIp, int groupPort) {
		this.groupIp = groupIp;
		this.groupPort = groupPort;
		this.programNagrywania = new LinkedList<RecordingTask>();
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
	
	private String generateFileName(RecordingTask task) {
		return "";
	}

	@Override
	public void run() {
		try {
			InetAddress group = InetAddress.getByName(groupIp);
			MulticastSocket sock = new MulticastSocket(groupPort);
			sock.joinGroup(group);
			
			
			while (!programNagrywania.isEmpty())
			{
				try {
					RecordingTask task = programNagrywania.removeLast();
					String fileName = generateFileName(task);
					
					FileOutputStream fos = new FileOutputStream(fileName);
					
					byte[] buf = new byte[sock.getReceiveBufferSize()];
					DatagramPacket recv = new DatagramPacket(buf, buf.length);
					
					long poczatekNagrywaniaLiczb = task.getPoczatekNagrywania().getTime();
					long koniecNagrywaniaLiczb = task.getKoniecNagrywania().getTime();
					
					System.out.println(fileName + ": Czekam na: " + task.getPoczatekNagrywania().toGMTString());
					
					while (System.currentTimeMillis() < poczatekNagrywaniaLiczb)
					{
						sock.receive(recv);
					}
					
					System.out.println(groupIp + ": Rozpoczynam nagrywanie");
					
					while (System.currentTimeMillis() < koniecNagrywaniaLiczb)
					{
						sock.receive(recv);
						fos.write(recv.getData(), 0, recv.getLength());
					}
					
					System.out.println(groupIp + ": Jest już: " + task.getPoczatekNagrywania().toGMTString());			
					System.out.println(groupIp + ": Koniec nagrywania");
					
					fos.close();
				} catch (IOException e) {
					System.err.println(groupIp + ": Plik docelowy jest niedostępny");
				}
			}
			
			sock.leaveGroup(group);
			sock.close();
		} catch (UnknownHostException e) {
			System.err.println(groupIp + ": Błędnie podany adres nasłuchowy");
		} catch (IOException e) {
			System.err.println(groupIp + ": Port nasłuchowy zajęty");
		}
	}

}
