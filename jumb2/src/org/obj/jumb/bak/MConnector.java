package org.obj.jumb.bak;

import java.net.InetAddress;

import com.sun.multicast.reliable.transport.RMStreamSocket;
import com.sun.multicast.reliable.transport.TransportProfile;
import com.sun.multicast.reliable.transport.tram.TRAMTransportProfile;

public class MConnector {
	private RMStreamSocket sender = null;
	private RMStreamSocket receiver = null;
	
	public MConnector(String host, int port) throws Exception {
		init(host, port);
	}
	
	public void init(String host, int port) throws Exception {
	}
}
