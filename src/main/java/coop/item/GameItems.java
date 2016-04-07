package coop.item;

import java.util.*;

public class GameItems {
	private List<Item> items;

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public Item findItem(String id) {
		for (Item item : items) {
			if (id.equals(item.getID())) {
				return item;
			}
		}
		return null;
	}

	public void removeItem(Item item) {
		if (items != null) {
			items.remove(item);
		}
	}

	public void addItem(Item item) {
		if (items == null) {
			items = new ArrayList<Item>();
		}
		items.add(item);
	}
}