package coop.player;

import coop.map.Position;

public class PlayerPosition {
	private String name;
	private Position position;

	public void setName(String name) {
		this.name = name;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public String getName() {
		return name;
	}

	public Position getPosition() {
		return position;
	}
}