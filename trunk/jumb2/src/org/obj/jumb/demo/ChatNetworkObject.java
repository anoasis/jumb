package org.obj.jumb.demo;

import java.io.Serializable;

public class ChatNetworkObject implements Serializable
{
	private int type;
	private Object data = null;

	public static final int CHAT_MESSAGE = 0;

	public ChatNetworkObject
			(int initType, Object initData) { 
		data = initData;
		type = initType;
	}

	public Object getData() { return data; }
	public int getType() 	{ return type; }
}
