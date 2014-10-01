package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.Serializable;

public class Message implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String message;
	private String seqNum;
	private String avdNum;
	
	public Message(String msg, String seq, String avd) {
		message = msg;
		seqNum = seq;
		avdNum = avd;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getSequenceNumber() {
		return seqNum;
	}

	public String getAvdNumber() {
		return avdNum;
	}
	
	public void setSequenceNumber(String seq) {
		seqNum = seq;
	}	
}
