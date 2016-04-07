package coop.map;

import java.util.*;

public class GameWalls {

	private List<Wall> walls;

	public void setWalls(List<Wall> walls) {
		this.walls = walls;
	}

	// FIXME: should not need to have two lookup methods
	public Wall findWall(String name) {
		for (Wall wall : walls) {
			if (name.equals(wall.getName())) {
				return wall;
			}
		}
		return null;
	}

	public Wall findWallbyID(String id) {
		for (Wall wall : walls) {
			if (id.equals(wall.getID())) {
				return wall;
			}
		}
		return null;
	}

	public void printWalls() {
		for ( Wall wall : walls) {
			System.out.println(wall);
		}
	}
	
}