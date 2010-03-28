package pl.lodz.p.cm.ctp.npvrd;

public class QueableData implements Queable {
	
	private byte[] data;
	private int length;
	private int offset;

	public QueableData(byte[] data, int offset, int length) {
		this.data = data.clone();
		this.offset = offset;
		this.length = length;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public int getLength() {
		return length;
	}
	
	public int getOffset() {
		return offset;
	}
	
}