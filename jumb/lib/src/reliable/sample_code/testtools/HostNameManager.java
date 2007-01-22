/* 
 * Copyright (c) 2001, Sun Microsystems Laboratories 
 * All rights reserved. 
 * 
 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met: 
 * 
 *     Redistributions of source code must retain the 
 *     above copyright notice, this list of conditions 
 *     and the following disclaimer. 
 *             
 *     Redistributions in binary form must reproduce 
 *     the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation 
 *     and/or other materials provided with the distribution. 
 *             
 *     Neither the name of Sun Microsystems, Inc. nor 
 *     the names of its contributors may be used to endorse 
 *     or promote products derived from this software without 
 *     specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
 * CONTRIBUTORS ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, 
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND 
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, 
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY 
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
 * THE POSSIBILITY OF SUCH DAMAGE. 
 */

/*
 * HostNameManager.java
 */
package com.sun.multicast.reliable.applications.testtools;

import java.util.*;
import java.net.*;
import java.io.*;

/** 
 * Used to create a Hashtable for storing hostnames found int the  
 * hostnames.txt file. Values are unique for each host.  Also used
 * to help define when to reset graph data. 
 */
public class HostNameManager {
    private static HostNameManager hostnamemanager;
    private Vector hosts = new Vector();
    private Hashtable hashhosts = new Hashtable();
    private File hostfile = new File("hostnames.txt");
    private String hostfilename = "";
    private int counter = 0;

    HostNameManager() {
	if (hostfile.exists()) {
	    resetHosts();
	}
    }

    public Vector getHosts() {
	return hosts;
    }

    public int getHostCount() {
	return hosts.size();
    }

    public Hashtable getHashHosts() {
	resetHosts();	
	return hashhosts;
    }

    public boolean isHostFile() {
	return hostfile.exists();
    }

    public void resetHosts() {
	try { 
	    BufferedReader hostnames =
		new BufferedReader(
		    new FileReader(hostfile));
	    String s;
	    hosts.clear();
	    hashhosts.clear();
	    while ((s = hostnames.readLine()) != null) {
		if (!s.startsWith("#")) {
		    hosts.addElement(s);
		    hashhosts.put(s, new Integer(counter++));
		}
	    } 
	    hashhosts.put("reset", new Integer(99)); // 99 reserved for reset
	    hashhosts.put("blank", new Integer(100)); // 100 reserved for empty
	    counter = 0;
	    hostnames.close();
	} catch (IOException e) {
	    System.out.println(e);
	    e.printStackTrace(System.out);
	}
    }

    public static HostNameManager getHostNameManager() {
	if (hostnamemanager == null) {
	    hostnamemanager = new HostNameManager();
	}
	return hostnamemanager;
    }
}
