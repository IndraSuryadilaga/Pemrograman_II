package modul_05;

public abstract class Shape {
	protected String shapeName;

	protected Shape(String name) {
		this.shapeName = name;
	}

	protected abstract double area();

	public String toString() {
		return this.shapeName;
	}

}
