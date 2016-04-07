package coop.player;

import coop.action.*;
import coop.player.PAction;
import coop.map.Position;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.*;


@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerOptions {
	private String message;
	private Position position;	
	private List<Action> actions;

	public PlayerOptions(String message, Position p, List<Action> actions) {
		this.message = message;
		this.actions = actions;
		this.position = p;
	}

	public PlayerOptions(String message, String action, List<Player> players) {
		this.message = message;
		actions = new ArrayList<Action>();
		for (Player player : players) {
			System.out.println("Adding " + action + " for " + player.getName());			
			ActionObject ao = new ActionObject();
			ao.setName(player.getName());
			ao.setID(player.getID());
			actions.add(new Action(action, "player", ao));
//			actions.add(new Action(action, "player", player));
		}
	}	

	public void setPosition(Position position) {
		this.position = position;
	}

	public Position getPosition() {
		return position;
	}

	public String getMessage() {
		return message;
	}

	public List<Action> getActions() {
		return actions;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("PlayerOptions - message:" + message + " actions: ");
		for (Action action: actions) {
			sb.append(action.toString()+",");
		}
		return sb.toString();
	}

}