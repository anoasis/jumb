package org.obj.jumb.test.jrms;

import java.net.*;
import java.io.*;
import java.util.*;
import com.sun.multicast.reliable.transport.*;
import com.sun.multicast.reliable.transport.tram.*;

public class ReceiverTestTRAM {
    static String addr = "224.100.100.224";
    static int dataPort = 6824;

    static RMStreamSocket ss;

    private static void setupChannel() throws IOException, Exception {
       TRAMTransportProfile tp = null;
       InetAddress address = InetAddress.getByName(addr);
       tp = new TRAMTransportProfile(address, dataPort);
       tp.setLateJoinPreference( 
           TRAMTransportProfile.LATE_JOIN_WITH_NO_RECOVERY );
       tp.setMaxDataRate(100000);

       ss = tp.createRMStreamSocket(TransportProfile.RECEIVER);
    }

    private static void startReceiving() throws Exception {
        int len;
        int buffer_size = 1024;
        byte buffer[] = new byte[buffer_size];
        InputStream is = ss.getInputStream();

        do {
            len = is.read( buffer );
            if ( len > 0 ) {
                System.out.println( new String(buffer, 0, len));
            }
	} while ( len != -1 );
        System.out.println( "Session complete." );
        ss.close();
    }

    public static void main(String args[]) {
	try {
	    setupChannel();
	    startReceiving();
	} 
	catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }
}
