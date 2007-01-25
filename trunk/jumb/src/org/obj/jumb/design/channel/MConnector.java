package org.obj.jumb.design.channel;

import java.io.IOException;
import java.net.DatagramSocket;

import org.obj.jumb.design.msg.Message;

public class MConnector {
	private MAddress addr = null;
	private DatagramSocket sock = null;
	
	public MConnector(MAddress address) {
		this.addr = address;
	}
	
	public MAddress getAddress() {
		return addr;
	}
	
	public void send(Message msg) throws IOException {
		// TODO
	}
	
	public Message receive() throws IOException {
		// TODO
		return null;
	}
}
