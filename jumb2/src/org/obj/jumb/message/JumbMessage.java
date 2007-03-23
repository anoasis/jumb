package org.obj.jumb.message;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.obj.jumb.Channel;

public class JumbMessage implements Serializable {
	private static Logger log = Logger.getLogger(JumbMessage.class);

	public static final int JOIN = 0;
	public static final int LEAVE = 1;
	public static final int DATA = 2;

	public int head;
	
	// body
	public String srcName = null;
	public String mcastHost = null;
	public int port = 0;
	public long channelId = 0;
	public String srcHost = null;

	public JumbMessage(int head, Channel channel) {
		this.head = head;
		this.mcastHost = channel.getHost();
		this.port = channel.getPort();
		this.srcName = channel.getName();
		this.channelId = channel.getChannelId();

		setLocalSrcHost();
	}

	protected void setLocalSrcHost() {
		try {
			srcHost = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			log.error(e);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}

		JumbMessage m = (JumbMessage) obj;
		if(m == this) {
			return true;
		}

		return (m.channelId == channelId && equals(srcHost, m.srcHost));
//				&& m.port == port && equals(srcName, m.srcName) && equals(mcastHost, m.mcastHost));
	}
	
	private static boolean equals(Object obj1, Object obj2) {
		if(obj1 == obj2) return true;
		if(obj1 == null || obj2 == null) return false;
		if(obj1.equals(obj2) == true) return true;
		
		return false;
	}
}
