package coop.item;

public class Weapon extends ActionItem implements HasValue {
	
	double strength;

	public Weapon(String name) {
		super(name);
		init();
	}

	private void init() {
		setWeight(5.0); // FIXME
		setType("weapon");
		addAction("attack");				
	}

	public void setValue(Double value) {
		strength = value;
	}

	public Double getValue() {
		return strength;
	}

	public boolean removeValue(Double value) {
		if (value < strength) {
			strength -= value;
			return true;
		}
		return false;
	}

	public void addValue(Double value) {
		strength += value;
	}
}