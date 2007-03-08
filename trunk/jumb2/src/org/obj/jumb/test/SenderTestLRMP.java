package org.obj.jumb.test;

import java.net.*;
import java.io.*;
import java.util.*;
import com.sun.multicast.reliable.transport.*;
import com.sun.multicast.reliable.transport.lrmp.*;

public class SenderTestLRMP {
    static int numSends = 20;
    static String sendString = "Hello World!";
    static InetAddress address = null;
    static int dataPort = 6824;
    static String addr = "224.100.100.224";

    static RMPacketSocket ps;

    private static void setupSender() throws IOException, Exception {

       LRMPTransportProfile tp;
       address = InetAddress.getByName(addr);

       tp = new LRMPTransportProfile(address, dataPort);
       tp.setMaxDataRate(100000);

       ps = tp.createRMPacketSocket(TransportProfile.SENDER);
    }

    private static void startSending() throws Exception {
        int timesSent = 1;

	while ( timesSent <= numSends ) {

            byte stringBytes[] = ( timesSent + " - " + sendString ).getBytes();
            System.out.println("Sending.");
            ps.send(new DatagramPacket(
                stringBytes, stringBytes.length, address, dataPort));
            timesSent++;
	    Thread.sleep(1000);
	}

        System.out.println( "Halting transmission..." );
        ps.close();
    }

    public static void main( String args[] ) {
	if (args.length > 0 ) {
	    sendString = args[0];
	}
        if (args.length > 1 ) {
            numSends = Integer.parseInt( args[1] );
        }

	try {
	    setupSender();
	    startSending();
	} 
	catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }
}
