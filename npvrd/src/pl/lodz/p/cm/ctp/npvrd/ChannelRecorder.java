package pl.lodz.p.cm.ctp.npvrd;

import java.io.*;
import java.net.*;
import java.util.*;
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
		return DAOUtil.hashMD5(plaintext) + ".m2t";
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		try {
			Npvrd.log(groupIp + ": Setting up socket.");
			InetAddress group = InetAddress.getByName(groupIp);
			MulticastSocket sock = new MulticastSocket(groupPort);
			sock.setLoopbackMode(true);
			sock.setSoTimeout(10000);
			sock.joinGroup(group);
			
			ScheduleUpdater updater = new ScheduleUpdater(this, Thread.currentThread());
			this.scheduleUpdater = new Thread(updater);
			this.scheduleUpdater.start();
			
			while (this.getRunMode().equals(RunMode.RUN)) {
				RecordingTask task = null;
				while ((this.getRecheckSchedule()) && (!this.isEmpty())) {
					this.setRecheckSchedule(false);
					try {
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
						
						long beginRecordingNum = task.getRecordingBegin().getTime();
						long endRecordingNum = task.getRecordingEnd().getTime();
						
						if (endRecordingNum > System.currentTimeMillis()) {
							Mode fileMode = Mode.PROCESSING;
							task.setState(fileMode);
							task.saveToDatabase();
							String path = Npvrd.config.recordings;
							String fileName = generateFileName(task);
							
							Npvrd.log(groupIp + ": New task: " + task.getProgramName() + " at " + task.getRecordingBegin().toGMTString());
							
							fileMode = Mode.UNAVAILABLE;
							
							try {
								byte[] buf = new byte[sock.getReceiveBufferSize()];
								DatagramPacket recv = new DatagramPacket(buf, buf.length);
								
								if (System.currentTimeMillis() < beginRecordingNum) {
									Npvrd.log(groupIp + ": Waiting for: " + task.getRecordingBegin().toGMTString());
									Thread.sleep(beginRecordingNum - System.currentTimeMillis() - 10);
								}
								
								OutputStream fos = new BufferedOutputStream(new FileOutputStream(path + fileName));
								
								while (System.currentTimeMillis() < beginRecordingNum) {
									try {
										sock.receive(recv);
										if (!runMode.equals(RunMode.RUN)) {
											fos.close();
											throw new InterruptedException();
										}
									} catch (IOException e) {
										// Do not report that source group is unavailable, if just waiting for program to start
									}
								}
								
								Npvrd.log(groupIp + ": Recording starts for " + task.getProgramName());
								
								try {
									while (System.currentTimeMillis() < endRecordingNum) {
										sock.receive(recv);
										fos.write(recv.getData(), 0, recv.getLength());
										if (!runMode.equals(RunMode.RUN)) {
											fos.close();
											fileMode = Mode.UNAVAILABLE;
											task.setState(fileMode);
											task.setResultFileName(null);
											task.saveToDatabase();
											throw new InterruptedException();
										}
									}
									fileMode = Mode.AVAILABLE; // Recording ended successfully
								} catch (IOException e) {
									Npvrd.error(groupIp + ": Source channel is off-air: " + e.getMessage() + " Nothing to record.");
								}
								
								Npvrd.log(groupIp + ": Recording ends.");			
								
								fos.close();
								
								// Make the file available
								VlmManager vlm = new VlmManager(Npvrd.config.vlm);
								vlm.createNewVod(fileName, path + fileName);
							} catch (IOException ioe) {
								Npvrd.error(groupIp + ": Destination file is unavailable: " + ioe.getMessage());
							}

							// Announce that the file is available
							task.setState(fileMode);
							task.setResultFileName(fileName);
							task.saveToDatabase();
						}
					} catch (InterruptedException ie) {
						
					}
				}
				
				if (this.getRunMode().equals(RunMode.RUN) && this.isEmpty() && !this.getRecheckSchedule()) {
					try {
						Thread.sleep(100000);
					} catch (InterruptedException e) {
						
					}
				}
			}
			
			this.scheduleUpdater.interrupt();
			
			Npvrd.log(groupIp + ": Shutting down");
			
			sock.leaveGroup(group);
			sock.close();
		} catch (UnknownHostException e) {
			Npvrd.error(groupIp + ": Group IP address invalid");
		} catch (IOException e) {
			Npvrd.error(groupIp + ": Listening port busy");
		}
	}

}
