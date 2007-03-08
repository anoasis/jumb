package org.obj.jumb;

import org.apache.log4j.Logger;
import org.obj.jumb.message.JumbMessage;

public class LeaveHandler extends ReceiveHandler {
	private static Logger log = Logger.getLogger(LeaveHandler.class);
	
	public LeaveHandler(Channel channel) {
		super(channel);
	}

	public void handleMessage(JumbMessage msg) {
		channel.getNeighborManager().removeNeighbor(msg);
	}
}
