package gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import data.DataStorage;
import objects.Airport;
import shapes.AirportShape;
import shapes.PlaneShape;
import logic.BlinkingTimer;

import static gui.Style.*;

// Canvas that displays airports and planes
// Converts geographic coordinates (x from -180 to 180, y from -90 to 90) into pixels on the canvas
public class MapCanvas extends Canvas {

	// Geographic coordinate bounds
	private static final int X_RANGE = Airport.MAX_X - Airport.MIN_X;
	private static final int Y_RANGE = Airport.MAX_Y - Airport.MIN_Y;

	// Grid step in geographic degrees - a line every 30 degrees
	private static final int GRID_STEP_X = 30;
	private static final int GRID_STEP_Y = 30;

	// How many milliseconds between blink phase changes
	private static final long BLINK_INTERVAL = 500;

	private final DataStorage storage;

	// Airport shapes - recreated on every refresh
	private ArrayList<AirportShape> shapes = new ArrayList<>();

	// Plane shapes - empty in phase B, filled by the simulation in phase C
	private ArrayList<PlaneShape> planes = new ArrayList<>();

	// Timer that toggles the blink phase of the selected airport
	private final BlinkingTimer blinkTimer;

	// Who gets notified of an airport click - phase B or phase C
	private AirportClickListener clickListener = (airport) -> {};

	// Current blink phase
	private boolean blinkOn = true;

	public MapCanvas(DataStorage storage) {
		this.storage = storage;
		setBackground(Color.WHITE);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				handleClick(e.getX(), e.getY());
			}
		});

		blinkTimer = new BlinkingTimer(BLINK_INTERVAL, () -> {
			blinkOn = !blinkOn;
			repaint();
		});
		blinkTimer.start();
	}

	
	// ------------ Coordinates ------------


	// Geographic X coordinate (from -180 to 180) to a canvas pixel
	private int toPixelX(double geoX) {
		int usableWidth = getWidth() - 2 * PADDING;
		return PADDING + (int) ((geoX - Airport.MIN_X) * usableWidth / X_RANGE);
	}

	// Geographic Y coordinate (from -90 to 90) to a canvas pixel
	private int toPixelY(double geoY) {
		int usableHeight = getHeight() - 2 * PADDING;
		return PADDING + (int) ((Airport.MAX_Y - geoY) * usableHeight / Y_RANGE);
	}

	// Canvas pixel to a geographic X coordinate
	private double toGeoX(int px) {
		int usableWidth = getWidth() - 2 * PADDING;
		return (double) (px - PADDING) * X_RANGE / usableWidth + Airport.MIN_X;
	}

	// Canvas pixel to a geographic Y coordinate
	private double toGeoY(int py) {
		int usableHeight = getHeight() - 2 * PADDING;
		return Airport.MAX_Y - (double) (py - PADDING) * Y_RANGE / usableHeight;
	}
	
	// ------------ Mouse ------------

	
	// Handles a click at position (mx, my)
	private void handleClick(int mx, int my) {

		// Finds the clicked airport and reports it to the window
		for (AirportShape shape : shapes) {
			if (shape.contains(mx, my)) {
				clickListener.onAirportClicked(shape.getAirport());
				return;
			}
		}
	}

	// Toggles the selection state of the given airport
	public void selectAirport(Airport airport) {
		airport.setSelected(!airport.isSelected());
		updateBlinking();
		repaint();
	}

	// Deselects all airports
	public void deselectAll() {
		for (Airport airport : storage.getAirports()) {
			airport.setSelected(false);
		}
	}

	// Whether any airport is currently selected
	public boolean hasSelection() {
		for (Airport airport : storage.getAirports()) {
			if (airport.isSelected()) {
				return true;
			}
		}
		return false;
	}

	
	// ------------ Blinking ------------

	
	// Starts the blinking timer if there is a selection
	private void updateBlinking() {
		if (hasSelection()) {
			blinkTimer.go();
		} else {
			blinkTimer.pause();
			blinkOn = true;
		}
	}

	// Stops the blinking timer when the window is closed
	public void stop() {
		blinkTimer.interrupt();
	}

	// ------------ Drawing ------------

	// Called on every paint, since the canvas size and content may change
	private void rebuildShapes() {
		shapes = new ArrayList<>();
		for (Airport airport : storage.getAirports()) {
			if (airport.isVisible()) {
				int px = toPixelX(airport.getX());
				int py = toPixelY(airport.getY());
				shapes.add(new AirportShape(airport, px, py));
			}
		}
	}
	
	// Draws the coordinate grid
	private void drawGrid(Graphics g) {

		// Vertical lines - at every round x value
		for (int geoX = Airport.MIN_X; geoX <= Airport.MAX_X; geoX += GRID_STEP_X) {
			int px = toPixelX(geoX);

			// The Y axis is highlighted
			g.setColor(geoX == 0 ? AXIS_COLOR : GRID_COLOR);
			g.drawLine(px, toPixelY(Airport.MAX_Y), px, toPixelY(Airport.MIN_Y));

			// Number below the bottom edge
			g.setColor(LABEL_COLOR);
			g.drawString(String.valueOf(geoX), px - 8, toPixelY(Airport.MIN_Y) + 18);
		}

		// Horizontal lines - at every round y value
		for (int geoY = Airport.MIN_Y; geoY <= Airport.MAX_Y; geoY += GRID_STEP_Y) {
			int py = toPixelY(geoY);

			// The X axis is highlighted
			g.setColor(geoY == 0 ? AXIS_COLOR : GRID_COLOR);
			g.drawLine(toPixelX(Airport.MIN_X), py, toPixelX(Airport.MAX_X), py);

			// Number to the left of the left edge
			g.setColor(LABEL_COLOR);
			g.drawString(String.valueOf(geoY), toPixelX(Airport.MIN_X) - 24, py + 4);
		}
	}

	@Override
	// The grid is drawn first, then airports, then planes on top of them
	public void paint(Graphics g) {
		
		rebuildShapes();
		
		drawGrid(g);
		
		for (AirportShape shape : shapes) {
			shape.setBlinkOn(blinkOn);
			shape.paint(g);
		}
		
		for (PlaneShape plane : planes) {
			plane.setPosition(toPixelX(plane.getGx()), toPixelY(plane.getGy()));
			plane.paint(g);
		}
	}

	// Sets who gets notified of an airport click - phase B or phase C
	public void setClickListener(AirportClickListener clickListener) {
		this.clickListener = clickListener;
	}

	// Sets the list of planes to draw - stays empty in phase B
	public void setPlanes(ArrayList<PlaneShape> planes) {
		this.planes = planes;
	}

	// Refreshes the display
	// Called when the visibility of an airport changes in the checkbox list
	public void refresh() {
		updateBlinking();
		repaint();
	}
}