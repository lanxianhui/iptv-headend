package pl.lodz.p.cm.ctp.epgd;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
// import pl.lodz.p.cm.ctp.dao.model.*;

public class XmlTvParser {
	
	public static void main(String[] args) throws Exception{
		String uri;

	    if(args.length == 0) {
	      throw new Exception("Filename necessary");
	    } else {
	      uri = args[0];
	    }
	    
	    XMLReader parser = XMLReaderFactory.createXMLReader();
	    XmlTvHandler handler = new XmlTvHandler();
	    
	    parser.setContentHandler(handler);	
	    parser.setErrorHandler(handler);
	    
	    parser.parse(uri);	
	    
	    System.out.println("Parsing ends");
	}
	
}
