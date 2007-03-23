package org.obj.jumb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.obj.jumb.message.JumbMessage;

import com.sun.multicast.reliable.transport.RMPacketSocket;
import com.sun.multicast.reliable.transport.RMStatistics;
import com.sun.multicast.reliable.transport.TransportProfile;
import com.sun.multicast.reliable.transport.lrmp.LRMPTransportProfile;
import com.sun.multicast.reliable.transport.tram.TRAMTransportProfile;

public class MConnector {
	private static Logger log = Logger.getLogger(MConnector.class);
	
	private TRAMTransportProfile tp = null;
//    private RMStreamSocket rcvSock = null;
//    private RMStreamSocket sndSock = null;
	private RMPacketSocket rcvSock = null;
	private RMPacketSocket sndSock = null;
    private MessageListener listener = null;
    private boolean isClose = false;
    private List<JumbMessage> msgQue = new ArrayList<JumbMessage>();
    private Channel channel = null;
    private SendMessageBuffer sendDeferredQue = new SendMessageBuffer();
    private InetAddress mAddress = null;
	
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
        mAddress = InetAddress.getByName(channel.getHost());

//        TRAMTransportProfile stp = new TRAMTransportProfile(address, channel.getPort());
//		stp.setLateJoinPreference(TRAMTransportProfile.LATE_JOIN_WITH_NO_RECOVERY );
//		stp.setMaxDataRate(channel.getMessageSize());
//		
//		sndSock = stp.createRMStreamSocket(TransportProfile.SENDER);
//		
//        TRAMTransportProfile rtp = new TRAMTransportProfile(address, channel.getPort());
//        rtp.setLateJoinPreference(TRAMTransportProfile.LATE_JOIN_WITH_NO_RECOVERY );
//        rtp.setMaxDataRate(channel.getMessageSize());
//
//        rcvSock = rtp.createRMStreamSocket(TransportProfile.RECEIVER);
        
        LRMPTransportProfile rtp = new LRMPTransportProfile(mAddress, channel.getPort());
        rcvSock = rtp.createRMPacketSocket(TransportProfile.RECEIVER);

        LRMPTransportProfile stp = new LRMPTransportProfile(mAddress, channel.getPort());
        stp.setMaxDataRate(channel.getMessageSize());

        sndSock = stp.createRMPacketSocket(TransportProfile.SENDER);
    
		isClose = true;
		listener = new MessageListener(this);
		listener.start();
	}

	public void send(JumbMessage msg) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		
		oos.writeObject(msg);
		byte data[] = baos.toByteArray();
		
		sndSock.send(new DatagramPacket(data, data.length, mAddress, channel.getPort()));
//    	RMStatistics stat = (RMStatistics) sndSock.getRMStatistics();
//
//    	if(stat.getReceiverCount() > 0) {
//    		
//	    	ObjectOutputStream out = new ObjectOutputStream(sndSock.getOutputStream());
//	    	out.writeObject(msg);
//	    	out.flush();
//    	}
	}
	
	
	private JumbMessage receiveStream() throws Exception {
		DatagramPacket dp = rcvSock.receive();
		
		byte data[] = dp.getData();
		Object obj = new ObjectInputStream(new ByteArrayInputStream(data)).readObject();
				
		return (JumbMessage) obj;
		
//		return (JumbMessage) new ObjectInputStream(rcvSock.getInputStream()).readObject();
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
//log.info(channel.getChannelId() + " processMessage " + msg.channelId);			
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





	public void send_org(JumbMessage msg) throws Exception {
//    	RMStatistics stat = (RMStatistics) sndSock.getRMStatistics();
//    	if(stat.getReceiverCount() > 0) {
//	    	ObjectOutputStream out = new ObjectOutputStream(sndSock.getOutputStream());
//log.info(1);
//			
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
    		
//log.info(5);
//	    	out.writeObject(msg);
//	    	out.flush();
//log.info(6);
//	    	
//	    	return;
//    	}
//
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
}


