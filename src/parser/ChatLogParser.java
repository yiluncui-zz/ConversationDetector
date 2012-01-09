package parser;

import java.util.List;
import message.Message;


public interface ChatLogParser {

	public List<Message> getMessages(String path);
	
}
