package message;

import java.util.Date;

public class SimpleMessage implements Message{

	private String sender;
	private StringBuffer buffer;
	private Date time;
	
	public SimpleMessage(String sender
						,String message
						,Date time) {
		this.sender = sender;
		this.buffer = new StringBuffer(message);
		this.time = time;
	}
	
	@Override
	public String getSender() {
		return this.sender;
	}

	@Override
	public String getMessage() {
		return this.buffer.toString();
	}
	
	@Override
	public Date getTime() {
		return this.time;
	}

	
	@Override
	public void append(Message m) {
		this.buffer.append("\n");
		this.buffer.append(m.getMessage());
		this.time = m.getTime();
	}
	
	@Override
	public void print() {
		System.out.println(this.time.toString() + " [" + this.sender + "] " + getMessage());
	}

}
