package coop.item;
import java.util.*;
import java.util.Map.Entry;

public class ChangeItem extends Item {

	private Map<String, Class<?>> changes;   // each change is a verb -> class mapping

	public ChangeItem(String name) {
		super(name);
	}

	

	public Map<String, Class<?>> getChanges() {
		return changes;
	}

	public void addChange(String action, Class c ) {
		if (changes == null) {
			changes = new HashMap<String, Class<?>>();
		}
		changes.put(action, c);
	}
	
}