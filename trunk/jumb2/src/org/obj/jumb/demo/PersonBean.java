package org.obj.jumb.demo;

import java.io.Serializable;

public class PersonBean implements Serializable {
	private String name;
    private int age;
    
    public PersonBean() {
    }

    public PersonBean(String name, int age) {
    	this.name = name;
    	this.age = age;
    }
    
    @Override
    public String toString() {
    	return "name : " + name + ", age : " + age;
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
