package org.obj.jumb.test.jrms; 

import java.net.*;
import java.io.*;
import java.util.*;
import com.sun.multicast.reliable.transport.*;
import com.sun.multicast.reliable.transport.lrmp.*;

public class ReceiverTestLRMP {

    static int dataPort = 6824;
    static String addr = "224.100.100.224";

    static RMPacketSocket ps;

    private static void setupReceiver() throws IOException, Exception {
       LRMPTransportProfile tp = null;
       InetAddress address = InetAddress.getByName(addr);

       tp = new LRMPTransportProfile(address, dataPort);
       ps = tp.createRMPacketSocket(TransportProfile.RECEIVER);
    }

    private static void startReceiving() throws Exception {
        try {
	    while (true) {
	        DatagramPacket p = ps.receive();
                System.out.println(new String(p.getData(), 0, p.getLength()));
	    }
        }
        catch (SessionDoneException e) {
            System.out.println( "Session complete." );
        }
        ps.close();
    }
    
    public static void main(String args[]) {
	try {
	    setupReceiver();
	    startReceiving();
	} 
	catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }
}    