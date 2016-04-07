package coop.action;

public class ActionObject {
	private String name;
	private String id;

	public void setName(String name) {
		this.name = name;
	}

	public void setID(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public String getID() {
		return id;
	}

	public String toString() {
		return "ActionObject - name:"+name + " id:"+id;		
	}
}