package coop.map;

import java.util.*;

public class GameDoors {
	private List<Door> doors;

	public void setDoors(List<Door> doors) {
		this.doors = doors;
	}

	public Door findDoor(String doorID) {
		System.out.println("Looking for " + doorID);
		if (doors != null) {
			for (Door door : doors) {
				if (doorID.equals(door.getID())) {
					System.out.println("Found a match for " + door);
					return door;
				}
				else if (doorID.equals(door.getName())) {
					System.out.println("Matched name for " + door);
				}
				else {
					System.out.println("No match for " + door);
				}
			}
		}
		return null;
	}
}