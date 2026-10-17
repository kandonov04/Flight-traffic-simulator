package shapes;

import java.awt.Color;
import java.awt.Graphics;

import objects.Airport;

// Visual representation of a single airport on the map.
public class AirportShape extends MapShape {

	// Side length of the square in pixels
	public static final int SIZE = 16;
	// Square colors
	private static final Color NORMAL_COLOR = Color.GRAY;
	private static final Color SELECTED_COLOR = Color.RED;
	
	private final Airport airport;
	private boolean blinkOn = true;

	public AirportShape(Airport airport, int px, int py) {
		super(px, py);
		this.airport = airport;
	}

	public Airport getAirport() {
		return airport;
	}

	public void setBlinkOn(boolean blinkOn) {
		this.blinkOn = blinkOn;
	}

	// Whether the point lies inside this airport's square.
	public boolean contains(int a, int b) {
		int half = SIZE / 2;
		return (a >= px - half && a <= px + half) && (b >= py - half && b <= py + half);
	}

	@Override
	public void paint(Graphics g) {

		Color color;
		if (!airport.isSelected()) {
			color = NORMAL_COLOR;
		} else {
			color = blinkOn ? SELECTED_COLOR : NORMAL_COLOR;
		}
		g.setColor(color);

		int half = SIZE / 2;
		g.fillRect(px - half, py - half, SIZE, SIZE);

		g.setColor(Color.BLACK);
		g.drawRect(px - half, py - half, SIZE, SIZE);
		g.drawString(airport.getCode(), px + half + 4, py + 5);
	}
}