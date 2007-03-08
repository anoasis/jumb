package org.obj.jumb;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.obj.jumb.message.MessageProcessor;

public class Channel {
	private static Logger log = Logger.getLogger(Channel.class);
	
	private String name = null;
	private String host = null;
	private int port = 0;
	private int messageSize = 0;
	private long channelId = 0;
	private ReceiveHandler handler = null;
	private MConnector conn = null;
	private boolean isJoined = false;
	private NeighborManager neighbors = null;
    private ReceiveHandler joinHandler = null;
    private ReceiveHandler leaveHandler = null;
	
	public Channel() {
		neighbors = new NeighborManager();
		joinHandler = new JoinHandler(this);
		leaveHandler = new LeaveHandler(this);
		channelId = System.nanoTime();
	}

	public synchronized boolean join() throws Exception {
		if(conn == null) {
			conn = new MConnector(this);
		}
		
		if(isJoined == true) {
			return false;
		}
		
		conn.send(MessageProcessor.createJoinMessage(this));
		isJoined = true;
		
		return true;
	}
	
	public synchronized boolean leave() throws Exception {
		if(isJoined == false) {
			return false;
		}
		
		conn.send(MessageProcessor.createLeaveMessage(this));
		conn = null;
		isJoined = false;
		
		return true;
	}
	
	public synchronized void send(Serializable data) throws Exception {
		if(isJoined == false) {
			return;
		}

		conn.send(MessageProcessor.createDataMessage(this, data));
	}
	
	public ReceiveHandler getHandler() {
		return handler;
	}

	public void setHandler(ReceiveHandler handler) {
		this.handler = handler;
		handler.setChannel(this);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMessageSize() {
		return messageSize;
	}

	public void setMessageSize(int messageSize) {
		this.messageSize = messageSize;
	}

	public ReceiveHandler getJoinHandler() {
		return joinHandler;
	}

	public ReceiveHandler getLeaveHandler() {
		return leaveHandler;
	}

	public NeighborManager getNeighborManager() {
		return neighbors;
	}

	public long getChannelId() {
		return channelId;
	}

}
