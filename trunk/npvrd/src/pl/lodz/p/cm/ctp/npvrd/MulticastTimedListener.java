package pl.lodz.p.cm.ctp.npvrd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Date;
import java.util.Queue;

public class MulticastTimedListener implements Runnable {
	
	private Queue<Queable> streamQueue;
	private MulticastSocket socket;
	volatile private long beginRecording;
	volatile private long endRecording;
	
	public enum Result { OK, ERROR, UNDEFINED };
	
	private Result result;
	
	public MulticastTimedListener(Date beginRecording, Date endRecording, Queue<Queable> streamQueue, MulticastSocket socket) {
		this.streamQueue = streamQueue;
		this.socket = socket;
		this.beginRecording = beginRecording.getTime();
		this.endRecording = endRecording.getTime();
		this.result = Result.UNDEFINED;
	}

	public long getBeginRecording() {
		return beginRecording;
	}

	public void setBeginRecording(long beginRecording) {
		this.beginRecording = beginRecording;
	}

	public long getEndRecording() {
		return endRecording;
	}

	public void setEndRecording(long endRecording) {
		this.endRecording = endRecording;
	}

	@Override
	public void run() {
		try {
			byte[] buf = new byte[socket.getReceiveBufferSize()];
			DatagramPacket recv = new DatagramPacket(buf, buf.length);
			
			if (System.currentTimeMillis() < beginRecording) {
				Thread.sleep(beginRecording - System.currentTimeMillis() - 10);
			}
			
			while (System.currentTimeMillis() < beginRecording) {
				try {
					socket.receive(recv);
				} catch (IOException e) {
					// Do not report that source group is unavailable, if just waiting for program to start
				}
			}
			
			while (System.currentTimeMillis() < endRecording) {
				try {
					socket.receive(recv);
					streamQueue.offer(new QueableData(recv.getData(), 0, recv.getLength()));
				} catch (IOException e) {
					throw new InterruptedException();
				}
			}
			
			streamQueue.offer(new QueablePoison());
			this.result = Result.OK;
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			streamQueue.offer(new QueablePoison());
			this.result = Result.ERROR;
		} catch (InterruptedException e) {
			streamQueue.offer(new QueablePoison());
			this.result = Result.ERROR;
		}
	}

	public Result getResult() {
		return result;
	}
}
