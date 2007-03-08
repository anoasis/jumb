package org.obj.jumb.demo;

import org.apache.log4j.Logger;
import org.obj.jumb.Channel;
import org.obj.jumb.ReceiveHandler;
import org.obj.jumb.message.JumbDataMessage;
import org.obj.jumb.message.JumbMessage;

public class MyReceiveHandler extends ReceiveHandler {
	private static Logger log = Logger.getLogger(MyReceiveHandler.class);

	public MyReceiveHandler() {
	}

	public void handleMessage(JumbMessage msg) {
		JumbDataMessage data = (JumbDataMessage) msg;
		log.info((PersonBean) data.msg);
	}

}
