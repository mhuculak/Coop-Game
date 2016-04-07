package coop.player;

import coop.action.Action;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerAction {
	private String name; //player name
	private PAction action;	
	
	public String getName() {
		return name;
	}

	public PAction getAction() {
		return action;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAction(PAction action) {
		this.action = action;
	}
	
}