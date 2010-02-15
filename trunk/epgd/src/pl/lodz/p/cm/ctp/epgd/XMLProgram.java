package pl.lodz.p.cm.ctp.epgd;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class XMLProgram implements Serializable {
	
	private static final long serialVersionUID = 6463470089861691064L;
	
	private String start;
	private String stop;
	private String channelId;
	private String title;
	private String subTitle;
	private String description;
	private String category;
	
	public String getStart() {
		return start;
	}
	public Date getStartDate() {
		return dateFromXmlString(start);
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getStop() {
		return stop;
	}
	public Date getStopDate() {
		return dateFromXmlString(stop);
	}
	public void setStop(String stop) {
		this.stop = stop;
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubTitle() {
		return subTitle;
	}
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	private Date dateFromXmlString(String xmlDate) {
		Calendar calendar = Calendar.getInstance();
		
		String year = xmlDate.substring(0, 4);
		String month = xmlDate.substring(4, 6);
		String date = xmlDate.substring(6, 8);
		String hourOfDay = xmlDate.substring(8, 10);
		String minute = xmlDate.substring(10, 12);
		String second = xmlDate.substring(12, 14);
		String timeZone = "GMT" + xmlDate.substring(15, 20);
		
		calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
		calendar.set(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(date), Integer.parseInt(hourOfDay), Integer.parseInt(minute), Integer.parseInt(second));
		return calendar.getTime();
	}
	
}
