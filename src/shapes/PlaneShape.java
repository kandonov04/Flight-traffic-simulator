package shapes;

import java.awt.Color;
import java.awt.Graphics;

// Visual representation of a plane in the simulation - a blue circle.
public class PlaneShape extends MapShape {

	// Circle radius in pixels
	private static final int RADIUS = 6;

	// Plane color
	private static final Color PLANE_COLOR = Color.BLUE;

	// Position between the departure and arrival airports
	private final double gx;
	private final double gy;

	public PlaneShape(double gx, double gy) {
		super(0, 0);
		this.gx = gx;
		this.gy = gy;
	}

	public double getGx() {
		return gx;
	}

	public double getGy() {
		return gy;
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(PLANE_COLOR);
		g.fillOval(px - RADIUS, py - RADIUS, 2 * RADIUS, 2 * RADIUS);
	}
}