package org.obj.jumb.message.bak;

import org.obj.jumb.Channel;

public class MessageFactory {
	public static JumbMessage createJoinMessage(Channel channel) {
		return new JoinMessage(channel.getName());
	}
	
	public static JumbMessage createJumbMessage(byte[] head, byte[] body) throws Exception {
		String strHead = new String(head);
		byte[] msg = new byte[head.length + body.length];
		int i=0;
		
		for(; i<head.length; i++) {
			msg[i] = head[i];
		}
		for(int j=0; j<body.length; j++, i++) {
			msg[i] = body[j];
		}
		
		if(strHead.startsWith(JumbMessage.JOIN) == true) {
			return new JoinMessage().unmarshal(msg);
		}
		
		return null;
	}

	public static JumbMessage createLeaveMessage(Channel channel) {
		return new LeaveMessage(channel.getName());
	}
}
