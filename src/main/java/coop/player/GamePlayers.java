package coop.player;

import coop.action.*;
import coop.map.*;

import java.util.*;

public class GamePlayers {
	private List<Player> available;
	private List<Player> allPlayers;
	private Map<String, Player> playing;

	public void init() {
		allPlayers = new ArrayList<Player>();
		for (Player player: available) {
			allPlayers.add(player);
		}
	}

	public void setAvailable(List<Player> available) {
		this.available = available;
	}

	public List<Player> getAvailable() {
		return available;
	}

	// FIXME: why not just use the name to perform a lookup in the map?
	public Player findPlayer(String id) {
		for (Player player : allPlayers) {			
			if (id.equals(player.getID())) {
				return player;
			}
		}
		return null;
	}

	public boolean pickPlayer(String name) {
		for (Player player : available) {
			if (player.getName().equals(name)) {
				available.remove(player);
				addPlayer(name, player);
//				player.setAllPlaces(allPlaces);
				return true;
			}
		}
		return false;
	}

	private void addPlayer(String name, Player player) {
		if (playing == null) {
			playing = new HashMap<String, Player>();
		}
		playing.put(name, player);
	}

	public PlayerOptions updatePosition(PlayerPosition playerPos) {
		Player player = playing.get(playerPos.getName());
		player.updatePosition(playerPos.getPosition());
		return player.getOptions();
	}

	public PlayerOptions invokeAction(PlayerAction playerAction) {
		Action action = new Action(playerAction.getAction());
		if (action.getAction().equals("Play As"))  {
			ActionObject ao = action.getObjects().get(0);
			Player player = findPlayer(ao.getID());
//			Player player = (Player)action.getObject();           
            if (pickPlayer(player.getName())) {
                System.out.println("Playing as " + player.getName());
                PlayerOptions po = player.getOptions();
                po.setPosition(player.getPosition());
                return po;
            }
            else {
                System.out.println("Player " + player.getName() + " is not available"); 
            }
            return null;
        }
        else if (action.getAction().equals("Start")) {        	       	
        	return new PlayerOptions("Pick a player", "Play As", available);
        }
        else {
			Player player = playing.get(playerAction.getName());
			return player.invokeAction(action);
		}
	}

	public Position setTrajectory(PlayerPosition destPos) {
		Player player = playing.get(destPos.getName());
		return player.setTrajectory(destPos.getPosition());		
	}
}