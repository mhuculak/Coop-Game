package coop.map;

import coop.engine.WebSocketController;

import coop.item.*;

import org.springframework.beans.factory.BeanNameAware;

public class Door implements BeanNameAware {
	
	private String name;
	private boolean locked;
	private boolean isOpen;
	private String doorText;	
	private Key key;
	private String id;

	public Door(String name) {
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

	public String getText() {
		return doorText;
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public boolean isLocked() {	
		return locked;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setLocked(boolean isLocked) {
		this.locked = isLocked;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public String getID() {
		if (id == null) {			
			setID(WebSocketController.getUniqueID());
		}
		return id;
	}

	private void setID(int id) {
		this.id = "DOOR"+Integer.toString(id);
	}

	public String toString() {
		return id + " locked = " + Boolean.toString(locked) + " open = " + Boolean.toString(isOpen);
	}
}