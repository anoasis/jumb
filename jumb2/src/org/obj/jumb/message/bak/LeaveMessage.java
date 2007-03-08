package org.obj.jumb.message.bak;

public class LeaveMessage extends MgmtStringMessage {
	public LeaveMessage() {
		super(LEAVE);
	}
	
	public LeaveMessage(String srcName) {
		super(LEAVE, srcName);
	}

}