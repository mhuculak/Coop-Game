package coop.action;

import coop.player.Player;
import coop.map.*;
import coop.engine.WebSocketController;

import java.util.*;

public class DoorActions extends BaseActions {

	static String type = "door";
				
	public static List<Action> getActions(Player player) {		
		List<Action> actions = new ArrayList<Action>();
		Place place = WebSocketController.getPlace(player.getPosition());

		if (place == null) {
			return null;
		}
					
		for (Door door : place.getInsideDoors()) {
			System.out.println("Found inside door ");
			if (door.isOpen()) {
				actions.add(new Action("close", type, convertObject(door)));
			}	
			else {		
				if (door.isLocked()) {
					actions.add(new Action("unlock", type, convertObject(door)));
				}
				else {					
					actions.add(new Action("lock", type, convertObject(door)));
					actions.add(new Action("open", type, convertObject(door)));
				}
			} 
			
		}
		
		for (Door door : place.getOutsideDoors()) {
			System.out.println("Found outside door ");
			if (door.isOpen()) {
				actions.add(new Action("close", type, convertObject(door)));
			}
			else {	
				if (door.isLocked()) {
					if (player.hasItem(door.getKey())) {
						actions.add(new Action("unlock", type, convertObject(door)));						
					}
				}
				else {
					actions.add(new Action("open", type, convertObject(door)));
					if (player.hasItem(door.getKey())) {
						actions.add(new Action("lock", type, convertObject(door)));
					}
				}
			}							
		}	
		
		return actions;
	}

	public static void doAction(Action action) {
		Door door = WebSocketController.getDoor(action.getObjects().get(0).getID());
		if (action.getAction().equals("open")) {
			door.setOpen(true);
		}
		else if (action.getAction().equals("close")) {
			door.setOpen(false);
		}
		else if (action.getAction().equals("lock")) {
			door.setLocked(true);
		}
		else if (action.getAction().equals("unlock")) {
			door.setLocked(false);
		}
	}
	

}