package pl.lodz.p.cm.ctp.npvrd;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.*;
import pl.lodz.p.cm.ctp.dao.*;

public class ChannelRecorder implements Runnable {
	public enum RunMode { RUN, STOP };
	
	private String groupIp;
	private int groupPort;
	private LinkedList<RecordingTask> recordingSchedule;
	private Lock recordingScheduleLock = new ReentrantLock();
	private RunMode runMode;
	
	/**
	 * Creates a new channel recorder listening on a given multicast IP address and port.
	 * @param groupIp Multicast group IP to listen to.
	 * @param groupPort UDP Port to listen on.
	 */
	public ChannelRecorder(String groupIp, int groupPort) {
		this.groupIp = groupIp;
		this.groupPort = groupPort;
		this.recordingSchedule = new LinkedList<RecordingTask>();
		this.runMode = RunMode.RUN;
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
	
	public RunMode getRunMode() {
		synchronized (this) {
			return this.runMode;
		}
	}
	
	public void setRunMode(RunMode newMode) {
		synchronized (this) {
			this.runMode = newMode;
		}
	}
	
	/**
	 * Add a task for a program to record.
	 * @param task A task to be added to the list
	 */
	public boolean add(RecordingTask task) {
		boolean result = false;
		
		recordingScheduleLock.lock();
		try {
			result = recordingSchedule.add(task);
			if (result) Collections.sort(recordingSchedule);
		} finally {
			recordingScheduleLock.unlock();
		}
		return result;
	}
	
	public boolean remove(RecordingTask task) {
		boolean result = false;
		
		recordingScheduleLock.lock();
		try {
			result = recordingSchedule.remove(task);
		} finally {
			recordingScheduleLock.unlock();
		}
		return result;
		
	}
	
	public boolean removeAll(Collection<RecordingTask> tasks) {
		boolean result = false;
		
		recordingScheduleLock.lock();
		try {
			result = recordingSchedule.removeAll(tasks);
		} finally {
			recordingScheduleLock.unlock();
		}
		return result;
	}
	
	public void clear() {
		recordingScheduleLock.lock();
		try {
			recordingSchedule.clear();
		} finally {
			recordingScheduleLock.unlock();
		}
	}
	
	public boolean isEmpty() {
		boolean result = true;
		
		recordingScheduleLock.lock();
		try {
			result = recordingSchedule.isEmpty();
		} finally {
			recordingScheduleLock.unlock();
		}
		
		return result;
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
		return DAOUtil.hashMD5(plaintext) + ".m2t";
	}

	@Override
	public void run() {
		try {
			System.out.println(groupIp + ": Setting up socket.");
			InetAddress group = InetAddress.getByName(groupIp);
			MulticastSocket sock = new MulticastSocket(groupPort);
			sock.joinGroup(group);
			
			
			while (this.getRunMode() == RunMode.RUN)
			{
				try {
					if (!this.isEmpty()) {
						RecordingTask task = null;
						recordingScheduleLock.lock();
						try {
							task = recordingSchedule.removeLast();
						} finally {
							recordingScheduleLock.unlock();
						}
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
						
						System.out.println(groupIp + ": Recording starts for " + task.getProgramName());
						
						while (System.currentTimeMillis() < koniecNagrywaniaLiczb)
						{
							sock.receive(recv);
							fos.write(recv.getData(), 0, recv.getLength());
						}
						
						System.out.println(groupIp + ": Current time: " + task.getRecordingBegin().toString());			
						System.out.println(groupIp + ": Recording ends");
						
						fos.close();
					} else {
						try {
							System.out.println(groupIp + ": Temporarly going to sleep, task list empty.");
							Thread.sleep(100000);
						} catch (InterruptedException e) {
							System.out.println(groupIp + ": Woken up.");
						}
					}
				} catch (IOException e) {
					System.err.println(groupIp + ": Destination file is unavailable");
				}
			}
			
			System.out.println(groupIp + ": Thread terminating.");
			
			sock.leaveGroup(group);
			sock.close();
		} catch (UnknownHostException e) {
			System.err.println(groupIp + ": Group IP address invalid");
		} catch (IOException e) {
			System.err.println(groupIp + ": Listening port busy");
		}
	}

}
