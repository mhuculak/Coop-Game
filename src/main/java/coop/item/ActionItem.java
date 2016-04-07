package coop.item;

import java.util.*;

public class ActionItem extends Item {

	private List<String> actions;

    public ActionItem(String name) {
    	super(name);
    }

	public List<String> getActions() {
		return actions;
	}

	public void setActions(List<String> actions) {
		this.actions = actions;
	}

	public void addAction(String action) {
		if (actions == null) {
			actions = new ArrayList<String>();
		}
		actions.add(action);
	}
	
}