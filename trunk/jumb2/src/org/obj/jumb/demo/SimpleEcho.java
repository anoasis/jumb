package org.obj.jumb.demo;

import org.apache.log4j.Logger;
import org.obj.jumb.Channel;
import org.obj.jumb.ChannelManager;

public class SimpleEcho {
	private static Logger log = Logger.getLogger(SimpleEcho.class);
	private final static String xmlpath = "org\\obj\\jumb\\demo\\jumb.xml";
	public static void main(String[] args) {
		ChannelManager cm = new ChannelManager(xmlpath);
		try {
			Channel ch = cm.getChannel("test2");
			ch.join();

			PersonBean data = new PersonBean("vicdev", 0);
			for(int i=0; i<30; i++) {
				data.setAge(i);
				ch.send(data);
				
				Thread.sleep(2000);
			}
			
			ch.leave();
		} catch (Exception e) {
			log.error(e);
		}
		log.info("END");
	}
}
