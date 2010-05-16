package pl.lodz.p.cm.ctp.npvrd;

import java.io.OutputStream;
import java.io.IOException;

public class FileSink implements Sink {
	
	private OutputStream fileStream;
	private long begin;
	private long end;
	private boolean error = false;
	private boolean active = true;
	
	/**
	 * Create a new FileSink
	 * @param fileStream The file stream to base it on
	 * @param begin The time (in milliseconds) at which to begin recording
	 * @param end The time (in millisenconds) at which the recording should end
	 */
	FileSink(OutputStream fileStream, long begin, long end) {
		this.fileStream = fileStream;
		this.begin = begin;
		this.end = end;
	}
	
	/**
	 * Get the set recording begin time
	 * @return Begin time (in milliseconds) since Unix Epoch
	 */
	public long getBegin() {
		return begin;
	}

	/**
	 * Get the set recording end time
	 * @return End time (in milliseconds) since Unix Epoch
	 */
	public long getEnd() {
		return end;
	}

	@Override
	public boolean isActive() {
		return this.active;
	}

	@Override
	public void close() {
		try {
			fileStream.close();
		} catch (IOException e) {
			System.err.println("Error closing file: " + e.getMessage());
		}
	}

	@Override
	public boolean write(byte[] data, int offset, int length, long time) {
		if ((time > this.begin) && (time < this.end)) {
			try {
				this.fileStream.write(data, offset, length);
			} catch (IOException e) {
				System.err.println("Error writing to file: " + e.getMessage()); 
			}
		} else if (time > this.end) {
			this.active = false;
		}
		return false;
	}

	@Override
	public boolean isError() {
		return error;
	}

	@Override
	public void setError() {
		this.error = true;	
	}

}
