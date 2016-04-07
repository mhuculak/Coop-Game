package coop.action;

import coop.item.Item;
import coop.player.Player;
import coop.map.Wall;
import coop.map.Door;

public class BaseActions {

	public static ActionObject convertObject(Object obj) {
		ActionObject aobj = new ActionObject();
		
		if (obj instanceof Player) {
			Player player = (Player)obj;			
			aobj.setName(player.getName());			
			aobj.setID(player.getID());
		}
		else if (obj instanceof Item) {
			Item item = (Item)obj;			
			aobj.setName(item.getName());			
			aobj.setID(item.getID());
		}
		else if (obj instanceof Wall) {
			Wall wall = (Wall)obj;			
			aobj.setName("wall");			
			aobj.setID(wall.getID());
		}
		else if (obj instanceof Door) {
			Door door = (Door)obj;			
			aobj.setName("door");			
			aobj.setID(door.getID());
		}
		else if (obj instanceof String) {
			String str = (String)obj;
			aobj.setName(str);
		}
		return aobj;
	}	
}