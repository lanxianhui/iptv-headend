package pl.lodz.p.cm.ctp.epgd;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.TimeZone;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import pl.lodz.p.cm.ctp.dao.model.*;

public class XmlTvHandler extends DefaultHandler {
	
	Program currentProgram = null;
	String characterBuffer = "";
	
	Hashtable<String, Long> channelMap;
	Hashtable<Long, String> timeZoneMap;

	public XmlTvHandler() {
		// TODO Auto-generated constructor stub
		channelMap = new Hashtable<String, Long>();
		timeZoneMap = new Hashtable<Long, String>();
	}

	public XmlTvHandler(Hashtable<String, Long> channelMap, Hashtable<Long, String> timeZoneMap) {
		this.channelMap = channelMap;
		this.timeZoneMap = timeZoneMap;
	}
	
	@SuppressWarnings("unused")
	private Date dateFromXmlString(String xmlDate) {
		return dateFromXmlString(xmlDate, null);
	}
	
	private Date dateFromXmlString(String xmlDate, String altTimeZone) {
		Calendar calendar = GregorianCalendar.getInstance();
		
		String year = xmlDate.substring(0, 4);
		String month = xmlDate.substring(4, 6);
		String date = xmlDate.substring(6, 8);
		String hourOfDay = xmlDate.substring(8, 10);
		String minute = xmlDate.substring(10, 12);
		String second = xmlDate.substring(12, 14);
		String timeZone = xmlDate.substring(15);
		
		if (altTimeZone != null)
			timeZone = altTimeZone;
		
		if ((timeZone.charAt(0) == '-') | (timeZone.charAt(0) == '+')) {
			timeZone = "GMT" + timeZone;
		}
		
		calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
		calendar.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(date), Integer.parseInt(hourOfDay), Integer.parseInt(minute), Integer.parseInt(second));
		return calendar.getTime();
	}
	
	public void startDocument() {
		//System.out.println("startDocument triggered");
		//System.out.println();
	}
	
	public void endDocument() {
		//System.out.println("endDocument triggered");
		//System.out.println();
	}
	
	public void startElement(String namespaceURL, String localName, String qName, Attributes atts) {
		//System.out.println("startElement triggered: " + qName + ", " + localName);
		
		if (localName.equals("programme")) {
			String channelIdString = atts.getValue("channel");
			long channelId = 0;
			try {
				channelId = channelMap.get(channelIdString);
			} catch (NullPointerException npe) {
				channelId = Integer.parseInt(channelIdString);
			}
			String beginString = atts.getValue("start");
			String endString = atts.getValue("stop");
			
			String altTimeZone = null;
			try {
				altTimeZone = timeZoneMap.get(currentProgram.getTvChannelId());
			} catch (NullPointerException npe) {
				
			}
			
			currentProgram = new Program(null, channelId, "",
					new Timestamp(dateFromXmlString(beginString, altTimeZone).getTime()),
					new Timestamp(dateFromXmlString(endString, altTimeZone).getTime()));
		}
	}

	public void endElement(String namespaceURL, String localName, String qName) {
		//System.out.println("endElement triggered: " + qName + ", "  + localName);
		
		if (localName.equals("programme")) {
			System.out.println(currentProgram.toString());
		} else if (localName.equals("title")) {
			currentProgram.setTitle(characterBuffer.trim());
			characterBuffer = "";
		} else if (localName.equals("desc")) {
			currentProgram.setDescription(characterBuffer.trim());
			characterBuffer = "";
		}
	}
	
	public void characters(char ch[], int start, int length) {
		//System.out.print("Arbitrary characters: ");
		int finishLength = start + length;
		for (int i=start; i<finishLength; i++) {
			characterBuffer = characterBuffer + ch[i];
		}
	}

}
