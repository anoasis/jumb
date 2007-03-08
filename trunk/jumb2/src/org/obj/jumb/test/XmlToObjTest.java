package org.obj.jumb.test;

import org.apache.log4j.Logger;
import org.obj.jumb.ChannelManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class XmlToObjTest {
	private static Logger logger = Logger.getLogger(XmlToObjTest.class);
	private static final String xml = "org\\obj\\jumb\\test\\jumb.xml";
	
	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(xml);
		System.out.println(ctx.getBeanDefinitionNames()[0]);
		//ChannelManager chm = (ChannelManager)ctx.getBean("chMgr");
		//logger.info(chm.getChannels());
		//System.out.println(chm.getChannels() + " " + chm.getChannels().get("test1").getHost() + " " + chm.getChannels().get("test1").getPort());
	}
	
//	private static void test() {
//		try {
//			BufferedReader reader = new BufferedReader(new FileReader(xml));
//			
//			BeanReader beanReader  = new BeanReader();
//			beanReader.getXMLIntrospector().getConfiguration().setAttributesForPrimitives(false);
//			beanReader.getBindingConfiguration().setMapIDs(false);
//			
//			beanReader.registerBeanClass("person", Test1.class);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public static void main(String[] args) {
////		Channel ch = new Channel();
//		
//        StringReader xmlReader = new StringReader(
//        	"<?xml version='1.0' ?><person><age>25</age><name>James Smith</name></person>");
//
//		BeanReader beanReader  = new BeanReader();
//		beanReader.getXMLIntrospector().getConfiguration().setAttributesForPrimitives(false);
//		beanReader.getBindingConfiguration().setMapIDs(false);
//		
//		try {
//			beanReader.registerBeanClass("person", Test1.class);
//			Test1 person = (Test1) beanReader.parse(xmlReader);
//			System.out.println(person.getAge() + " " + person.getName());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	
//	}
}
