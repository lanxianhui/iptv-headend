package pl.lodz.p.cm.ctp.npvrd;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.*;
import pl.lodz.p.cm.ctp.dao.model.TvChannel;

public class ChannelListener implements Runnable {
	public enum RunMode { RUN, STOP };
	
	private TvChannel tvChannel;
	private Thread scheduleUpdater;
	private Lock sinksLock = new ReentrantLock();
	private LinkedList<Sink> sinks;
	private RunMode runMode;
	
	private MulticastSocket sock;
	private InetAddress group;
	
	/**
	 * Creates a new channel recorder listening on a given multicast IP address and port.
	 * @param groupIp Multicast group IP to listen to.
	 * @param groupPort UDP Port to listen on.
	 */
	public ChannelListener(TvChannel tvChannel) {
		this.tvChannel = tvChannel;
		//this.recordingSchedule = new LinkedList<RecordingTask>();
		this.sinks = new LinkedList<Sink>();
		this.runMode = RunMode.RUN;
		
		// Synchronous socket setup to solve crosstalk issues
		Npvrd.log(tvChannel.getIpAdress() + ": Setting up socket.");
		try {
			this.group = InetAddress.getByName(tvChannel.getIpAdress());
			this.sock = new MulticastSocket(tvChannel.getPort());
			this.sock.setLoopbackMode(true);
			this.sock.setSoTimeout(10000);
			this.sock.joinGroup(group);
		} catch (UnknownHostException e) {
			Npvrd.error(tvChannel.getIpAdress() + ": Group IP address invalid: " + e.getMessage());
		} catch (IOException e) {
			Npvrd.error(tvChannel.getIpAdress() + ": Error while trying to set-up socket: " + e.getMessage());
		}
	}
	
	public Lock getSinksLock() {
		return sinksLock;
	}
	
	public List<Sink> getSinks() {
		return sinks;
	}
	
	public TvChannel getTvChannel() {
		return this.tvChannel;
	}
	
	public RunMode getRunMode() {
		return this.runMode;
	}
	
	public void setRunMode(RunMode newMode) {
		this.runMode = newMode;
	}

	@Override
	public void run() {
		// We initialize by creating a local ScheduleUpdater
		ScheduleUpdater scheduleUpdaterObject = new ScheduleUpdater(this, Thread.currentThread());
		this.scheduleUpdater = new Thread(scheduleUpdaterObject);
		
		try {
			byte[] pb = new byte[sock.getReceiveBufferSize()];
			DatagramPacket dp = new DatagramPacket(pb, pb.length);
			long curTime = 0;
			
			// Grab data from network while runMode is on
			while (this.runMode.equals(RunMode.RUN)) {
				try {
					this.sock.receive(dp);
					curTime = System.currentTimeMillis();
					
					// We lock the sinks list, so that it's not modified while we iterate through it
					this.sinksLock.lock();
					// We iterate through all sinks and write to each the data in the packet
					for  (Iterator<Sink> itr = sinks.iterator(); itr.hasNext();) {
					  // Go to next sink
				      Sink curSink = itr.next();
				      curSink.write(dp.getData(), dp.getOffset(), dp.getLength(), curTime);
				    }
					// We unlock the sinks list
					this.sinksLock.unlock();
				} catch (IOException ioe) {
					Npvrd.error("Error reading from network: " + ioe.getMessage());
					
					// We lock the sinks list, so that it's not modified while we iterate through it
					this.sinksLock.lock();
					// We iterate through all sinks and write to each the data in the packet
					for  (Iterator<Sink> itr = sinks.iterator(); itr.hasNext();) {
					  // Go to next sink
				      Sink curSink = itr.next();
				      curSink.setError();
				    }
					// We unlock the sinks list
					this.sinksLock.unlock();
				}
			}
		} catch (SocketException e1) {
			Npvrd.error("Error creating packet data buffer: " + e1.getMessage());
		}
		
		// runMode has to be STOP, we shut down operations
		scheduleUpdater.interrupt();
		try {
			this.sock.leaveGroup(group);
		} catch (IOException e) {
			Npvrd.error("Error leaving group: " + e.getMessage());
		}
		this.sock.close();
	}

}
