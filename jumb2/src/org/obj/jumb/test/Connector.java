package org.obj.jumb.test;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;

import com.sun.multicast.reliable.transport.RMStatistics;
import com.sun.multicast.reliable.transport.RMStreamSocket;
import com.sun.multicast.reliable.transport.TransportProfile;
import com.sun.multicast.reliable.transport.tram.TRAMTransportProfile;

public class Connector {
    static String addr = "224.100.100.224";
    static int dataPort = 6824;

    private RMStreamSocket rcvSock = null;
    private RMStreamSocket sndSock = null;
    
    public Connector() throws Exception {
        InetAddress address = InetAddress.getByName(addr);

        TRAMTransportProfile rtp = new TRAMTransportProfile(address, dataPort);
        rtp.setLateJoinPreference(TRAMTransportProfile.LATE_JOIN_WITH_NO_RECOVERY );
        rtp.setMaxDataRate(100000);

        rcvSock = rtp.createRMStreamSocket(TransportProfile.RECEIVER);

        TRAMTransportProfile stp = new TRAMTransportProfile(address, dataPort);
		stp.setLateJoinPreference(TRAMTransportProfile.LATE_JOIN_WITH_NO_RECOVERY );
		stp.setMaxDataRate(100000);
		
		sndSock = stp.createRMStreamSocket(TransportProfile.SENDER);

    
		InetAddress addr = rcvSock.getInterface();
		System.out.println(addr.getHostAddress() + "#########" + rcvSock.getTransportProfile().getName());
		System.out.println(rcvSock.getTransportProfile().getPort() + " $$ " + rcvSock.getTransportProfile().getTTL());

		addr = sndSock.getInterface();
		System.out.println(addr.getHostAddress() + "#########" + sndSock.getTransportProfile().getName());
		System.out.println(sndSock.getTransportProfile().getPort() + " $$ " + sndSock.getTransportProfile().getTTL());
		System.out.println(sndSock.getTransportProfile().isOrdered() + "  " + InetAddress.getLocalHost());
    }
    
    public void sendObj(Serializable data) throws Exception {
    	RMStatistics stat = (RMStatistics) sndSock.getRMStatistics();
    	System.out.println("send 1");    	
    	if(stat.getReceiverCount() > 0) {

	    	ObjectOutputStream oos = new ObjectOutputStream(sndSock.getOutputStream());
	    	System.out.print("send 2, ");    	
	    	oos.writeObject(data);
	    	System.out.print("send 3, ");    	
	    	oos.flush();
	    	System.out.println("send 4");    	
    	}
    }
    
    public Object receiveObj() throws Exception {
    	System.out.println("receive 1	available : " + rcvSock.getInputStream().available());

    	Object obj = new ObjectInputStream(rcvSock.getInputStream()).readObject();

    	System.out.println("receive 3   " + obj);
    	return obj;
    }
    
    public void send(Serializable data) throws Exception {
    	RMStatistics stat = (RMStatistics) sndSock.getRMStatistics();
System.out.println("send 1");    	
    	if(stat.getReceiverCount() > 0) {

//ByteArrayOutputStream baos = new ByteArrayOutputStream();
//ObjectOutputStream oos = new ObjectOutputStream(baos);
//System.out.print("send 2, ");    	
//oos.writeObject(data);
//byte[] buf = baos.toByteArray();
//System.out.print("3, ");    	
//sndSock.getOutputStream().write(buf);
sndSock.getOutputStream().write("AAAAAA".getBytes());
sndSock.getOutputStream().flush();
System.out.println("4");    	
    		
    		
//    		ObjectOutputStream out = new ObjectOutputStream(sndSock.getOutputStream());
//    		out.writeObject(data);
//System.out.println("send 2");    	
    	}
    }
    
    public Object receive() throws Exception {
System.out.println("receive 1	available : " + rcvSock.getInputStream().available());

byte[] buf = new byte[10000];

int len = rcvSock.getInputStream().read(buf);
System.out.println("receive 2");
return new String(buf, 0, len);
//Object obj = new ObjectInputStream(new ByteArrayInputStream(buf)).readObject();

//System.out.println("receive 3   " + obj);
//return obj;
//
//		if(rcvSock.getInputStream().available() > 0) {
//			ObjectInputStream in = new ObjectInputStream(rcvSock.getInputStream());
//			
//			return in.readObject();
//		}
//System.out.println("receive 3");    	
//    	return null;
    }
}
