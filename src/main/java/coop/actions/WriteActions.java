package coop.action;

import coop.player.Player;
import coop.item.*;

import coop.engine.WebSocketController;

import java.util.*;

public class WriteActions extends BaseActions {

	static String type = "write";

	public static List<Action> getActions(Player player) {
		List<Action> actions = new ArrayList<Action>();
		
		if (player.hasItemOf(Pen.class)) {
			Paper paper = (Paper)player.getItemOf(Paper.class);
			String noteText = "";			
			if (paper != null) {
				actions.add( new Action( "write", type, convertObject(paper), convertObject(noteText) ));
			}
		}		
		return actions;
	}

	public static void doAction(Player player, Action action) {

		if (action.getAction().equals("write")) {			
			Paper paper = (Paper)WebSocketController.getItem(action.getObjects().get(0).getID());
			String noteText = action.getObjects().get(1).getName();
			Note note = new Note("written note");
			note.setText(noteText);
			player.removeItem(paper);
			player.addItem(note);
			WebSocketController.removeItem(paper);
			WebSocketController.addItem(note);
		}		
	}	


}