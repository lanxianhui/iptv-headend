package pl.lodz.p.cm.ctp.nullguide;

import java.io.*;
import java.text.*;
import java.util.*;

import com.generationjava.io.WritingException;
import com.generationjava.io.xml.*;

public class NullGuide {

	/**
	 * @param args
	 */
	static Date baseDate;
	static Date begin;
	static Date end;
	static int intervalMinutes = 60;
	static String dummyProgram = "No Data";
	static String[] channelIds;
	static String outputFile = "nullprogram.xml";
	static String timeZone = "+0000";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-d")) {
				String baseDateString = args[++i];
				SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
				
				try {
					baseDate = f.parse(baseDateString);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (args[i].equals("-b")) {
				String beginString = args[++i];
				SimpleDateFormat f = new SimpleDateFormat("HH:mm");
				
				try {
					begin = f.parse(beginString);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (args[i].equals("-e")) {
				String endString = args[++i];
				SimpleDateFormat f = new SimpleDateFormat("HH:mm");
				
				try {
					end = f.parse(endString);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (args[i].equals("-i")) {
				intervalMinutes = Integer.parseInt(args[++i]);
			} else if (args[i].equals("-t")) {
				dummyProgram = args[++i];
			} else if (args[i].equals("-c")) {
				channelIds = args[++i].split(",");
			} else if (args[i].equals("-o")) {
				outputFile = args[++i];
			} else if (args[i].equals("-z")) {
				timeZone = args[++i];
			}
		}
		
		begin.setTime(baseDate.getTime() + begin.getTime());
		end.setTime(baseDate.getTime() + end.getTime());
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		
		try {
			FileWriter fWriter = new FileWriter(outputFile);
			XmlWriter xmltvWriter = new XmlWriter(fWriter);
			
			xmltvWriter.writeEntity("tv");
			xmltvWriter.writeAttribute("generator-info-name", "CTP NullGuide Generator");
			
			for (String id : channelIds) {
				Calendar c = Calendar.getInstance();
				c.setTime(begin);
				Date currentBegin = new Date(begin.getTime());
				Date currentEnd;
				
				while (currentBegin.before(end)) {
					currentBegin = c.getTime();
					c.add(Calendar.MINUTE, intervalMinutes);
					currentEnd = c.getTime();
					
					xmltvWriter.writeEntity("programme");
					xmltvWriter.writeAttribute("start", sdf.format(currentBegin) + " " + timeZone);
					xmltvWriter.writeAttribute("stop", sdf.format(currentEnd) + " " + timeZone);
					xmltvWriter.writeAttribute("channel", id);
					
						xmltvWriter.writeEntity("title");
						xmltvWriter.writeText(dummyProgram);
						xmltvWriter.endEntity();
					
					xmltvWriter.endEntity();
				}
			}
			
			for (String id : channelIds) {
				// <channel id="457"><display-name lang="pl">Euronews</display-name></channel>
				xmltvWriter.writeEntity("channel");
				xmltvWriter.writeAttribute("id", id);
					xmltvWriter.writeEntity("display-name");
					xmltvWriter.writeAttribute("lang", "eo");
						xmltvWriter.writeText("Unknown name");
					xmltvWriter.endEntity();
				xmltvWriter.endEntity();
			}
			
			xmltvWriter.endEntity();
			
			xmltvWriter.close();
			fWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WritingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*	<tv generator-info-name="ctp nullguide generator">
		 *   <programme start=YYYYMMddHHMMss +ZZzz" stop="" channel="">
		 *   <title></title>
		 *   <sub-title></sub-title>
		 *   <desc></desc>
		 *   <category></category>
		 *   </programme>
		 *   <channel id="1">
		 *   <display-name lang="pl"></display-name>
		 *   </channel>
		 *   </tv>
		 */
	}

}
