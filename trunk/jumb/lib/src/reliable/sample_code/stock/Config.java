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
 * A basic extension of the java.awt.Dialog class
 */
package com.sun.multicast.reliable.applications.stock;

import java.awt.*;
import javax.swing.*;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.*;
import com.sun.multicast.reliable.applications.slinger.ImagePanel;
import com.sun.multicast.reliable.applications.slinger.MessageBox;

/**
 * Undocumented Class Declaration.
 * 
 * 
 * @see
 *
 * @author
 */
public class Config extends Dialog {

    /**
     * Undocumented Class Constructor.
     * 
     * 
     * @param parent
     * @param modal
     * @param properties
     *
     * @see
     */

    public Config(StockViewer parent, boolean modal) {

        super(parent, modal);

        fParent = parent;

        // This code is automatically generated by Visual Cafe when you add
        // components to the visual environment. It instantiates and initializes
        // the components. To modify the code, only use code syntax that matches
        // what Visual Cafe can generate, or Visual Cafe may be unable to back
        // parse your Java file into its visual environment.
        // {{INIT_CONTROLS

	Font theFont = new Font("Monospaced", Font.PLAIN, 14);
	setFont(theFont);
        setLayout(null);
        setVisible(false);
        setSize(451, 440);
        setForeground(new Color(0));
        setBackground(new Color(16777215));

        label1 = new java.awt.Label(
	    "Enter ticker symbols separated by plus signs:");
        label1.setBounds(40, 10, 400, 29);
        add(label1);

	tickerBox = new TextArea(
	    fParent.myTickers, 50, 4, java.awt.TextArea.SCROLLBARS_NONE);
	tickerBox.setBounds(40, 40, 400, 60);
	tickerBox.setEditable(true);
	add(tickerBox);

        int i;
	int startIndex, endIndex = 0;
        char cbuf[] = new char[4000];
	String availableTickers = new String("Available ticker list not found");

        try {
	    // Show them which tickers are available
	    FileReader inputFile = new FileReader(
		"/net/bcn.east/files2/projects/jrms/test/StockSender.sh");
    
	    if ((i = inputFile.read(cbuf, 0, 4000)) != -1) {
		availableTickers = new String(cbuf, 0, i);
		if ((startIndex = availableTickers.indexOf("-STickers")) != 
		    -1) {

		    availableTickers = 
			availableTickers.substring(startIndex + 10);
		    if ((endIndex = availableTickers.indexOf(" ")) != -1)
			availableTickers = 
			    availableTickers.substring(0, endIndex);
		}
		availableTickers = availableTickers.replace('+', ' ');

		while (availableTickers.indexOf("\\") != -1) {
		    availableTickers = availableTickers.substring(0, 
			  availableTickers.indexOf("\\")) + 
			availableTickers.substring(
			    availableTickers.indexOf("\\") + 1);
		}

		availableTickers = "Available symbols: " + availableTickers;
	    }
	}
        catch (FileNotFoundException ie) {System.err.println(ie); }
	catch (IOException ie) {ie.printStackTrace(); }

	availableTickerBox = new TextArea(availableTickers, 50, 5, 
	    java.awt.TextArea.SCROLLBARS_NONE);
	availableTickerBox.setBounds(40, 110, 400, 120);
	availableTickerBox.setEditable(false);
	add(availableTickerBox);

        label2 = new java.awt.Label("Select News Channels:");
        label2.setBounds(40, 230, 200, 29);
        add(label2);

        SunWebSubscribe = new java.awt.Checkbox("SunWeb News");
        SunWebSubscribe.setBounds(40, 260, 160, 29);
	SunWebSubscribe.setState(fParent.showSUNWNews);
        add(SunWebSubscribe);

        PressSubscribe = new java.awt.Checkbox("Press Releases");
        PressSubscribe.setBounds(230, 260, 160, 29);
	PressSubscribe.setState(fParent.showPressNews);
        add(PressSubscribe);

        label3 = new java.awt.Label(
	    "For more symbols, e-mail Ticker@proteus.east");
        label3.setBounds(40, 340, 400, 29);
        add(label3);

        OKButton = new java.awt.Button();
        OKButton.setLabel("OK");
        OKButton.setBounds(143, 386, 61, 21);
        OKButton.setBackground(new Color(16777215));
        add(OKButton);

        CancelButton = new java.awt.Button();
        CancelButton.setLabel("Cancel");
        CancelButton.setBounds(239, 386, 68, 22);
        CancelButton.setBackground(new Color(16777215));
        add(CancelButton);

        setTitle("Sun Labs Ticker - Configuration");


        // {{REGISTER_LISTENERS

        SymWindow aSymWindow = new SymWindow();

        this.addWindowListener(aSymWindow);

        SymMouse aSymMouse = new SymMouse();

        OKButton.addMouseListener(aSymMouse);
        CancelButton.addMouseListener(aSymMouse);
    }

    java.awt.Label label1;
    java.awt.TextArea tickerBox;
    java.awt.TextArea availableTickerBox;
    java.awt.Label label2;
    java.awt.Label label3;
    java.awt.Checkbox SunWebSubscribe;
    java.awt.Checkbox PressSubscribe;
    java.awt.Button OKButton;
    java.awt.Button CancelButton;

    /**
     * Undocumented Method Declaration.
     * 
     * 
     * @see
     */
    public void addNotify() {

        // Record the size of the window prior to calling parents addNotify.

        Dimension d = getSize();

        super.addNotify();

        if (fComponentsAdjusted) {
            return;
        } 

        try {

            // try jdk1.2 method getInsets().
            // Adjust components according to the insets

            setSize(getInsets().left + getInsets().right + d.width, 
                    getInsets().top + getInsets().bottom + d.height);

            Component components[] = getComponents();

            for (int i = 0; i < components.length; i++) {
                Point p = components[i].getLocation();

                p.translate(getInsets().left, getInsets().top);
                components[i].setLocation(p);
            }
        } catch (NoSuchMethodError e) {

            // try jdk1.1 method insets().
            // Adjust components according to the insets

            setSize(insets().left + insets().right + d.width, 
                    insets().top + insets().bottom + d.height);

            Component components[] = getComponents();

            for (int i = 0; i < components.length; i++) {
                Point p = components[i].getLocation();

                p.translate(insets().left, insets().top);
                components[i].setLocation(p);
            }
        }

        fComponentsAdjusted = true;
    }

    // Used for addNotify check.

    boolean fComponentsAdjusted = false;
    java.awt.FileDialog fDialog;
    StockViewer fParent;

    /**
     * Undocumented Method Declaration.
     * 
     * 
     * @see
     */
    public synchronized void show() {
        try {

            // try jdk1.2 method getBounds().

            setLocation(10, 10);

        } catch (NoSuchMethodError e) {

            // try jdk1.1 method bounds().

            move(10, 10);
        }

        super.show();
    }


    /**
     * Undocumented Class Declaration.
     * 
     * 
     * @see
     *
     * @author
     */
    class SymWindow extends java.awt.event.WindowAdapter {

        /**
         * Undocumented Method Declaration.
         * 
         * 
         * @param event
         *
         * @see
         */
        public void windowClosing(java.awt.event.WindowEvent event) {
            Object object = event.getSource();

            if (object == Config.this) {
                Dialog1_WindowClosing(event);
            } 
        }

    }

    /**
     * Undocumented Method Declaration.
     * 
     * 
     * @param event
     *
     * @see
     */
    void Dialog1_WindowClosing(java.awt.event.WindowEvent event) {
        try {

            // try jdk 1.2 method setVisible().

            setVisible(false);
        } catch (NoSuchMethodError e) {

            // try jdk 1.1 method hide().

            hide();
        }
    }

    /**
     * Undocumented Class Declaration.
     * 
     * 
     * @see
     *
     * @author
     */
    class SymMouse extends java.awt.event.MouseAdapter {

        /**
         * Undocumented Method Declaration.
         * 
         * 
         * @param event
         *
         * @see
         */
        public void mouseClicked(java.awt.event.MouseEvent event) {
            Object object = event.getSource();

            if (object == OKButton) {
                OKButton_MouseClick(event);
            } else if (object == CancelButton) {
                CancelButton_MouseClick(event);
            } 
        }

    }

    /**
     * Undocumented Method Declaration.
     * 
     * 
     * @param event
     *
     * @see
     */
    void OKButton_MouseClick(java.awt.event.MouseEvent event) {

	fParent.myTickers = tickerBox.getText();
	fParent.properties.put("stockviewer.tickers", fParent.myTickers);

	if (fParent.showSUNWNews = SunWebSubscribe.getState())
	    fParent.properties.put("stockviewer.SUNWNews", "true");
	else
	    fParent.properties.put("stockviewer.SUNWNews", "false");

	if (fParent.showPressNews = PressSubscribe.getState())
	    fParent.properties.put("stockviewer.PressNews", "true");
	else
	    fParent.properties.put("stockviewer.PressNews", "false");

	fParent.saveProperties(fParent.properties);
	fParent.initTickers();
	fParent.initNews();
        setVisible(false);
    }

    /**
     * Undocumented Method Declaration.
     * 
     * 
     * @param event
     *
     * @see
     */
    void CancelButton_MouseClick(java.awt.event.MouseEvent event) {
        setVisible(false);
    }

}
