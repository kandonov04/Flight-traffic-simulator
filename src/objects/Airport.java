package objects;

public class Airport {

	// Coordinate bounds from the assignment specification
	public static final int MIN_X = -180;
	public static final int MAX_X = 180;
	public static final int MIN_Y = -90;
	public static final int MAX_Y = 90;

	// Exactly three uppercase letters
	private static final String REGEX = "[A-Z]{3}";

	private final String code;
	private final String name;
	private final int x;
	private final int y;
	private boolean visible = true;
	private boolean selected = false;

	public Airport(String name, String code, int x, int y) {

		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("Airport name must not be empty!");
		}
		if (code == null || !code.matches(REGEX)) {
			throw new IllegalArgumentException("Airport code must consist of exactly three uppercase letters");
		}
		if (x < MIN_X || x > MAX_X) {
			throw new IllegalArgumentException("X coordinate must be between " + MIN_X + " and " + MAX_X + " (entered: " + x + ").");
		}
		if (y < MIN_Y || y > MAX_Y) {
			throw new IllegalArgumentException("Y coordinate must be between " + MIN_Y + " and " + MAX_Y + " (entered: " + y + ").");
		}

		this.code = code;
		this.name = name.trim();
		this.x = x;
		this.y = y;
	}

	public String getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Airport)) {
			return false;
		}
		return code.equals(((Airport) obj).code);
	}

	@Override
	public int hashCode() {
		return code.hashCode();
	}
	
	@Override
	public String toString() {
		return code + " " + name + " (" + x + ", " + y + ")";
	}
}