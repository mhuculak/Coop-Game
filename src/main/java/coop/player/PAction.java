package coop.player;

import coop.action.ActionObject;

import java.util.*;

/*
     needed cuz spring can't instantiate Action from JSON     
*/
public class PAction {
	public List<ActionObject> objects;
	public String action;
	public String type;
}