package org.obj.jumb.message.bak;

import java.io.Serializable;

/**
 * Message Format
 * HEADER(5)|Length(10)|BODY
 * 
 */

public abstract class JumbMessage {
	public static final String JOIN = "JOIN-";
	public static final String LEAVE = "LEAVE";
	public static final String DATA = "DATA-";
	protected static final String splitDel = "\\|";
	public static final int headLength = 17;
	
	private static final String postLen[] =  {
		"0000000000",
		"000000000",
		"00000000",
		"0000000",
		"000000",
		"00000",
		"0000",
		"000",
		"00",
		"0"
	};
	
	public String header = null; 
	public StringBuffer buff = new StringBuffer();
	public byte[] msg = null;
	
	protected JumbMessage(String header) {
		this.header = header;
		buff.append(header).append("|");
	}
	
	abstract public byte[] marshal();
	abstract public JumbMessage unmarshal(byte[] msg) throws Exception;
	
	protected static String getLength(String body) {
		String len = body.length()+"";
		
		return postLen[len.length()] + len;
	}
	
	public static void main(String[] args) {
		System.out.println("DATA-|00x0000003|".substring(6, 16));
		String s = "The following code example formats the input string (the literal target), embedding it into a larger string that begins with Pre and ends with Post. The conversion is done in two steps. The first is two create a new instance of the class PrintfFormat. This is done with a constructor that takes a sprintfcontrol string as an input argument. The second step is to use a sprintfmethod of the class PrintfFormat to create a string that can be used for other purposes (printing to the console, for example).";
		System.out.println(getLength("134"));
		System.out.println(getLength("1342"));
		System.out.println(getLength("13434"));
		System.out.println(getLength("13411111"));
		System.out.println(getLength(s));
	}
}

