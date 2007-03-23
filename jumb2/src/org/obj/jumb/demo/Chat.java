package org.obj.jumb.demo;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.obj.jumb.Channel;
import org.obj.jumb.ChannelManager;
import org.obj.jumb.ReceiveHandler;
import org.obj.jumb.demo.chat.ChatClient;
import org.obj.jumb.demo.chat.ChatNetworkObject;
import org.obj.jumb.message.JumbDataMessage;
import org.obj.jumb.message.JumbMessage;

public class Chat extends ReceiveHandler 
						implements ActionListener {
  private Channel channel=null;
  private JFrame mainFrame=null;
  private JTextArea textArea = new JTextArea();
  private JScrollPane jsp = new JScrollPane(textArea);
  private JTextField textField = new JTextField();
  boolean no_channel=false;
  private final static String configpath = "org\\obj\\jumb\\demo\\chat.xml"; 

  public Chat(Channel channel) throws Exception {
	  this.channel=channel;
	  channel.setHandler(this);
  }

  public static void main(String[] args) {
	  Chat chat=null;
	  try {
          ChannelManager cm = new ChannelManager(configpath);
          chat=new Chat(cm.getChannel("Chat"));
		  chat.go();
	  }
	  catch(Throwable e) {
		  e.printStackTrace();
		  System.exit(0);
	  }
  }
	
  public void go() throws Exception{
	if(!no_channel) {
		channel.join();
	}

	mainFrame = new JFrame("ChatDemo");
	mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	mainFrame.setSize(600,400);
    Container cp = mainFrame.getContentPane();
    cp.add(jsp, "Center");
    cp.add(textField, "South");
    textField.addActionListener(this);
    mainFrame.setVisible(true);
  }

  public void actionPerformed(ActionEvent ae) {
    String message = textField.getText();
this.addText(message);    
    textField.setText("");
    ChatNetworkObject outgoing = new ChatNetworkObject(ChatNetworkObject.CHAT_MESSAGE, message);
    try {
		channel.send(outgoing);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
	
	public void addText(String text) {
		textArea.append(text + "\n");
	}
	

	@Override
	public void handleMessage(JumbMessage msg) {
		JumbDataMessage data = (JumbDataMessage) msg;
        ChatNetworkObject incoming = (ChatNetworkObject)data.msg;
        String text = (String)incoming.getData();
        this.addText(text);

//		while (data != null) {
//	        try {
//	          ChatNetworkObject incoming = (ChatNetworkObject)data.msg;
//	          String text = (String)incoming.getData();
//	          this.addText(text);
//	        }
//	        catch (Exception e) { e.printStackTrace(); }
//	      }		
	}
}
