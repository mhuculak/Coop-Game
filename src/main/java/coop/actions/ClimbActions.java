package coop.action;

import coop.player.Player;
import coop.map.*;

import coop.engine.WebSocketController;

import java.util.*;

public class ClimbActions extends BaseActions {

	static String type = "climb";
		
	

	public static List<Action> getActions(Player player) {
		List<Action> actions = new ArrayList<Action>();
		System.out.println(player.getName() + " checking for walls..");
		Trajectory trajectory = player.getTrajectory();
		if (trajectory != null && trajectory.getObstacle() instanceof Wall)	{			
			Wall wall = (Wall)trajectory.getObstacle();		
			System.out.println("Found wall " + wall.getID());		
			if (wall.hasRoof() == false) {
				if (wall.getHeight() < player.getHeight()) {
					actions.add(new Action( "climb", type, convertObject(wall)));				
				}
				else if (player.findOthers() != null) {
					for (Player p: player.findOthers()) {
						if (!player.equals(p)) {
							if (wall.getHeight() < p.getHeight() + player.getHeight() &&
								p.getWeight() < player.getWeight()) {
								actions.add( new Action("boost", type, convertObject(wall), convertObject(p)));
							}
						}
					}
				}
			}
		}
		
		return actions;
	}

	public static void doAction(Player player, Action action) {	
		Place place = WebSocketController.getPlace(player.getPosition());	
		if (type.equals(action.getType())) {			
			Wall wall = WebSocketController.getWallbyID(action.getObjects().get(0).getID());
			Place adjacentPlace = place.getAdjacent(wall);
			System.out.println("processing climb action for wall " + wall.getName() + " from " + place.getName() + " to " + adjacentPlace.getName());
			if (action.getAction().equals("climb")) { // player climbed wall				
				player.setPlace(adjacentPlace);
			}
			else if (action.getAction().equals("boost")) { // friend climbed wall with your help
				Player friend = WebSocketController.getPlayer(action.getObjects().get(1).getID());				
				friend.setPlace(adjacentPlace);
				
			}			
		}
		else {
			System.out.println("ERROR: ClimbAction could not invoke action " + action.getAction() + " of type " + action.getType());
		}		
	}
}