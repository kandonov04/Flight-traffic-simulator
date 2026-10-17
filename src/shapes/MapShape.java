package shapes;

import java.awt.Graphics;

// Everything drawn on the map derives from this class
// Stores the position in pixels (NOT THE SAME AS A COORDINATE!)
public abstract class MapShape {

	protected int px;
	protected int py;

	public MapShape(int px, int py) {
		this.px = px;
		this.py = py;
	}

	public int getPx() {
		return px;
	}

	public int getPy() {
		return py;
	}

	public void setPosition(int px, int py) {
		this.px = px;
		this.py = py;
	}

	// Draws the shape on the given graphics surface
	public abstract void paint(Graphics g);
}