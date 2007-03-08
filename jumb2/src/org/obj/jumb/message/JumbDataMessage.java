package org.obj.jumb.message;

import java.io.Serializable;

import org.obj.jumb.Channel;
import org.obj.jumb.message.bak.JumbMgmtMessage;

public class JumbDataMessage extends JumbMessage {
	public Serializable msg = null;

	public JumbDataMessage(Channel channel, Serializable data) {
		super(DATA, channel);
		msg = data;
	}
}
