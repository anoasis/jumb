package org.obj.jumb;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.obj.jumb.message.JumbMessage;

import com.sun.multicast.reliable.transport.RMStatistics;
import com.sun.multicast.reliable.transport.RMStreamSocket;
import com.sun.multicast.reliable.transport.TransportProfile;
import com.sun.multicast.reliable.transport.tram.TRAMTransportProfile;

public class MConnector {
	private static Logger log = Logger.getLogger(MConnector.class);
	
	private TRAMTransportProfile tp = null;
    private RMStreamSocket rcvSock = null;
    private RMStreamSocket sndSock = null;
    private MessageListener listener = null;
    private boolean isClose = false;
    private List<JumbMessage> msgQue = new ArrayList<JumbMessage>();
    private Channel channel = null;
    private SendMessageBuffer sendDeferredQue = new SendMessageBuffer();
	
	public MConnector(Channel owner) throws Exception {
		this.channel = owner;
		init();
	}
	
	public void finit() {
		isClose = false;
		sendDeferredQue.clear();
		sndSock.close();
		rcvSock.close();
	}
	
	private void init() throws Exception {
        InetAddress address = InetAddress.getByName(channel.getHost());

        TRAMTransportProfile rtp = new TRAMTransportProfile(address, channel.getPort());
        rtp.setLateJoinPreference(TRAMTransportProfile.LATE_JOIN_WITH_NO_RECOVERY );
        rtp.setMaxDataRate(channel.getMessageSize());

        rcvSock = rtp.createRMStreamSocket(TransportProfile.RECEIVER);
    
        TRAMTransportProfile stp = new TRAMTransportProfile(address, channel.getPort());
		stp.setLateJoinPreference(TRAMTransportProfile.LATE_JOIN_WITH_NO_RECOVERY );
		stp.setMaxDataRate(channel.getMessageSize());
		
		sndSock = stp.createRMStreamSocket(TransportProfile.SENDER);
		
		isClose = true;
		listener = new MessageListener(this);
		listener.start();
	}

	public void send(JumbMessage msg) throws Exception {
    	RMStatistics stat = (RMStatistics) sndSock.getRMStatistics();
    	if(stat.getReceiverCount() > 0) {
	    	ObjectOutputStream out = new ObjectOutputStream(sndSock.getOutputStream());
	    	out.writeObject(msg);
	    	out.flush();
    	}
	}
	
	
	public void send_org(JumbMessage msg) throws Exception {
    	RMStatistics stat = (RMStatistics) sndSock.getRMStatistics();
    	if(stat.getReceiverCount() > 0) {
	    	ObjectOutputStream out = new ObjectOutputStream(sndSock.getOutputStream());
log.info(1);
			
//	    	for(;;) {
//    			JumbMessage deferredMsg = sendDeferredQue.get();
//log.info(2);
//    			
//    			if(deferredMsg == null) {
//    				break;
//    			}
//    			
//log.info(3 + "  " + deferredMsg);
//    	    	out.writeObject(deferredMsg);
//    	    	out.flush();
//    	    	
//    	    	Thread.sleep(1000);
//log.info(4);
//    		}
    		
log.info(5);
	    	out.writeObject(msg);
	    	out.flush();
log.info(6);
	    	
	    	return;
    	}

//    	sendDeferredQue.add(msg);
	}
	
	public JumbMessage receive() throws Exception {
		synchronized (msgQue) {
			if(msgQue.isEmpty()) {
				msgQue.wait();
			}
			
			return msgQue.get(0);
		}
	}
	
	private JumbMessage receiveStream() throws Exception {
		return (JumbMessage) new ObjectInputStream(rcvSock.getInputStream()).readObject();
	}

	public class MessageListener extends Thread {
		private MConnector connector = null;
	    private ReceiveHandler joinHandler = null;
	    private ReceiveHandler leaveHandler = null;
		
		public MessageListener(MConnector owner) {
			this.connector = owner;
			joinHandler = connector.channel.getJoinHandler();
			leaveHandler = connector.channel.getLeaveHandler();
		}
		
		public void run() {
			while(connector.isClose) {
				try {
					processMessage(connector.receiveStream());
					
				} catch (Exception e) {
					log.error(e);
				}
			}
		}
		
		private void processMessage(JumbMessage msg) {
			if(msg.head == JumbMessage.DATA) {
				ReceiveHandler msgHandler = connector.channel.getHandler();
				
				if(msgHandler != null) {
					msgHandler.processMessage(msg);
					return;
				}
					
				synchronized (msgQue) {
					connector.msgQue.add(msg);
					connector.msgQue.notify();
				}
			} else if(msg.head == JumbMessage.JOIN) {
				joinHandler.processMessage(msg);
			} else if(msg.head == JumbMessage.LEAVE) {
				leaveHandler.processMessage(msg);
			} else {
				log.error("unexpected message : " + msg);
			}			
		}
	}
}
