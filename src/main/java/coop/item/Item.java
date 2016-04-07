package coop.item;

import org.springframework.beans.factory.BeanNameAware;

import coop.engine.WebSocketController;

public class Item {
// public class Item implements BeanNameAware {	
	private String id;
	private String name;
	private String type;
	private double weight;	

    public Item(String name) {
    	this.name = name;
    }
/*
    @Override
	public void setBeanName(String beanName) {
		System.out.println(beanName +" bean has been initialized." );
		name = beanName;		
	}
*/
	public String getID() {
		if (id == null) {			
			setID(WebSocketController.getUniqueID());
		}
		return id;
	}

	private void setID(int id) {
		this.id = "ITEM"+Integer.toString(id);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getWeight() {
		return weight;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String toString() {
		return name + " id:" + id + " type:" + type;
	}

}