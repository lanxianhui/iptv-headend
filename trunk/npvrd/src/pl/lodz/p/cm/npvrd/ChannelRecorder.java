package pl.lodz.p.cm.npvrd;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;

public class ChannelRecorder implements Runnable {
	
	private String groupIp;
	private int groupPort;
	public LinkedList<RecordingTask> recordingSchedule;
	
	public ChannelRecorder(String groupIp, int groupPort) {
		this.groupIp = groupIp;
		this.groupPort = groupPort;
		this.recordingSchedule = new LinkedList<RecordingTask>();
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
		String plaintext = task.getRecordingBegin().toGMTString() + task.getRecordingEnd().toGMTString() + task.getProgramName();
		MessageDigest m;
		try {
			m = MessageDigest.getInstance("MD5");
			m.reset();
			m.update(plaintext.getBytes());
			byte[] digest = m.digest();
			BigInteger bigInt = new BigInteger(1,digest);
			String hashtext = bigInt.toString(16);

			while(hashtext.length() < 32 ){
			  hashtext = "0"+hashtext;
			}
			return hashtext + ".m2t";
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			return "stream.m2t";
		}
	}

	@Override
	public void run() {
		try {
			InetAddress group = InetAddress.getByName(groupIp);
			MulticastSocket sock = new MulticastSocket(groupPort);
			sock.joinGroup(group);
			
			
			while (!recordingSchedule.isEmpty())
			{
				try {
					RecordingTask task = recordingSchedule.removeLast();
					String fileName = generateFileName(task);
					
					FileOutputStream fos = new FileOutputStream(fileName);
					
					byte[] buf = new byte[sock.getReceiveBufferSize()];
					DatagramPacket recv = new DatagramPacket(buf, buf.length);
					
					long poczatekNagrywaniaLiczb = task.getRecordingBegin().getTime();
					long koniecNagrywaniaLiczb = task.getRecordingEnd().getTime();
					
					System.out.println(fileName + ": Waiting for: " + task.getRecordingBegin().toGMTString());
					
					while (System.currentTimeMillis() < poczatekNagrywaniaLiczb)
					{
						sock.receive(recv);
					}
					
					System.out.println(groupIp + ": Recording starts");
					
					while (System.currentTimeMillis() < koniecNagrywaniaLiczb)
					{
						sock.receive(recv);
						fos.write(recv.getData(), 0, recv.getLength());
					}
					
					System.out.println(groupIp + ": Current time: " + task.getRecordingBegin().toGMTString());			
					System.out.println(groupIp + ": Recording ends");
					
					fos.close();
				} catch (IOException e) {
					System.err.println(groupIp + ": Destination file is unavailable");
				}
			}
			
			sock.leaveGroup(group);
			sock.close();
		} catch (UnknownHostException e) {
			System.err.println(groupIp + ": Group IP address invalid");
		} catch (IOException e) {
			System.err.println(groupIp + ": Listening port busy");
		}
	}

}
