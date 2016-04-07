package coop.item;

public class Paper extends ChangeItem {

	public Paper(String name) {
		super(name);
		init();
	}

	private void init() {
		addChange("write", Note.class);
		setType("paper");
	}
	
}