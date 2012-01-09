package parser;

import message.Message;
import message.SimpleMessage;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.joda.time.DateTime;

import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AdiumChatLogParser implements ChatLogParser {

	public AdiumChatLogParser() {}
	
	@Override
	public List<Message> getMessages(String path) {
		File f = new File(path);
		List<Message> messages  = new ArrayList<Message>();
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
			NodeList nodes = doc.getElementsByTagName("message");
			
			for(int i = 0; i< nodes.getLength(); ++i) {
				Element elt = (Element) nodes.item(i);
				DateTime dt = new DateTime(elt.getAttribute("time"));
				
				Message m = new SimpleMessage(elt.getAttribute("sender")
											,elt.getElementsByTagName("div").item(0).getTextContent()
											,dt.toDate());
				
				if(messages.size() > 0 
					&&  m.getSender().equals(
							messages.get(messages.size()-1).getSender()
						)
					) 
				{
					messages.get(messages.size()-1).append(m);
				}
				else
					messages.add(m);
				
			}
			return messages;
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
