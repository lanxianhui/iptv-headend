package pl.lodz.p.cm.ctp.epgd;

public class TimeCorrection {

	private long timeOffset = 0;
	
	public TimeCorrection(String timeOffsetString) {
		char sign = timeOffsetString.charAt(0);
		int hours = Integer.parseInt(timeOffsetString.substring(1, 2));
		int minutes = Integer.parseInt(timeOffsetString.substring(3,4));
		switch (sign) {
			case '+':
				this.timeOffset = ((hours * 60) + (minutes)) * 60;
				break;
			case '-':
				this.timeOffset = ((hours * 60) + (minutes)) * -60;
				break;
		}
	}
	
	public TimeCorrection() {
		
	}
	
	public TimeCorrection(long timeOffset) {
		this.timeOffset = timeOffset;
	}
	
	public long getOffset() {
		return this.timeOffset;
	}
	
	public void setOffset(long timeOffset) {
		this.timeOffset = timeOffset;
	}
	
}
