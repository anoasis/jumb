package org.obj.jumb.message.bak;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.obj.jumb.Channel;
import org.obj.jumb.message.JumbMessage;

public class JumbMgmtMessage extends JumbMessage {
	
//	public JumbMgmtMessage(int type) {
//		super(type);
//		setLocalSrcHost();
//	}
	
	public JumbMgmtMessage(int type, Channel channel) {
		super(type, channel);
	}
	
	@Override
	public boolean equals(Object obj) {
		JumbMgmtMessage m = (JumbMgmtMessage) obj;
		return (super.equals(obj));
	}
}
