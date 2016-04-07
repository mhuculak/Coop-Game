package coop.map;

import coop.engine.WebSocketController;

import org.springframework.beans.factory.BeanNameAware;

public class Wall implements BeanNameAware {

	private String name;
	private Door door;	
	private double height;
	private Place inner; // one side of the wall
	private Place outer; // other side of the wall	
	private boolean hasRoof;
	private String id;

	public Wall(String name) {
		this.name = name;
	}

	@Override
	public void setBeanName(String beanName) {
		System.out.println(beanName +" bean has been initialized." );
		name = beanName;		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public void setHasRoof(boolean hasRoof) {
		this.hasRoof = hasRoof;
	}

	public void setDoor(Door door) {
		this.door = door;
	}

	private void checkDoor() {
		if (door != null) {
			System.out.println("wall " + id + " has door " + door);
		}
	}
	public Door getDoor() {
		checkDoor();
		return door;
	}
	
	public double getHeight() {
		return height;
	}

	public boolean hasRoof() {		
		return hasRoof;
	}

	public Place getInside() {
		return inner;
	}

	public Place getOutside() {
		return outer;
	}

	public String getID() {
		checkDoor();
		if (id == null) {			
			setID(WebSocketController.getUniqueID());
		}
		return id;
	}

	private void setID(int id) {
		this.id = "WALL"+Integer.toString(id);
	}

	public String toString() {
		if (door == null) {
			return name + " id:" + id + " (no door)";
		}
		else {
			return name + " id:" + id + " (door " + door + ")";
		}
	}

}