package pl.lodz.p.cm.ctp.npvrd;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Date;

public class MulticastTimedListener implements Runnable {
	
	private OutputStream output;
	private MulticastSocket socket;
	private InetAddress group;
	volatile private long beginRecording;
	volatile private long endRecording;
	
	public enum Result { OK, ERROR, ABORTED, UNDEFINED };
	
	private Result result;
	
	public MulticastTimedListener(Date beginRecording, Date endRecording, OutputStream output, InetAddress group, MulticastSocket socket) {
		this.output = output;
		this.socket = socket;
		this.group = group;
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
					output.write(recv.getData(), recv.getOffset(), recv.getLength());
				} catch (IOException e) {
					Npvrd.error("Finishing recording");
					output.close();
					this.result = Result.ERROR;
					return;
				}
			}
			
			output.close();
			this.result = Result.OK;
		} catch (SocketException se) {
			Npvrd.error("Unable to get receive buffer size");
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
