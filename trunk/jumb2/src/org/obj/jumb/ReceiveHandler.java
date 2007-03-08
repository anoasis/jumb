package org.obj.jumb;

import org.apache.log4j.Logger;
import org.obj.jumb.message.JumbMessage;

public abstract class ReceiveHandler {
	private static Logger log = Logger.getLogger(ReceiveHandler.class);

	protected Channel channel = null;
	private JumbMessage localEndPoint = null;

	public ReceiveHandler() {
	}
	
	public ReceiveHandler(Channel channel) {
		this.channel = channel;
	}
	
	public void processMessage(JumbMessage msg) {
		if(localEndPoint == null) {
			localEndPoint = new JumbMessage(JumbMessage.JOIN, channel);			
		}

		if(localEndPoint.equals(msg) == false) {
			handleMessage(msg);
		}
//log.error("must have change");		
//		handleMessage(msg);
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	abstract public void handleMessage(JumbMessage msg);
}
