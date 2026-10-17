package gui;

import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Label;

// Central place for all colors, fonts and component styling
public class Style {

	// Margin on all 4 sides
	public static final int MARGIN_CONTENT = 20;

	// Spacing between buttons
	public static final int MARGIN_BUTTON = 10;

	// Default dialog size
	public static final int DIALOG_WIDTH = 360;
	public static final int DIALOG_HEIGHT = 150;

	// Background color - dark blue
	public static final Color BACKGROUND = new Color(40, 60, 80);

	// Button color - yellow
	public static final Color BUTTON = new Color(225, 170, 0);

	// Text color - white
	public static final Color BUTTON_TEXT = Color.WHITE;

	public static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 16);
	public static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 14);

	// Fixed character width - without it the table columns don't line up
	public static final Font TABLE_FONT = new Font("Monospaced", Font.PLAIN, 13);

	// Canvas dimensions
	public static final int CANVAS_WIDTH = 800;
	public static final int CANVAS_HEIGHT = 420;
	public static final int LIST_HEIGHT = 120;
	public static final int PADDING = 30;

	// Map grid colors
	public static final Color GRID_COLOR = new Color(230, 230, 230);
	public static final Color AXIS_COLOR = new Color(150, 150, 150);
	public static final Color LABEL_COLOR = new Color(120, 120, 120);

	public static void styleButton(Button button) {
		button.setBackground(BUTTON);
		button.setForeground(BUTTON_TEXT);
		button.setFont(BUTTON_FONT);
	}

	public static Label createLabel(String text) {
		Label label = new Label(text);
		label.setForeground(BUTTON_TEXT);
		label.setFont(LABEL_FONT);
		return label;
	}

	// ------------ TABLE FORMAT ------------

	public static final String AIRPORT_HEADER = String.format("%-5s %-28s %8s %8s", "CODE", "NAME", "X", "Y");
	public static final String FLIGHT_HEADER = String.format("%-5s %-5s %10s %10s", "FROM", "TO", "DEPARTURE", "DURATION");

	public static String airportRow(String code, String name, int x, int y) {
		return String.format("%-5s %-28s %8d %8d", code, name, x, y);
	}

	public static String flightRow(String from, String to, String departure, int duration) {
		return String.format("%-5s %-5s %10s %10d", from, to, departure, duration);
	}
}