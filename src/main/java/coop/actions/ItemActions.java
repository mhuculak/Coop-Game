package coop.action;

import coop.player.Player;
import coop.item.Item;
import coop.map.Place;

import coop.engine.WebSocketController;

import java.util.*;

public class ItemActions extends BaseActions {

	static String type = "item";
	
	public static List<Action> getActions(Player player) {
		List<Action> actions = new ArrayList<Action>();
		Place place = WebSocketController.getPlace(player.getPosition());
		
		if (place == null) {
			return null; // FIXME: we may want to create places dynamically to allow droping items anywhere
		}
		System.out.println("Item getActiosn for " + player.getName() + " in " + place.getName());

		if (player.getItems() != null) {
			for ( Item item: player.getItems()) {
				System.out.println("new drop action for item name = " + item.getName() + " id = " + item.getID());
				actions.add(new Action( "drop", type, convertObject(item)));			
			}
		}
		if (place.getItems() != null) {
			for ( Item item: place.getItems()) {

/*				System.out.println("getActions found item " + item.getName());
				System.out.println("player load is " + player.getLoad());
				System.out.println("item weight is " + item.getWeight());
				System.out.println("player max load is " +  player.getMaxLoad()); */

				if (item.getWeight() + player.getLoad() < player.getMaxLoad()) {
					actions.add(new Action( "pick", type, convertObject(item)));	
				}
				else if (place.getPlayers() != null){
					for (Player p: place.getPlayers()) {
						double combinedLoad = player.getLoad() + p.getLoad();
						double combinedMaxLoad = player.getMaxLoad() + p.getMaxLoad();
						if (item.getWeight() + combinedLoad < combinedMaxLoad) {
							actions.add(new Action( "lift", type, convertObject(item), convertObject(p)));	
						}				
					}
				}
			}
		}
		if (place.getPlayers() != null) {
			for (Player p: place.getPlayers()) {
				if (!player.equals(p)) {
					for ( Item item: player.getItems()) {
						actions.add(new Action( "give", type, convertObject(item), convertObject(p)));
					}		
				}
			}
		}
		
		return actions;
	}

	public static void doAction(Player player, Action action) {
		System.out.println("doAction: " + player.getName() + " " + action.getAction());
			Place place = WebSocketController.getPlace(player.getPosition());
			System.out.println(action.getAction() + " item " + action.getObjects().get(0).getID());
			Item item = WebSocketController.getItem(action.getObjects().get(0).getID());	
			if (action.getAction().equals("pick")) {
				System.out.println("pick " + item);
				player.addItem(item);
				place.removeItem(item);
			}
			else if (action.getAction().equals("drop")) {
				System.out.println("drop " + item);
				place.addItem(item);
				player.removeItem(item);
			}
			else if (action.getAction().equals("give")) {
				System.out.println("give " + item);
				Player friend = WebSocketController.getPlayer(action.getObjects().get(1).getID());
				player.removeItem(item);
				friend.addItem(item);
			}
			else if (action.getAction().equals("lift")) {
				System.out.println("lift " + item);
				Player friend = WebSocketController.getPlayer(action.getObjects().get(1).getID());
				player.addItem(item);
				friend.addItem(item);
			}
				
	}

}