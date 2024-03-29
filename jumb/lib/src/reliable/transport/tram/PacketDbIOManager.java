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
 * PacketDbIOManager.java
 */
package com.sun.multicast.reliable.transport.tram;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * This is an IO manager for the TRAM packet database. The TRAM packet
 * database is where the
 * a. Packets generated by the application(in case of a sender) reside
 * till the output dispatcher actually sends them.
 * b. Packets received from the net are stored for the application to
 * read.
 * c. Packets received from the net are stored till the
 * acknowledgements are received from the members (head node)
 * 
 * The PacketDbIOManager seperate databases for
 * 1. The incoming packets from the net that are to be forwarded to
 * the application(i.e net is the source of packets and the
 * application and other TRAM internal modules sink/consume the
 * packet.
 * 2. For the packets that are sourced by the application(in case of
 * the sender) and the is consumed by the TRAM dispatcher module
 * that forwards the message to the network layer.
 * 
 * The PacketDbIOManager implements the standard TRAMInputOutput interface.
 * The objects using these interfaces access the inputDatabase with all
 * of the get and remove methods and the output database for the put
 * method.
 * 
 * The outputDispatcher is the only object that removes packets from the
 * outputDatabase and this object is the only one that adds packets to
 * the inputDatabase.
 */
class PacketDbIOManager implements TRAMDataPacketListener, TRAMInputOutput {
    TRAMControlBlock tramblk;
    PacketDb outputDatabase;
    PacketDb inputDatabase;
    OutputDispThread outputDispatcher;
    TRAMLogger logger = null;
    boolean sender;

    /**
     * Create a new PacketDBIOManager. The input and output databases
     * are created, the TRAMDataPacketListener is initialized, and the
     * output dispatcher is created.
     * 
     * @param tramblk the TRAMControlBlock.
     */
    public PacketDbIOManager(TRAMControlBlock tramblk) {
        this.tramblk = tramblk;
        outputDatabase = new PacketDb(tramblk, 15);
	outputDatabase.setInbound(false);
        inputDatabase = new PacketDb(tramblk);
	inputDatabase.setInbound(true);
        logger = tramblk.getLogger();
        outputDispatcher = tramblk.getOutputDispThread();

        outputDispatcher.setPacketDB(outputDatabase);

        sender = ((tramblk.getTransportProfile().getTmode() & 0xff) 
                  == TMODE.SEND_ONLY);

        if (!sender) {
            tramblk.getTRAMDataCache().addTRAMDataPacketListener(this);
        } 
    }

    /**
     * This is the implementation of the TRAMDataPacketListener. the
     * InputDispThread calls this method when a new TRAMDataPacket
     * is received. The packet is then placed on the inputDatabase.
     * 
     * @param pk the TRAMDataPacket received.
     */
    public void receiveDataPacket(TRAMDataPacketEvent e) {
        TRAMDataPacket pk = e.getPacket();

        inputDatabase.putPacket((TRAMPacket) pk);
    }

    /**
     * Give the specified packet to the object.
     * 
     * @param pkt the TRAMPacket
     * 
     * @exception IOException if the TRAMPacket cannot be accepted
     */
    public synchronized void putPacket(TRAMPacket pkt) throws IOException {
        while (tramblk.isCacheFull()) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }

	if (pkt.getMessageType() == MESGTYPE.MCAST_DATA &&
            pkt.getSubType() == SUBMESGTYPE.DATA) {

	    setFlowControlFlags(pkt);	 // set the prune flag if need be
	}
	
        outputDatabase.putPacket(pkt);
    }

    /**
     * Give the specified packet to the object.
     * This routine is useful in implementations which uses a queue where
     * in a message with high priority gets put at the head of the queue.
     * 
     * @param pkt the TRAMPacket
     * @param boolean packet priority -- true -> high priority,
     * false -> normal
     * 
     * @exception IOException if the TRAMPacket cannot be accepted
     */
    public void putPacket(TRAMPacket pkt, 
                          boolean priority) throws IOException {

	if (logger.requiresLogging(TRAMLogger.LOG_VERBOSE)) {
            logger.putPacketln(this, 
		"PacketDbIOManager: Putpacket priority packet");
	}

	if (pkt.getMessageType() == MESGTYPE.MCAST_DATA &&
            pkt.getSubType() == SUBMESGTYPE.DATA) {

	    setFlowControlFlags(pkt);	 // set the prune flag if need be

	    if (logger.requiresLogging(TRAMLogger.LOG_VERBOSE)) {
                logger.putPacketln(this, 
		    "PacketDbIOManager: Packet Number is " + 
		    ((TRAMDataPacket) pkt).getSequenceNumber());
	    }
        }

        outputDatabase.putPacket(pkt, priority);
    }

    /**
     * Get the first TRAMPacket from the object. The packet is
     * removed from the object. This method blocks if the
     * database is empty.
     * 
     * @return The TRAMPacket that is returned.
     * 
     */
    public TRAMPacket getPacket() {
        TRAMPacket pk = inputDatabase.getPacket();

        return pk;
    }

    /**
     * Get the first TRAMPacket from the object. The packet is
     * removed from the object. If the database is empty, the
     * method throws NoSuchElementException.
     * 
     * @Exception NoSuchElementException when the input queue is
     * empty.
     * 
     * @return The TRAMPacket that is returned.
     * 
     */
    public TRAMPacket getPacketWithNoBlocking() throws NoSuchElementException {
        throw new NoSuchElementException("Unsupported Method");
    }

    /**
     * Get the TRAMDataPacket from the object with the sequence number
     * specified. The packet is removed from the object.
     * 
     * @return The TRAMDataPacket that is returned.
     * 
     * @exception NoSuchElementException is thrown if the packet doesn't
     * exist.
     */
    public TRAMDataPacket getPacket(long sequenceNumber) 
            throws NoSuchElementException {
        return inputDatabase.getPacket(sequenceNumber);
    }

    /**
     * Remove the packet with the specified sequence number.
     * 
     * @param sequenceNumber the sequence number of the packet to be
     * removed.
     * @exception NoSuchElementException if the packet does not exist.
     */
    public void removePacket(long sequenceNumber) 
            throws NoSuchElementException {
        inputDatabase.removePacket(sequenceNumber);
    }

    public void printDataCounts() {
	if (logger.requiresLogging(TRAMLogger.LOG_VERBOSE |
	    TRAMLogger.LOG_DATACACHE)) {

            logger.putPacketln(this, 
		"Input Database  = " + 
		inputDatabase.getPacketCount());

            logger.putPacketln(this, 
		"Output Database = " + 
		outputDatabase.getPacketCount());
	}
    }

    public synchronized void wake() {
        notifyAll();
    }

    /*
     * Determine if there is a member that's slowing us down to the
     * minimum data rate.  If there is such a member, check if it's
     * one of our direct members.  Also set the prune flag bit and the
     * "worst" flow control value for the group in this data packet 
     * which is the next data packet to go out.  
     */
    private void setFlowControlFlags(TRAMPacket tramPacket) {
	TRAMRateAdjuster rateAdjuster = tramblk.getRateAdjuster();

        byte flags = (byte)(tramPacket.getFlags() & 0xff);

        if (rateAdjuster.timeToPrune()) {
	    int groupFlowControlInfo = rateAdjuster.getGroupFlowControlInfo();

            tramPacket.setFlags((byte)(flags | TRAMDataPacket.FLAGBIT_PRUNE));
	    ((TRAMDataPacket)tramPacket).setFlowControlInfo(
		groupFlowControlInfo);

	    if (logger.requiresLogging(TRAMLogger.LOG_CONG)) {
                logger.putPacketln(this, "Setting FLAGBIT_PRUNE. " +
		    "flow " + groupFlowControlInfo);
	    }

	    /*
	     * try to find a suitable member for the sender to disown.
	     */
	    rateAdjuster.findMemberToPrune(groupFlowControlInfo);
	}
    }

    /**
     * gets the number of packets in the retransmit queue
     */
    public int getRetxDbSize() {
	return outputDatabase.getRetxDbSize();
    }

}



