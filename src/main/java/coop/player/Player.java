package coop.player;

import coop.map.*;
import coop.item.Item;
import coop.action.*;
import coop.engine.WebSocketController;

import java.util.*;

import org.springframework.beans.factory.BeanNameAware;

public class Player implements BeanNameAware {
	private String id;
	private String name;
	private String desc;
	private Position position;
	private Trajectory trajectory; // where the selected path is blocked & by what
//	private Place place;
	private List<Item> items;
	private double health;
	private double height;
	private double weight;
	private double strength; // max strength is 1.0
	private double load;

    public Player(String name) {
    	this.name = name;
    }

    @Override
	public void setBeanName(String beanName) {
		System.out.println(beanName +" bean has been initialized." );
		name = beanName;		
	}

	public String getID() {
		if (id == null) {			
			setID(WebSocketController.getUniqueID());
		}
		return id;
	}

	private void setID(int id) {
		this.id = "PLAYER"+Integer.toString(id);		
	}

	private void addActions(List<Action> actions, List<Action> acts) {
		if (acts != null) {
			actions.addAll(acts);
		}
	}

	public Trajectory getTrajectory() {
		return trajectory;
	}

	public List<Player> findOthers() {
		Place place = WebSocketController.getPlace(position);
		List<Player> players = place.getPlayers();
		if (players != null && players.size() > 1) {
			List<Player> others = new ArrayList<Player>();
			for (Player p : players) {
				if (p != this) {
					others.add(p);
				}
			}
			return others;
		}
		return null;
	}


	public PlayerOptions getOptions() {
		List<Action> actions = new ArrayList<Action>();		
		addActions(actions, ClimbActions.getActions(this));		
		addActions(actions, DoorActions.getActions(this));
		addActions(actions, ItemActions.getActions(this));
		addActions(actions, ReadActions.getActions(this));
		addActions(actions, WriteActions.getActions(this));
		Place place = WebSocketController.getPlace(position);
		String message = null;
		if (place != null) {
			message = place.getDesc();
		}
		if (message == null) {
			message = "Use the mouse to move across the map";
		}
		return new PlayerOptions(message, position, actions);		
	}

	// FIXME: we should be able to use polymorphism here 
	public PlayerOptions invokeAction(Action action) {
		if (action.getType().equals("climb")) {
			ClimbActions.doAction(this, action);
		}
		else if (action.getType().equals("door")) {
			DoorActions.doAction(action);
		}
		else if (action.getType().equals("item")) {
			ItemActions.doAction(this, action);
		}
		else if (action.getType().equals("read")) {
			ReadActions.doAction(action);
		}
		else if (action.getType().equals("write")) {
			WriteActions.doAction(this, action);
		}
		return getOptions();
	}

	public double getLoad() {
		return load;
	}

	public double getMaxLoad() {
		return weight*strength;
	}

	public double getHeight() {
		return height;
	}

	public double getWeight() {
		return weight;
	}
/*
	public Place getPlace() {
		Place place = null;
		GamePlaces allPlaces = WebSocketController.getPlaces();
		if (allPlaces != null) {
			place = allPlaces.findPlace(position);
		}
		return place;		
	}
*/
	public void setName(String name) {
		this.name = name;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setHeight(String height) {
		this.height = Double.parseDouble(height); 
	}

	public void setWeight(String weight) {
		this.weight = Double.parseDouble(weight); 
	}

	public void setStrength(String strength) {
		this.strength = Double.parseDouble(strength); 
	}

	public void setPlace(Place place) {
		position = place.getCenter();
	}

	private boolean areClose(Position p1, Position p2) {
		double dx = p2.getX() - p1.getX();
    	double dy = p2.getY() - p1.getY();
    	double dist = Math.sqrt(dx*dx + dy*dy);
    	if (dist < 1.0) {
    		return true;
    	}
    	return false;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public void updatePosition(Position position) {
		setPosition(position);
		if (trajectory.getDest() != null && areClose(position, trajectory.getDest())) {
			System.out.println("Stopping at " + position + " near " + trajectory.getDest());			
		}
		// Note: we need to keep checking for collision as we move cuz not
		//       all Position on the map are represented as a Place
		//       and Place is required to check for collision
		setTrajectory(trajectory.getOrigDest()); 
		WebSocketController.sendDest(trajectory.getDest());
		
	}

	public Position getPosition() {
		return position;	
	}

	public void addItem(Item item) {
		if (items == null) {
			items = new ArrayList<Item>();
		}
		System.out.println(name + " picked up " + item);
		items.add(item);
		load += item.getWeight();
	}

	public String getName() {
		return name;
	}

	public List<Item> getItems() {
		return items;
	}

	public void removeItem(Item item) {
		items.remove(item);
		System.out.println(this.name + " removed item " + item.getName());
		load -= item.getWeight();
	}

	public boolean hasItem(Item item) {
		if (items != null) {
			for (Item i : items) {
				if (item.equals(i)) {
					return true;
				}
			}
		}
		return false;
	}
	public boolean hasItemOf(Class c) {
		if (items != null) {
			for (Item item : items) {
				if (item.getClass().equals(c)) {
					return true;
				}
			}
		}
		return false;
	}

	public List<Item> getItemsOf(Class c) {
		if (items == null) {
			return null;
		}
		List<Item> rlist = new ArrayList<Item>();
		for (Item item : items) {
			if (item.getClass().equals(c)) {
				rlist.add(item);
			}
		}
		return rlist;
	}

	public Item getItemOf(Class c) {
		for (Item item : items) {
			if (item.getClass().equals(c)) {
				return item;
			}
		}
		return null;
	}

	public Item removeItemOf(Class c) {
		for (Item item : items) {
			if (item.getClass().equals(c)) {
				removeItem(item);
				return item;
			}
		}
		return null;
	}

	public void inflictDamage(double amount) {
		if (amount>0 && amount<1.0 ) {
			health = health * amount;
		}
	}

	public Position setTrajectory(Position destPos) {
		Place place = WebSocketController.getPlace(position);
		System.out.println("player position is " + position);
		Trajectory traj;
		if (place != null) {
			traj = place.getIntersection(position, destPos);
		}
		else {
			traj = new Trajectory(destPos);
		}
		this.trajectory = traj;
		
		return trajectory.getDest();
					
	}

}