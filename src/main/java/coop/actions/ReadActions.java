package coop.action;

import coop.player.Player;
import coop.map.Door;
import coop.item.*;

import java.util.*;

public class ReadActions extends BaseActions {

	static String type = "read";
	
	public static List<Action> getActions(Player player) {
		List<Action> actions = new ArrayList<Action>();
		
		List<Item> notes = player.getItemsOf(Note.class);
		if (notes != null) {
			for ( Item item : notes) {
				Note note = (Note)item;
				actions.add(new Action( "read",  type, convertObject(note), convertObject(note.getText()) ));
			}
		}
/*		
		for (Door door : doorActions.getOutsideDoors()) {
			String doorText = door.getText();
			if (doorText != null) {
				actions.add(new Action( "read", door, door.getText()));
			}
		}		
*/		
		return actions;
	}

	public static void doAction(Action action) { // nothing for server to do => client must display the text to the player

	}
}