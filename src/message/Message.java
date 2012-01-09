package message;

import java.util.*;

public interface Message {

	public String getSender();
	public String getMessage();
	public Date getTime();
	public void append(Message m);
	public void print();
	
}
