package coop.item;

public class Key extends ActionItem {
	public Key(String name) {
		super(name);
		init();
	}

	private void init() {
		setType("key");
		addAction("lock");
		addAction("unlock");
	}
}