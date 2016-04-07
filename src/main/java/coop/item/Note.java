package coop.item;

public class Note extends ActionItem implements HasText {

	private String text;

	public Note(String name) {
		super(name);
		setWeight(0.0);
	}

	public void setText(String noteText) {
		this.text = noteText;
	}
	
	public String getText() {
		return text;
	}
	
}