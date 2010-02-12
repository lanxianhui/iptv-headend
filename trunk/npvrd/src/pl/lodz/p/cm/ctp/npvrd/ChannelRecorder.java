package pl.lodz.p.cm.ctp.npvrd;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class ChannelRecorder implements Runnable {
	
	private String groupIp;
	private int groupPort;
	private LinkedList<RecordingTask> recordingSchedule;
	
	/**
	 * Creates a new channel recorder listening on a given multicast IP address and port.
	 * @param groupIp Multicast group IP to listen to.
	 * @param groupPort UDP Port to listen on.
	 */
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
	
	/**
	 * Add a task for a program to record.
	 * @param task A task to be added to the list
	 */
	public boolean add(RecordingTask task) {
		boolean result = recordingSchedule.add(task);
		if (result) Collections.sort(recordingSchedule);
		return result;
	}
	
	public boolean remove(RecordingTask task) {
		return recordingSchedule.remove(task);
	}
	
	public boolean removeAll(Collection<RecordingTask> tasks) {
		return recordingSchedule.removeAll(tasks);
	}
	
	public void clear() {
		recordingSchedule.clear();
	}
	
	public Collection<RecordingTask> getTasks() {
		return this.recordingSchedule;
	}
	
	/**
	 * Generates a unique file name for a program. Uses a MD5 digest on begining and
	 * end times of the task and the task name.
	 * @param task Task to generate the name for.
	 * @return A unique file name.
	 */
	private String generateFileName(RecordingTask task) {
		String plaintext = task.getRecordingBegin().toString() + task.getRecordingEnd().toString() + task.getProgramName();
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
			System.out.println(groupIp + ": Setting up socket.");
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
					
					System.out.println(groupIp + ": Waiting for: " + task.getRecordingBegin().toString());
					
					try {
						Thread.sleep(poczatekNagrywaniaLiczb - System.currentTimeMillis() - 10);
					} catch (InterruptedException e) {
						System.err.println(groupIp + ": Woken up while waiting, what's up?");
						e.printStackTrace();
					}
					
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
					
					System.out.println(groupIp + ": Current time: " + task.getRecordingBegin().toString());			
					System.out.println(groupIp + ": Recording ends");
					
					fos.close();
				} catch (IOException e) {
					System.err.println(groupIp + ": Destination file is unavailable");
				}
			}
			
			System.out.println(groupIp + ": Nothing to do.");
			
			sock.leaveGroup(group);
			sock.close();
		} catch (UnknownHostException e) {
			System.err.println(groupIp + ": Group IP address invalid");
		} catch (IOException e) {
			System.err.println(groupIp + ": Listening port busy");
		}
	}

}
