package coop.map;

import coop.player.Player;
import coop.item.Item;

import coop.engine.WebSocketController;

import java.util.*;
import java.awt.Polygon;

import org.springframework.beans.factory.BeanNameAware;

public class Place implements BeanNameAware {
    private String name;
    private int cellID;
	private List<Player> players;
	private Map<String, String> wallMap;
	private Map<String, String> waterMap;
	private Object[] obstacles;
	private List<Wall> walls;	
	private List<Item> items;
	private List<Door> insideDoors;
	private List<Door> outsideDoors; 
	private String desc;
	private Position position;
	private Position center;
	private int cellsPerRow;
	private double edgeSize;
	private double yLen;
	private Polygon hexagon;

	public Place(String name) {
    	this.name = name;
    }

    @Override
	public void setBeanName(String beanName) {
		System.out.println(beanName +" bean has been initialized." );
		name = beanName;		
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public Object[] getObstacles() {
		if (obstacles == null) {
			obstacles = new Object[6];
			if (wallMap != null) {
				System.out.println("place " + name + " has a wallMap");
				int num = 0;
				for ( String key : wallMap.keySet()) {
					System.out.println("wallMap key = " + key + " value = " + wallMap.get(key));
					Wall wall = WebSocketController.getWall(key);
					System.out.println("Found wall obstacle " + wall.getID());
					int index = Integer.parseInt(wallMap.get(key));
					System.out.println("wall " + wall.getID() + " found in direction " + index);
					obstacles[index] = wall;
					num++;
				}
				System.out.println("Created " + num + " obstacles for " + name);
			}
			if (waterMap != null) {
				System.out.println("place " + name + " has a waterMap");
				int num = 0;
				for ( String key : waterMap.keySet()) {
					System.out.println("waterMap key = " + key + " value = " + waterMap.get(key));
					int index = Integer.parseInt(waterMap.get(key));					
					obstacles[index] = new String("water");
					num++;
				}
				System.out.println("Created " + num + " obstacles for " + name);
			}

		}
		return obstacles;
	}

	public Place getAdjacent(Wall wall) {
		System.out.println("Looking for place on the other side of " + wall.getName());
		if (wallMap != null) {
			for ( String key : wallMap.keySet()) {
				int index = Integer.parseInt(wallMap.get(key));				
				Wall w = WebSocketController.getWall(key);
				if (w == null) {
					System.out.println("ERROR: no wall found for key " + key + " index " + index);
				}
				else {
					System.out.println("Found wall " + w.getName() + " using key " + key + " checking ...");
				}
				if (wall.getName().equals(w.getName())) {
					int cellID = getAdjacentCell(index);
					return WebSocketController.getPlace(cellID);					
				}
			}
		}
		return null;
	}

	public List<Wall> getWalls() {
		if (walls == null && wallMap != null) {
			System.out.println("Finding walls for "+ name);
			walls = new ArrayList<Wall>();
			for ( String key : wallMap.keySet()) {
				Wall wall = WebSocketController.getWall(key);
				System.out.println("Found wall " + wall.getID());
				walls.add(wall);
			}
		}
		return walls;
	}
 
	public Wall hasWall(int index) {
		if (wallMap != null) {
			for ( String key : wallMap.keySet()) {
				int value = Integer.parseInt(wallMap.get(key));
				if (index == value) {
					return WebSocketController.getWall(key);
				}
			}
		}
		return null;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public List<Item> getItems() {
		return items;
	}

	public Position getPosition() {
		return position;
	}

	public int getCellID() {
		return cellID;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public void setWallMap(Map<String, String> wallMap) {
		this.wallMap = wallMap;
	}

	public void setWaterMap(Map<String, String> waterMap) {
		this.waterMap = waterMap;
	}

	public void setPosition(Position position) {
		this.position = position;
		hexagon = getHexagon(position);
		center = new Position(position.getX()+edgeSize, position.getY()+yLen/2.0);		
	}

	public void setEdgeSize(double edgeSize) {
		this.edgeSize = edgeSize;
		this.yLen = edgeSize*Math.sqrt(3.0);
	}

	public void setCellsPerRow(int cellsPerRow) {
		this.cellsPerRow = cellsPerRow;
	}

	public void setCellID(int cellID) {
		this.cellID = cellID;
	}
	
	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public boolean hasItem(String itemToPick) {
		return true;
	}

	public void addItem(Item item) {
		if (items == null) {
			items = new ArrayList<Item>();
		}
		items.add(item);
	}

	public void removeItem(Item item) {
		items.remove(item);		
	}

	private int[] getHex_X(int xs) {
    	int[] xArray = new int[6];
    	xArray[0] = xs + (int)(edgeSize/2.0 + 0.5);
    	xArray[1] = xs + (int)(3.0*edgeSize/2.0 + 0.5);
    	xArray[2] = xs + (int)(2.0*edgeSize + 0.5);
    	xArray[3] = xArray[1];
    	xArray[4] = xArray[0];
    	xArray[5] = xs;
    	return xArray;
    }

    private int[] getHex_Y(int ys) {
    	int[] yArray = new int[6];
    	yArray[0] = ys;
    	yArray[1] = ys;
    	yArray[2] = ys + (int)(yLen/2.0 + 0.5);
    	yArray[3] = ys + (int)(yLen+0.5);
    	yArray[4] = yArray[3];
    	yArray[5] = yArray[2];
   	 	return yArray;
    }

	private Polygon getHexagon(Position pos) {
		int xs = (int)pos.getX();
		int ys = (int)pos.getY();
    	Polygon p = new Polygon( getHex_X(xs), getHex_Y(ys), 6);
    	return p;
    }

	public boolean contains(Position pos) {
		return hexagon.contains(pos.getX(), pos.getY());
	}

	public Position getCenter() {
		return center;
	}

	private Position near(Position s, Position p, double backoff) {
		Position t = sub(s, p);
		double d = distance(s, p);
		if (d > 0.0) {
			double z = 1.0 - backoff/d;
			Position b = new Position( s.getX() + z*t.getX(), s.getY() + z*t.getY());
			System.out.println("backing off from " + p + " to " + b);
			return b;
		}
		return p;
	}

	private boolean isOpen(Object obj) {
		if (obj instanceof Wall) {
			Wall wall = (Wall)obj;
			Door door =  wall.getDoor();
			if (door != null) {
				return door.isOpen();
			}
		}
		return false;
	}

	public Trajectory getIntersection(Position start, Position dest) {
		double dist = distance(start, dest, center);
		System.out.println("place "+name + ": distance of trajectory to center " + center + " is "+ dist);
		if (dist > edgeSize) {
			System.out.println("current place is too far");
			return null; // no intersection cuz min dist is larger than size of the cell
		}
		Position[] vertex = getVertices();
		Position[] intersection = new Position[6];
		double minDist = edgeSize*100;
		double maxExit = 0.0;
		Position closestIntersection = null;
		Object[] obs = getObstacles();
		int exitIndex = -1;
		int blockedIndex = -1;
		boolean hasEntrance = !this.contains(start);
		boolean hasExit = !this.contains(dest);
		if (!hasEntrance) {
			System.out.println("the current position is in " + this.name );
		}
		if (!hasExit) {
			System.out.println("the requested destination is in " + this.name );
		}
		for ( int i=0 ; i<6 ; i++) {
			int next = (i==5) ? 0 : i+1;
			System.out.println("check intersection with " + vertex[i] + " and " + vertex[next]);
			intersection[i] = getIntersection(start, dest, vertex[i], vertex[next]);
			if (intersection[i] != null) {
				if (obs[i] != null && !isOpen(obs[i])) {
					System.out.println("Found intersection " + intersection[i]);
					double d1 = distance(start, vertex[i]);
					double d2 = distance(start, vertex[next]);
					double d = (d1>d2) ? d2 : d1;
					if (d < minDist) {
						minDist = d;
						closestIntersection = intersection[i];
						blockedIndex = i;
						System.out.println("using intersection " + closestIntersection + " min dist is " + minDist); 
					}
					else {
						System.out.println("rejecting intersection " + intersection[i] + " with dist " + d + " > " + minDist); 
					}
				}
				else if (hasExit) {					
					Double d = distance(start, intersection[i]);
					if (d > maxExit) {
						exitIndex = i;
						maxExit = d;
					}	
				}
			} 
		}
		if (closestIntersection == null) {
			if (exitIndex >= 0) {
				System.out.println("Trajectory exits cell (direction "+ exitIndex + ") at " + intersection[exitIndex]);
				int nextCellID = getAdjacentCell(exitIndex);
				Place nextPlace = WebSocketController.getPlace(nextCellID);
				if (nextPlace != null) {
					return nextPlace.getIntersection(start, dest);
				}
			}
			System.out.println("No intersection...");
			return new Trajectory(dest); // no intersection
		}
		else {
			if (obs[blockedIndex] instanceof Wall) {
				Wall wall = (Wall)obs[blockedIndex];
				System.out.println("Path is blocked by " + wall.getName());
			}
			else if (obs[blockedIndex] instanceof String) {
				String v = (String)obs[blockedIndex];
				System.out.println("Path is blocked by " + v);
			}	
			double backoff = 10.0;
			Position backoffPos = near( start, closestIntersection, backoff);
			return new Trajectory(dest, backoffPos, obs[blockedIndex]);
		}
	}

	private Position[] getVertices() {
		Position[] vertex = new Position[6];
    	double xs = position.getX();
    	double ys = position.getY();
    	double x0 = xs + edgeSize/2.0;
    	double x1 = xs + 3.0*edgeSize/2.0;
    	double x2 = xs + 2.0*edgeSize;
    	double y2 = ys + yLen/2.0;
    	double y3 = ys + yLen;
    	vertex[0] = new Position(x0, ys);
    	vertex[1] = new Position(x1, ys);
    	vertex[2] = new Position(x2, y2);
    	vertex[3] = new Position(x1, y3);
    	vertex[4] = new Position(x0, y3);
    	vertex[5] = new Position(xs, y2);
    	return vertex;
	}

	// find intersection of trajectory and an edge of the cell (defined by two consecutive vertices)
	private Position getIntersection(Position s, Position d, Position v1, Position v2) {
		Position t = sub(s,d);
//		System.out.println(" t = " + t);
		Position e = sub(v1,v2);
		double txe = cross(t,e);
		Position sv = sub(s,v1);
		double svxt = cross(sv,t);		
		if (txe == 0.0 && svxt == 0.0) {
			System.out.println("lines are parallel...");
			double tt = dot(t,t);
			double z0 = dot(sv, t) / tt;
			Position w = add(sv,t);
			double z1 = dot(w,t) / tt;
			if (z0 > 1.0 || z1 < 0.0) {

				return null;
			}
			else {
				return new Position(s.getX() + z0*t.getX(), s.getY() + z0*t.getX());								
			}
		}
		else if (txe == 0.0) {
			return null;
		}	
		else {
//			System.out.println("compute normal intersection...");
			double svxe = cross(sv,e);
			double z = svxe / txe;
			double u = svxt / txe;
//			System.out.println("got params z = " + z + " u = " + u);
			if (z>=0.0 && z<=1.0 && u>=0.0 && u<=1.0) {
				return new Position(s.getX() + z*t.getX(), s.getY() + z*t.getX());				
			}
		}
		return null;
	}

	private double dot(Position v1, Position v2) {
		return v1.getX()*v2.getX() + v1.getY()*v2.getY();
	}

	// compute 2-D cross product of two vectors
	private double cross(Position v1, Position v2) {
		return v1.getX() * v2.getY() - v1.getY() * v2.getX(); 
	}

	private double distance(Position p1, Position p2) {
		double dx = p2.getX() - p1.getX();
    	double dy = p2.getY() - p1.getY();
    	return Math.sqrt(dx*dx + dy*dy);
	}

	// compute distance from point ot line segment
	private double distance(Position p1, Position p2, Position p) {    	
    	double dx = p2.getX() - p1.getX();
    	double dy = p2.getY() - p1.getY();    
    	double dist = dy*p.getX() - dx*p.getY() + p2.getX()*p1.getY() - p2.getY()*p1.getX();
    	dist = (dist<0) ? -dist : dist;
    	dist = dist / Math.sqrt(dx*dx + dy*dy);
    	return dist;
  	}

  	private Position sub(Position p1, Position p2) {
  		return new Position(p2.getX() - p1.getX(), p2.getY() - p1.getY());  		
  	}

  	private Position add(Position p1, Position p2) {
  		return new Position(p2.getX() + p1.getX(), p2.getY() + p1.getY());
  	}

  	private int getAdjacentCell(int index) {  		
  		int row = cellID/cellsPerRow;
  		int[] outerCell = new int[6]; 
    	outerCell[0] = cellID - 2*cellsPerRow;
    	outerCell[1] = cellID - cellsPerRow;
    	outerCell[2] = cellID + cellsPerRow;
    	outerCell[3] = cellID + 2*cellsPerRow;
    	outerCell[4] = cellID + cellsPerRow - 1;
    	outerCell[5] = cellID - cellsPerRow - 1;
    	if ( (row % 2) == 1 ) {
       		outerCell[1] += 1;
       		outerCell[2] += 1;
       		outerCell[4] += 1;
       		outerCell[5] += 1;
    	}
    	return outerCell[index];	
  	}

	public List<Door> getOutsideDoors() {		
		if (outsideDoors == null) {
			outsideDoors = new ArrayList<Door>();
			System.out.println("Find outside doors for " + name);
			for (int i=0 ; i<6 ; i++) {
				int reverseDirection = (i < 3) ? i+3 : i-3;
				int cellID = getAdjacentCell(i);
				Place place = WebSocketController.getPlace(cellID);
				if (place != null) {
					System.out.println("Found adjacent place " + place.getName());
					Wall wall = place.hasWall(reverseDirection);
					if (wall != null) {
						System.out.println("Found adjacent wall " + wall + " direction " + reverseDirection);
						Door door = wall.getDoor();
						if (door != null) {
							System.out.println("Found door " + door.getID());
							outsideDoors.add(door);
						}						
					}
				}
			}

		}
		return outsideDoors;
	}

	public List<Door> getInsideDoors() {

		if (insideDoors == null) {
			insideDoors = new ArrayList<Door>();
			if (wallMap != null) {
				System.out.println("Find inside doors for " + name);
				for ( String key : wallMap.keySet()) {
					System.out.println("wallMap key = " + key + " value = " + wallMap.get(key));
					Wall wall = WebSocketController.getWall(key);
					Door door = wall.getDoor();
					if (door != null) {
						System.out.println("Found door " + door.getID());
//						int index = Integer.parseInt(wallMap.get(key));
						insideDoors.add(door);
					}
				}
			}
		}
		return insideDoors;
	}
}