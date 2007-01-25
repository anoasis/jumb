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
		// loop (xml�� ���ǵ� Channel ���� ��ŭ ..) 
		// xml�� ���ǵ� ������ ���� Channel�� �����.
		// chMap�� ����Ѵ�.
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
