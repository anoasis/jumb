

package org.obj.jumb.demo;


import org.apache.log4j.Logger;
import org.obj.jumb.*;
import org.obj.jumb.message.*;

import javax.management.MBeanServer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;




/**
 * Shared whiteboard, each new instance joins the same group. Each instance chooses a random color,
 * mouse moves are broadcast to all group members, which then apply them to their canvas<p>
 * @author Bela Ban, Oct 17 2001
 */
public class Draw extends ReceiveHandler implements ActionListener {
	private static Logger log = Logger.getLogger(Draw.class);
	private final static String configpath = "org\\obj\\jumb\\demo\\DrawDemo.xml";
    private Channel                channel=null;
    private int                    member_size=1;
    final boolean                  first=true;
    final boolean                  cummulative=true;
    private JFrame                 mainFrame=null;
    private JPanel                 sub_panel=null;
    private DrawPanel              panel=null;
    private JButton                clear_button, leave_button;
    private final Random           random=new Random(System.currentTimeMillis());
    private final Font             default_font=new Font("Helvetica",Font.PLAIN,12);
    private final Color            draw_color=selectColor();
    private final Color background_color=Color.white;
    boolean                        no_channel=false;
    boolean                        jmx;


    public Draw(String props, boolean debug, boolean cummulative, boolean no_channel, boolean jmx) throws Exception {
        this.no_channel=no_channel;
        this.jmx=jmx;
        if(no_channel)
            return;

        channel=new Channel();
        channel.setHandler(this);
    }

    public Draw(Channel channel) throws Exception {
        this.channel=channel;
        channel.setHandler(this);
    }

   public static void main(String[] args) {
       Draw             draw=null;
       String           props=null;
       boolean          debug=false;
       boolean          cummulative=false;
       boolean          no_channel=false;
       boolean          jmx=false;

        try {
        	ChannelManager cm = new ChannelManager(configpath);
        	draw=new Draw(cm.getChannel("Draw"));
            draw.go();
        }
        catch(Throwable e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private Color selectColor() {
        int red=(Math.abs(random.nextInt()) % 255);
        int green=(Math.abs(random.nextInt()) % 255);
        int blue=(Math.abs(random.nextInt()) % 255);
        return new Color(red, green, blue);
    }



    public void go() throws Exception {
        if(!no_channel) {
            channel.join();
        }
        mainFrame=new JFrame();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel=new DrawPanel();
        panel.setBackground(background_color);
        sub_panel=new JPanel();
        mainFrame.getContentPane().add("Center", panel);
        clear_button=new JButton("Clear");
        clear_button.setFont(default_font);
        clear_button.addActionListener(this);
        leave_button=new JButton("Leave");
        leave_button.setFont(default_font);
        leave_button.addActionListener(this);
        sub_panel.add("South", clear_button);
        sub_panel.add("South", leave_button);
        mainFrame.getContentPane().add("South", sub_panel);
        mainFrame.setBackground(background_color);
        clear_button.setForeground(Color.blue);
        leave_button.setForeground(Color.blue);
        setTitle();
        mainFrame.pack();
        mainFrame.setLocation(15, 25);
        mainFrame.setBounds(new Rectangle(250, 250));
        mainFrame.setVisible(true);
    }

    void setTitle(String title) {
        String tmp="";
        if(no_channel) {
            mainFrame.setTitle(" Draw Demo ");
            return;
        }
        if(title != null) {
            mainFrame.setTitle(title+" (" + member_size + ")");
        }
    }

    void setTitle() {
        setTitle(null);
    }



    public void receive(JumbDataMessage msg) {
        if(msg == null) {
            System.err.println("received null buffer from " + msg.srcName + ", Host: " + msg.mcastHost);
            return;
        }

        try {
        	DrawCommand comm= new DrawCommand();
   //         DrawCommand comm=(DrawCommand)Util.streamableFromByteBuffer(DrawCommand.class, buf, msg.getOffset(), msg.getLength());
            switch(comm.mode) {
                case DrawCommand.DRAW:
                    if(panel != null)
                        panel.drawPoint(comm);
                    break;
                case DrawCommand.CLEAR:
                    clearPanel();
                    break;
                default:
                    System.err.println("***** received invalid draw command " + comm.mode);
                    break;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void block() {
        System.out.println("--  received BlockEvent");
    }
 
    public void unblock() {
        System.out.println("-- received UnblockEvent");
    }



    /* --------------- Callbacks --------------- */


    public void clearPanel() {
        if(panel != null)
            panel.clear();
    }

    public void sendClearPanelMsg() {
        int                  tmp[]=new int[1]; tmp[0]=0;
        DrawCommand          comm=new DrawCommand(DrawCommand.CLEAR);

        try {
            channel.send(comm);
        }
        catch(Exception ex) {
            System.err.println(ex);
        }
    }


    public void actionPerformed(ActionEvent e) {
        String     command=e.getActionCommand();
        if("Clear".equals(command)) {
            if(no_channel) {
                clearPanel();
                return;
            }
            sendClearPanelMsg();
        }
        else if("Leave".equals(command)) {
            stop();
        }
        else
            System.out.println("Unknown action");
    }


    public void stop() {
        if(!no_channel) {
            try {
                channel.leave();
            }
            catch(Exception ex) {
                System.err.println(ex);
            }
        }
        mainFrame.setVisible(false);
        mainFrame.dispose();
    }


    private class DrawPanel extends JPanel implements MouseMotionListener {
        final Dimension        preferred_size=new Dimension(235, 170);
        Image            img=null; // for drawing pixels
        Dimension        d, imgsize=null;
        Graphics         gr=null;


        public DrawPanel() {
            createOffscreenImage();
            addMouseMotionListener(this);
            addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    if(getWidth() <= 0 || getHeight() <= 0) return;
                    createOffscreenImage();
                }
            });
        }



        final void createOffscreenImage() {
            d=getSize();
            if(img == null || imgsize == null || imgsize.width != d.width || imgsize.height != d.height) {
                img=createImage(d.width, d.height);
                if(img != null)
                    gr=img.getGraphics();
                imgsize=d;
            }
        }


        /* ---------------------- MouseMotionListener interface------------------------- */

        public void mouseMoved(MouseEvent e) {}

        public void mouseDragged(MouseEvent e) {
            int                 x=e.getX(), y=e.getY();
            DrawCommand         comm=new DrawCommand(DrawCommand.DRAW, x, y,
                                                     draw_color.getRed(), draw_color.getGreen(), draw_color.getBlue());

            if(no_channel) {
                drawPoint(comm);
                return;
            }

            try {
                //byte[] buf=Util.streamableToByteBuffer(comm);
                channel.send(comm);
                Thread.yield(); // gives the repainter some breath
            }
            catch(Exception ex) {
                System.err.println(ex);
            }
        }

        /* ------------------- End of MouseMotionListener interface --------------------- */


        /**
         * Adds pixel to queue and calls repaint() whenever we have MAX_ITEMS pixels in the queue
         * or when MAX_TIME msecs have elapsed (whichever comes first). The advantage compared to just calling
         * repaint() after adding a pixel to the queue is that repaint() can most often draw multiple points
         * at the same time.
         */
        public void drawPoint(DrawCommand c) {
            if(c == null || gr == null) return;
            gr.setColor(new Color(c.r, c.g, c.b));
            gr.fillOval(c.x, c.y, 10, 10);
            repaint();
        }


        public void clear() {
            if(gr == null) return;
            gr.clearRect(0, 0, getSize().width, getSize().height);
            repaint();
        }


        public Dimension getPreferredSize() {
            return preferred_size;
        }


        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if(img != null) {
                g.drawImage(img, 0, 0, null);
            }
        }

    }

	@Override
	public void handleMessage(JumbMessage msg) {
		// TODO Auto-generated method stub
		
	}

}

