package pl.lodz.p.cm.ctp.npvrd;

import java.io.*;
import java.net.*;
import java.util.*;
//import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.*;
import pl.lodz.p.cm.ctp.dao.*;
import pl.lodz.p.cm.ctp.dao.model.Recording.Mode;

public class ChannelRecorder implements Runnable {
	public enum RunMode { RUN, STOP };
	
	private long channelId;
	private String groupIp;
	private int groupPort;
	private LinkedList<RecordingTask> recordingSchedule;
	private Thread scheduleUpdater;
	private Lock recordingScheduleLock = new ReentrantLock();
	private volatile boolean recheckSchedule; 
	private RunMode runMode;
	
	private MulticastSocket sock;
	private InetAddress group;
	
	/**
	 * Creates a new channel recorder listening on a given multicast IP address and port.
	 * @param groupIp Multicast group IP to listen to.
	 * @param groupPort UDP Port to listen on.
	 */
	public ChannelRecorder(long channelId, String groupIp, int groupPort) {
		this.channelId = channelId;
		this.groupIp = groupIp;
		this.groupPort = groupPort;
		this.recordingSchedule = new LinkedList<RecordingTask>();
		this.runMode = RunMode.RUN;
		
		// Synchronous socket setup to solve crosstalk issues
		Npvrd.log(groupIp + ": Setting up socket.");
		try {
			this.group = InetAddress.getByName(this.groupIp);
			this.sock = new MulticastSocket(this.groupPort);
			this.sock.setLoopbackMode(true);
			this.sock.setSoTimeout(10000);
			this.sock.joinGroup(group);
		} catch (UnknownHostException e) {
			Npvrd.error(groupIp + ": Group IP address invalid: " + e.getMessage());
		} catch (IOException e) {
			Npvrd.error(groupIp + ": Error while trying to set-up socket: " + e.getMessage());
		}
	}
	
	public long getChannelId() {
		return channelId;
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
	
	public boolean getRecheckSchedule() {
		return this.recheckSchedule;
	}
	
	public void setRecheckSchedule(boolean recheck) {
		this.recheckSchedule = recheck;
	}
	
	public RunMode getRunMode() {
		return this.runMode;
	}
	
	public void setRunMode(RunMode newMode) {
		this.runMode = newMode;
	}
	
	/**
	 * Add a task for a program to record.
	 * @param task A task to be added to the list
	 */
	public boolean add(RecordingTask task) {
		boolean result = false;
		
		result = recordingSchedule.add(task);
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
	
	public void lockSchedule() {
		this.recordingScheduleLock.lock();
	}
	
	public void unlockSchedule() {
		this.recordingScheduleLock.unlock();
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
		return DAOUtil.hashMD5(plaintext) + ".ts";
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		try {
			// Make the file available
			Npvrd.log(groupIp + ": Creating a VLM manager.");
            VlmManager vlm = new VlmManager(Npvrd.config.vlm);
			
			ScheduleUpdater updater = new ScheduleUpdater(this, Thread.currentThread());
			this.scheduleUpdater = new Thread(updater);
			this.scheduleUpdater.start();
			
			while (this.getRunMode().equals(RunMode.RUN)) {
				RecordingTask task = null;
				while ((this.getRecheckSchedule()) && (!this.isEmpty())) {
					this.setRecheckSchedule(false);
				
					recordingScheduleLock.lock();
					try {
						if (task != null) {
							if (recordingSchedule.peekFirst().compareTo(task) < 0) {
								task.setState(Mode.AVAILABLE);
								task.saveToDatabase();
								task = recordingSchedule.removeFirst();
							}
						} else {
							task = recordingSchedule.removeFirst();
						}
					} finally {
						recordingScheduleLock.unlock();
					}
					
					long endRecordingNum = task.getRecordingEnd().getTime();
					
					if (endRecordingNum > System.currentTimeMillis()) {
						String fileName = generateFileName(task);
						Mode fileMode = Mode.PROCESSING;
						task.setResultFileName(fileName);
						task.setState(fileMode);
						task.saveToDatabase();
						String path = Npvrd.config.recordings;
						
						Npvrd.log(groupIp + ": New task: " + task.getProgramName() + " at " + task.getRecordingBegin().toGMTString());
						
						fileMode = Mode.UNAVAILABLE;
						
						OutputStream fos = new BufferedOutputStream(new FileOutputStream(path + fileName));
						
						MulticastTimedListener multicastListener = new MulticastTimedListener(task.getRecordingBegin(), task.getRecordingEnd(), fos, this.group, this.sock);
						
						Thread listenerThread = new Thread(multicastListener);
						
						listenerThread.start();
						
						Npvrd.log(groupIp + ": Threads up and running, waiting for them to terminate.");
						
						try {
							listenerThread.join();
						} catch (InterruptedException ie) {
							Npvrd.log(groupIp + ": Interrupted while waiting, passing interrupt so that Listener can close.");
							listenerThread.interrupt();
						}
						
						Npvrd.log(groupIp + ": Listener thread has terminated, checking results.");
						
						if (multicastListener.getResult().equals(MulticastTimedListener.Result.OK)) {
							fileMode = Mode.AVAILABLE;
							Npvrd.log(groupIp + ": Listener reported OK.");
						} else if (multicastListener.getResult().equals(MulticastTimedListener.Result.ABORTED)) {
							fileMode = Mode.WAITING;
							Npvrd.log(groupIp + ": Listener reported recording aborted before start.");
						} else if (multicastListener.getResult().equals(MulticastTimedListener.Result.ERROR)) {
							fileMode = Mode.UNAVAILABLE;
							Npvrd.log(groupIp + ": Listener reported Error.");
						} else {
							fileMode = Mode.UNAVAILABLE;
							Npvrd.log(groupIp + ": Ooops! Seems like we've interrupted listener in a bad moment.");
						}

						// Announce that the file is available
						vlm.createNewVod(fileName, path + fileName);
						
						task.setState(fileMode);
						task.setResultFileName(fileName);
						task.saveToDatabase();
					}
				}
				
				if (this.getRunMode().equals(RunMode.RUN) && this.isEmpty() && !this.getRecheckSchedule()) {
					try {
						Thread.sleep(100000);
					} catch (InterruptedException e) {
						Npvrd.error(groupIp + ": Uncaught InterruptedException.");
					}
				}
			}
			
			this.scheduleUpdater.interrupt();
			
			Npvrd.log(groupIp + ": Shutting down");
			
			this.sock.leaveGroup(group);
			this.sock.close();
		} catch (IOException e) {
			Npvrd.error(groupIp + ": Input/Output error: " + e.getMessage());
		}
	}

}
