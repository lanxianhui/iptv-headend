package pl.lodz.p.cm.ctp.npvrd;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Queue;

public class StreamQueueWriter implements Runnable {
	
	private Queue<Queable> streamQueue;
	private OutputStream output;

	public StreamQueueWriter(Queue<Queable> streamQueue, OutputStream output) {
		this.streamQueue = streamQueue;
		this.output = output;
	}

	@Override
	public void run() {
		while (true) {
			Queable queable = streamQueue.poll();
			if (queable != null) {
				if (queable instanceof QueablePoison) {
					try {
						output.close();
					} catch (IOException e) {
						Npvrd.error("Trouble closing file stream");
						e.printStackTrace();
					}
					Npvrd.log("Stream writer finished stream");
					return;
				} else if (queable instanceof QueableData) {
					QueableData queableData = (QueableData)queable;
					try {
						output.write(queableData.getData(), queableData.getOffset(), queableData.getLength());
					} catch (IOException e) {
						Npvrd.error("Trouble writing data to file");
						e.printStackTrace();
					}
				}
			}
		}
	}
	
}
