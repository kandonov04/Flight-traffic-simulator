package gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import data.DataStorage;
import objects.Airport;

import static gui.Style.*;


public class PhaseBWindow extends Frame {

	private final MainWindow mainWindow;
	private final DataStorage storage;

	private final MapCanvas mapCanvas;

	private Button back = new Button("Back");

	// Setting up margins
	@Override
	public Insets getInsets() {
		Insets base = super.getInsets();
		return new Insets(base.top + MARGIN_CONTENT, base.left + MARGIN_CONTENT, base.bottom + MARGIN_CONTENT, base.right + MARGIN_CONTENT);
	}

	// List of airports with checkboxes
	private Panel createAirportList() {

		Panel list = new Panel(new GridLayout(0, 4, MARGIN_CONTENT, MARGIN_BUTTON));
		list.setBackground(BACKGROUND);

		for (Airport airport : storage.getAirports()) {

			String text = airport.getCode() + " - " + airport.getName() + " (" + airport.getX() + ", " + airport.getY() + ")";
			Checkbox checkbox = new Checkbox(text, airport.isVisible());
			checkbox.setForeground(BUTTON_TEXT);

			checkbox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					airport.setVisible(e.getStateChange() == ItemEvent.SELECTED);
					mapCanvas.refresh();
				}
			});

			list.add(checkbox);
		}

		return list;
	}

	// Footer
	private Panel createFooter() {
		styleButton(back);

		back.addActionListener((e) -> {
			closeWindow();
		});

		Panel footer = new Panel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		footer.setBackground(BACKGROUND);
		footer.add(back);
		return footer;
	}

	private void populateWindow() {

		setLayout(new BorderLayout(0, MARGIN_CONTENT));

		mapCanvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
		add(mapCanvas, BorderLayout.CENTER);

		// The list can be taller than the window, so it goes into a ScrollPane
		ScrollPane scroll = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
		scroll.add(createAirportList());
		scroll.setPreferredSize(new Dimension(CANVAS_WIDTH, LIST_HEIGHT));

		Panel south = new Panel(new BorderLayout(0, MARGIN_BUTTON));
		south.setBackground(BACKGROUND);
		south.add(scroll, BorderLayout.CENTER);
		south.add(createFooter(), BorderLayout.SOUTH);
		add(south, BorderLayout.SOUTH);
	}

	// The selection is cleared so the airport doesn't stay selected when the phase is reopened
	private void closeWindow() {

		mapCanvas.deselectAll();
		mainWindow.unpauseInactivity();
		mapCanvas.stop();
		dispose();
		mainWindow.closePhase();
	}

	public PhaseBWindow(MainWindow mainWindow, DataStorage storage) {

		this.mainWindow = mainWindow;
		this.storage = storage;
		this.mapCanvas = new MapCanvas(storage);

		// While an airport is selected, the inactivity timer is paused
		mapCanvas.setClickListener((airport) -> {
			mapCanvas.selectAirport(airport);
			if (mapCanvas.hasSelection()) {
				mainWindow.pauseInactivity();
			} else {
				mainWindow.unpauseInactivity();
			}
		});

		setTitle("Phase B");
		setLocation(200, 120);
		setResizable(true);
		setBackground(BACKGROUND);

		populateWindow();

		pack();

		// Resets the inactivity timer
		mainWindow.registerActivity(this);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeWindow();
			}
		});

		setVisible(true);
	}
}