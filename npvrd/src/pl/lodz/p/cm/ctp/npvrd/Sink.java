package pl.lodz.p.cm.ctp.npvrd;

import java.io.IOException;

/**
 * Sink is an interface for anything that can receive a Transport-Stream for writing, processing
 * or something similar.
 * @author Jan Starzak
 *
 */
public interface Sink {
	
	/**
	 * Check if the sink is still active
	 * @return
	 */
	boolean isActive();
	
	/**
	 * Set the error flag
	 */
	void setError();
	
	/**
	 * Check if the sink has been object of an error
	 * @return true if an error happened to the sink
	 */
	boolean isError();
	
	/**
	 * Write data to the sink.
	 * @param data Byte array containing the data to write
	 * @param offset Offset at which the data begins
	 * @param length Length of the data
	 * @param time Current time in milliseconds (begin at Unix Epoch).
	 * @return 
	 */
	boolean write(byte[] data, int offset, int length, long time);
	
	/**
	 * Close the sink and the underlaying stream (if any).
	 * @throws IOException
	 */
	void close();
	
}
