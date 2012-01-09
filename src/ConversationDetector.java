import java.util.*;

import message.Message;

import parser.AdiumChatLogParser;
import parser.ChatLogParser;

public class ConversationDetector {

	public static void main(String[] args) {
		ChatLogParser lp = new AdiumChatLogParser();
		List<Message> messages = lp.getMessages("data/test.xml");
		for (Message m : messages) {
			m.print();
		}
	}

}
