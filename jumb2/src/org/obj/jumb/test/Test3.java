package org.obj.jumb.test;

import org.obj.jumb.Channel;
import org.obj.jumb.message.JumbDataMessage;
import org.obj.jumb.message.JumbMessage;

public class Test3 {
	Connector conn = null;
	
	class SenderThread extends Thread {
		Connector conn;
		
		public SenderThread(Connector conn) {
			this.conn = conn;
		}
		
		public void run() {
			PersonBean pb = new PersonBean("aaa", 1);
			for(;;) {
				try {
					Channel ch = new Channel();
					ch.setHost("AAAAAAAAA");
					ch.setMessageSize(10);
					ch.setName("DDDDDDDDdd");
					ch.setPort(1);
					JumbMessage jm = new JumbDataMessage(ch, pb);
					conn.sendObj(jm);
					Thread.sleep(1000);
				} catch(Exception e) {
					e.printStackTrace();
				}		
			}
		}
	}
	
	void doTest() {
		try {
			conn = new Connector();
			new SenderThread(conn).start();
			
			for(;;) {
				try {
					//System.out.println(conn.receiveObj());
					System.out.println("AAAAAA" + ((JumbDataMessage)conn.receiveObj()).msg);
					Thread.sleep(1000);
				} catch(Exception e) {
					e.printStackTrace();
					break;
				}		
			}
		} catch(Exception e) {
			e.printStackTrace();
		}		
	}
	
	public static void main(String[] args) {
		new Test3().doTest();
		
		for(;;) {
			try {
				Thread.sleep(1500);
			} catch(Exception e) {
				e.printStackTrace();
			}		
		}
	}
}
