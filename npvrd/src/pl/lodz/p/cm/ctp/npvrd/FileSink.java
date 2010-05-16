package pl.lodz.p.cm.ctp.npvrd;

import java.io.OutputStream;
import java.io.IOException;

public class FileSink implements Sink {
	
	private OutputStream fileStream;
	private long begin;
	private long end;
	private boolean error = false;
	private boolean active = true;
	
	FileSink(OutputStream fileStream, long begin, long end) {
		this.fileStream = fileStream;
		this.begin = begin;
		this.end = end;
	}
	
	public long getBegin() {
		return begin;
	}

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
