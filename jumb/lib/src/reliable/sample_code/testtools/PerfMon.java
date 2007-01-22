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
 * PerfMon.java
 */
package com.sun.multicast.reliable.applications.testtools;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.rmi.*;
import java.rmi.server.*;

/** 
 * Basic class for creating bytes/time graph, although PerfMonCanvas
 * does most of the work. 
 */

public class PerfMon extends Frame {
    private Vector GData = new Vector();
    private PerfMonCanvas pmc;

    public PerfMon(Observable notifier, int x, int y) {
	super("Bytes/Time Performance Monitor");
	addWindowListener(new DWAdapter());
	setBackground(Color.white);
	setLayout(new BorderLayout());
	setResizable(true);
	pmc = new PerfMonCanvas(notifier);
	add("Center", pmc);
	setBounds(x, y, 640, 480);
	setVisible(true);
	show();
    }

    public void setData(GraphData gd) {
	if (gd != null) {
	    pmc.setGData(gd);
	}
    }

    public void customPaint() {
	pmc.customPaint();
    }
 
    public void printGData() {
	pmc.printGData();
    }

    public void resetGData() {
	pmc.resetGData();
    }

    public void resetHostnames() {
	pmc.resetHostnames();
    }
	
    class DWAdapter extends WindowAdapter {
	public void windowClosing(WindowEvent event) {
	    closeWindow();
	}
    }

    public void closeWindow() {
	this.dispose();
    }
    
    public static void main(String[] args) {
	Observable notifier = new PerfObservable();
	PerfMon f = new PerfMon(notifier, 0, 0);
 
    }
}
