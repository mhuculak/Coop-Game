package coop.map;

public class Trajectory {
	private Position intersection;
	private Position origDest;
	private Object obstacle;

	public Trajectory(Position d) {		
		this.origDest = d;		
	}

	public Trajectory(Position origDest, Position inter, Object o) {
		this.origDest = origDest;
		this.intersection = inter;
		this.obstacle = o;
	}

	public Position getDest() {
		if (intersection != null) {
			return intersection;
		}
		else {
			return origDest;
		}
	}

	public Position getOrigDest() {
		return origDest;
	}

	public Object getObstacle() {
		return obstacle;
	}

	public String toString() {
		if (obstacle == null) {
			return "no obstacle for dest " + origDest;
		}
		else {
			if (obstacle instanceof Wall) {
				Wall wall = (Wall)obstacle;
				return "Path is blocked by " + wall.getName();
			}
			else if (obstacle instanceof String) {
				String v = (String)obstacle;
				return "Path is blocked by " + v;
			}	
		}
		return null;
	}
}