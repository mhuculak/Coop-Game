package coop.action;

import coop.player.Player;
import coop.player.PAction;
import coop.item.Item;
import coop.engine.WebSocketController;

import java.util.*;

public class Action {

	List<ActionObject> objects;
	String action;
	String type;
/*
	public Action(String action, String type, Object obj ) {
		this.action = action;
		this.type = type;
		addObj(obj);
	}
*/
	public Action(String action, String type, ActionObject ao ) {
		this.action = action;
		this.type = type;
		addObj(ao);
	}

	public Action(String action, String type, ActionObject ao1,  ActionObject ao2) {
		this.action = action;
		this.type = type;
		addObj(ao1);
		addObj(ao2);
	}
/*
	public Action(String action, String type, Object obj1,  Object obj2) {
		this.action = action;
		this.type = type;
		addObj(obj1);
		addObj(obj2);
	}
*/
	public Action(PAction paction) {
		this.action = paction.action;
		this.type = paction.type;
		this.objects = paction.objects;
	}

	private void addObj(ActionObject ao) {
		if (objects == null) {
			objects = new ArrayList<ActionObject>();			
		}
		objects.add(ao);
	}

	public String getAction() {
		return action;
	}

	public String getType() {
		return type;
	}

	public List<ActionObject> getObjects() {
		return objects;
	}
/*
	public ActionObject getObject() {
		return 	objects.get(0);
	}

	public ActionObject getObject(int i) {
		return objects.get(i);
	}
*/
	public String toString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("Action - action:"+action+" type:"+type+" objects: ");
		for (ActionObject ao : objects) {
			sb.append(ao.toString()+",");
		}
		return sb.toString();
	}
}