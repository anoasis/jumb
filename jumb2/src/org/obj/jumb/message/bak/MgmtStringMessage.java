package org.obj.jumb.message.bak;

public class MgmtStringMessage extends JumbMessage {
	public String srcName = null;
	
	public MgmtStringMessage(String type) {
		super(type);
	}
	
	public MgmtStringMessage(String type, String srcName) {
		super(type);
		this.srcName = srcName;
	}
	
	public byte[] marshal() {
		if(msg == null) {
			//byte[] body = buff.toString().getBytes();
			buff.append(getLength(srcName)).append(splitDel);
			buff.append(srcName);
			
			msg = buff.toString().getBytes();
		}
		
		return msg;
	}

	public JumbMessage unmarshal(byte[] msg) throws Exception {
		String strMsg = new String(msg);
		
		srcName = strMsg.split(splitDel)[2];
		this.msg = msg;
		
		return this;
	}
}
