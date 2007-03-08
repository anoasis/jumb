package org.obj.jumb.test;

import java.net.*;
import java.io.*;
import java.util.*;
import com.sun.multicast.reliable.transport.*;
import com.sun.multicast.reliable.transport.tram.*;

public class SenderTestTRAM {
    static int numSends = 20;

    static String sendString = "Hello World!";
    static InetAddress address = null;
    static int dataPort = 6824;
    static String addr = "224.100.100.224";

    static RMStreamSocket ss;

    private static void setupSender() throws IOException, Exception {
       TRAMTransportProfile tp;
       address = InetAddress.getByName(addr);

		tp = new TRAMTransportProfile(address, dataPort);
       tp.setLateJoinPreference( 
           TRAMTransportProfile.LATE_JOIN_WITH_NO_RECOVERY );

       tp.setMaxDataRate(100000);

       ss = tp.createRMStreamSocket(TransportProfile.SENDER);
    }


    private static void startSending() throws Exception {
        int timesSent = 1;
        byte stringBytes[];

	while ( timesSent <= numSends ) {
	    RMStatistics stat = (RMStatistics) ss.getRMStatistics();
            OutputStream os = ss.getOutputStream();

	    if ( stat.getReceiverCount() > 0 ) {

                stringBytes = ( timesSent + " - " + sendString ).getBytes();
		System.out.println("Sending.");
                os.write( stringBytes );
                os.flush();
                timesSent++;
	    } else {
                System.out.println( "Waiting for a receiver..." + new Date() );
            }
	    Thread.sleep(1000);
	}

        System.out.println( "Halting transmission..." );
        ss.close();
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

