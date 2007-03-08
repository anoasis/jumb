package org.obj.jumb;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.obj.jumb.message.JumbMessage;

public class NeighborManager {
	private static Logger log = Logger.getLogger(NeighborManager.class);
	
	private List<JumbMessage> neighbors = new ArrayList<JumbMessage>();
	
	public synchronized void addNeighbor(JumbMessage neighbor) {
		if(neighbors.contains(neighbor) == true) {
			return;
		}
		
		neighbors.add(neighbor);
	}
	
	public synchronized void removeNeighbor(JumbMessage neighbor) {
		neighbors.remove(neighbor);
	}
	
	public synchronized boolean contains(JumbMessage neighbor) {
		return neighbors.contains(neighbor);
	}
}
