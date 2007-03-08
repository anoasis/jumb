package org.obj.jumb.test;

public class Test2 {
	class SenderThread extends Thread {
		public void run() {
			SenderTestTRAM.main(new String[]{});
		}
	}
	
	public static void main(String[] args) {
System.out.println("00000000");
		new Test2().new SenderThread().start();
System.out.println("11111111111");
		ReceiverTestTRAM.main(null);
System.out.println("222222222");
		
		for(;;) {
			try {
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
