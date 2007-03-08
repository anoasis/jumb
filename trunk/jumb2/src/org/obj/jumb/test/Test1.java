package org.obj.jumb.test;

import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.UUID;

import org.apache.commons.betwixt.io.BeanReader;

public class Test1 {
	private String name;
    private int age;

    public Test1() {}
	
	public static void main(String[] args) {
		System.out.println("least : " + UUID.randomUUID().getLeastSignificantBits() + " most : " + UUID.randomUUID().getMostSignificantBits());
		System.out.println("least : " + UUID.randomUUID().getLeastSignificantBits() + " most : " + UUID.randomUUID().getMostSignificantBits());
		System.out.println(UUID.randomUUID());
		System.out.println(UUID.randomUUID());
		System.out.println(UUID.randomUUID());
		System.out.println(System.nanoTime());
		System.out.println(System.nanoTime());
		System.out.println(System.nanoTime());
		System.out.println(System.nanoTime());
		if(true) return;
		
		
		PersonBean p = new PersonBean();
		try {
			Field f = p.getClass().getDeclaredField("name");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		System.out.println(Integer.MAX_VALUE);
		System.out.println(Integer.parseInt("000030"));
		System.out.println("1234".length());
		System.out.println("1234".getBytes().length);
		
		System.out.println("DATA-|0000000003|".substring(6, 16));
		
		
		
        StringReader xmlReader = new StringReader(
                    "<?xml version='1.0' ?><person><age>25</age><name>James Smith</name></person>");
        
        BeanReader beanReader  = new BeanReader();
        beanReader.getXMLIntrospector().getConfiguration().setAttributesForPrimitives(false);
        beanReader.getBindingConfiguration().setMapIDs(false);
        
        try {
	        Test1 person = (Test1) beanReader.parse(xmlReader);
	        beanReader.registerBeanClass("person", Test1.class);
	        System.out.println(person.age + " " + person.name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getAge() {
		return age;
	}


	public void setAge(int age) {
		this.age = age;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
}


