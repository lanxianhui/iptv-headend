package pl.lodz.p.cm.ctp.npvrd;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Date;
//import java.util.Queue;

public class MulticastTimedListener implements Runnable {
	
	//private Queue<Queable> streamQueue;
	private OutputStream output;
	private MulticastSocket socket;
	volatile private long beginRecording;
	volatile private long endRecording;
	
	public enum Result { OK, ERROR, ABORTED, UNDEFINED };
	
	private Result result;
	
	public MulticastTimedListener(Date beginRecording, Date endRecording, OutputStream output, MulticastSocket socket) {
		//this.streamQueue = streamQueue;
		this.output = output;
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
			int bufLen = socket.getReceiveBufferSize();
			byte[] buf = new byte[bufLen];
			DatagramPacket recv = new DatagramPacket(buf, buf.length);
			
			Npvrd.log("Waiting....");
			if (System.currentTimeMillis() < beginRecording) {
				try {
					Thread.sleep(beginRecording - System.currentTimeMillis() - 10);
				} catch (InterruptedException e) {
					//streamQueue.offer(new QueablePoison());
					output.close();
					this.result = Result.ABORTED;
					return;
				}
			}
			
			while (System.currentTimeMillis() < beginRecording) {
				try {
					socket.receive(recv);
				} catch (IOException e) {
					// Do not report that source group is unavailable, if just waiting for program to start
				}
			}
			
			Npvrd.log("Recording....");
			
			while (System.currentTimeMillis() < endRecording) {
				try {
					socket.receive(recv);
					//byte[] dataBuf = new byte[bufLen];
					//int dataBufLen = recv.getLength();
					//System.arraycopy(recv.getData(), 0, dataBuf, 0, dataBufLen);
					//streamQueue.offer(new QueableData(dataBuf, 0, dataBufLen));
					output.write(recv.getData(), recv.getOffset(), recv.getLength());
				} catch (IOException e) {
					Npvrd.error("Trouble receiving - poisoning destination");
					//streamQueue.offer(new QueablePoison());
					output.close();
					this.result = Result.ERROR;
					return;
				}
			}
			
			//streamQueue.offer(new QueablePoison());
			output.close();
			this.result = Result.OK;
		} catch (SocketException se) {
			Npvrd.error("Unable to get receive buffer size");
			//streamQueue.offer(new QueablePoison());
			try {
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.result = Result.ERROR;
		} catch (IOException ioe) {
			Npvrd.error("Unable to close output");
		}
	}

	public Result getResult() {
		return result;
	}
}
