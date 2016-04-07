package coop.item;

public class Pen extends ActionItem {
	public Pen(String name) {
		super(name);
		init();
	}

	private void init() {
		setType("pen");
		addAction("write");
	}
}