package gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import data.DataStorage;
import logic.Simulation;
import objects.Flight;
import logic.SimulationTimer;

import static gui.Style.*;

public class PhaseCWindow extends Frame {

	private final MainWindow mainWindow;

	private final MapCanvas mapCanvas;
	private final Simulation simulation;
	private final SimulationTimer clock;

	private Label clockLabel = new Label("00:00");

	private Button start = new Button("Start");
	private Button pause = new Button("Pause");
	private Button reset = new Button("Reset");
	private Button back = new Button("Back");

	// Setting up margins
	@Override
	public Insets getInsets() {
		Insets base = super.getInsets();
		return new Insets(base.top + MARGIN_CONTENT, base.left + MARGIN_CONTENT, base.bottom + MARGIN_CONTENT, base.right + MARGIN_CONTENT);
	}

	// Refreshes the map and clock according to the current simulation state
	private void render() {
		mapCanvas.setPlanes(simulation.getPlanes());
		clockLabel.setText(Flight.formatTime(simulation.getSimTime()));
		mapCanvas.repaint();
	}

	// Buttons for controlling the simulation
	private Panel createControls() {

		styleButton(start);
		styleButton(pause);
		styleButton(reset);
		styleButton(back);

		clockLabel.setForeground(BUTTON_TEXT);
		clockLabel.setFont(BUTTON_FONT);

		start.addActionListener((e) -> {
			clock.go();
			mainWindow.pauseInactivity();
		});

		pause.addActionListener((e) -> {
			clock.pause();
			mainWindow.unpauseInactivity();
		});

		reset.addActionListener((e) -> {
			clock.pause();
			simulation.reset();
			render();
			mainWindow.unpauseInactivity();
		});

		back.addActionListener((e) -> {
			closeWindow();
		});

		Panel controls = new Panel(new FlowLayout(FlowLayout.CENTER, MARGIN_BUTTON, 0));
		controls.setBackground(BACKGROUND);
		controls.add(clockLabel);
		controls.add(start);
		controls.add(pause);
		controls.add(reset);
		controls.add(back);
		return controls;
	}

	private void populateWindow() {

		setLayout(new BorderLayout(0, MARGIN_CONTENT));

		mapCanvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
		add(mapCanvas, BorderLayout.CENTER);
		add(createControls(), BorderLayout.SOUTH);
	}

	private void closeWindow() {
		mainWindow.unpauseInactivity();
		clock.interrupt();
		mapCanvas.stop();
		dispose();
		mainWindow.closePhase();
	}

	public PhaseCWindow(MainWindow mainWindow, DataStorage storage) {

		this.mainWindow = mainWindow;
		this.mapCanvas = new MapCanvas(storage);
		this.simulation = new Simulation(storage);

		// Every 200ms - advance time, recalculate planes, refresh the display
		this.clock = new SimulationTimer(Simulation.REFRESH_MS, () -> {
			simulation.step();
			render();
		});
		this.clock.start();

		setTitle("Phase C");
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