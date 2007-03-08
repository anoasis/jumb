package org.obj.jumb;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.obj.jumb.message.JumbDataMessage;
import org.obj.jumb.message.JumbMessage;

public class SendMessageBuffer {
	private static Logger log = Logger.getLogger(SendMessageBuffer.class);

	private List<JumbMessage> mgmtQue = new ArrayList<JumbMessage>();
	private List<JumbDataMessage> msgQue = new ArrayList<JumbDataMessage>();
	
	private static final int mgmtQuelimit = 3;
	private static final int msgQuelimit  = 12;
	
	public synchronized void add(JumbMessage msg) {
		if (msg instanceof JumbDataMessage) {
			int overSize = msgQue.size() - msgQuelimit;
			
			if(overSize > 0) {
				for(int i=-1; i<overSize; i++)
					msgQue.remove(0);
			}
log.info("  1");			
			msgQue.add((JumbDataMessage) msg);
			return;
		}
		
		int overSize = mgmtQue.size() - mgmtQuelimit;
		
		if(overSize > 0) {
			for(int i=-1; i<overSize; i++)
				mgmtQue.remove(0);
		}
log.info("  2");			
		mgmtQue.add(msg);
	}
	
	public synchronized int size() {
		return mgmtQue.size() + msgQue.size();
	}
	
	public synchronized JumbMessage get() {
		if(mgmtQue.size() > 0) return mgmtQue.remove(0);
		if(msgQue.size() > 0)  return msgQue.remove(0);
		return null;
	}
	
	public synchronized void clear() {
		mgmtQue.clear();
		msgQue.clear();
	}
}
