package org.obj.jumb;

import org.apache.log4j.Logger;
import org.obj.jumb.message.JumbMessage;
import org.obj.jumb.message.bak.JumbMgmtMessage;

public class JoinHandler extends ReceiveHandler {
	private static Logger log = Logger.getLogger(JoinHandler.class);
	
	public JoinHandler(Channel channel) {
		super(channel);
	}

	public void handleMessage(JumbMessage msg) {
log.info(1);
		channel.getNeighborManager().addNeighbor(msg);
log.info(2);
	}

}
