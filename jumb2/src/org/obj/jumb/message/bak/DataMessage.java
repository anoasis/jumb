package org.obj.jumb.message.bak;

public class DataMessage extends JumbMessage {
	public Object msg;

	public DataMessage() {
		super(DATA);
	}

	public byte[] marshal() {
		return null;
	}

	public JumbMessage unmarshal(byte[] msg) throws Exception {
		return null;
	}
}
