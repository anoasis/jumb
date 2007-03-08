package org.obj.jumb.message;

import java.io.Serializable;

import org.obj.jumb.Channel;
import org.obj.jumb.message.JumbMessage;
import org.obj.jumb.message.bak.JumbMgmtMessage;

public class MessageProcessor {
	public static JumbMessage createJoinMessage(Channel channel) {
		return new JumbMessage(JumbMessage.JOIN, channel);
	}

	public static JumbMessage createLeaveMessage(Channel channel) {
		return new JumbMessage(JumbMessage.LEAVE, channel);
	}
	
	public static JumbMessage createDataMessage(Channel channel, Serializable data) {
		return new JumbDataMessage(channel, data);
	}
}
