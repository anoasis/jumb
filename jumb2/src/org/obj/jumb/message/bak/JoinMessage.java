package org.obj.jumb.message.bak;


public class JoinMessage extends MgmtStringMessage {
	public JoinMessage() {
		super(JOIN);
	}
	
	public JoinMessage(String srcName) {
		super(JOIN, srcName);
	}
}
