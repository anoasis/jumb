package org.obj.jumb.design.channel;

import java.util.HashMap;
import java.util.Map;

public class ChannelManager {
	private Map<String, Channel> chMap = new HashMap<String, Channel>();
	private static ChannelManager instance = null;
	
	private ChannelManager() {
		init();
	}
	
	public void init() {
		loadFromConfig();
		// loop (xml에 정의된 Channel 정보 만큼 ..) 
		// xml에 정의된 정보에 의해 Channel을 만든다.
		// chMap에 등록한다.
	}
	
	public void destroy() {
		// TODO
	}
	
	public static ChannelManager getInstance() {
		if(instance == null) {
			instance = new ChannelManager();
		}
		
		return instance;
	}
	
	private void loadFromConfig() {
		// TODO
	}
	
	public Channel getChannel(String chName) {
		return chMap.get(chName);
	}
}
