package org.obj.jumb;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ChannelManager extends Thread {
	private static Logger log = Logger.getLogger(ChannelManager.class);
	
	private String confFilePath = null;
	private Map<String, Channel> channels = new HashMap<String, Channel>();

	public ChannelManager(String confFilePath) {
		this.confFilePath = confFilePath;
		init();
	}
	
	private void init() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(confFilePath);
		String chNames[] = ctx.getBeanDefinitionNames();

		for(String chName: chNames) {
			Channel ch = (Channel) ctx.getBean(chName);
			ch.setName(chName);
			channels.put(chName, ch);
		}
		
		Runtime.getRuntime().addShutdownHook(this);
	}

	public String getConfFilePath() {
		return confFilePath;
	}
	
	public Channel getChannel(String name) {
		return channels.get(name);
	}
	
	public void run() {
		Iterator<Channel> chs = channels.values().iterator();
		
		while(chs.hasNext()) {
			try {
				chs.next().leave();
			} catch (Exception e) {
				log.error(e);
			}
		}
	}

}
