package gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import dialogs.QuitDialog;
import dialogs.TimeoutDialog;
import data.DataStorage;
import logic.InactivityListener;
import logic.InactivityTimer;

import static gui.Style.*;

public class MainWindow extends Frame implements InactivityListener {

	// A single data storage for the whole application
	private final DataStorage storage = new DataStorage();

	// A single inactivity timer for the whole application
	private final InactivityTimer inactivityTimer = new InactivityTimer(this);

	// Countdown dialog - shown during the last 5 seconds before shutdown
	private TimeoutDialog timeoutDialog;

	// A single listener that resets the timer on a mouse click
	// Added to every component via registerActivity
	private final MouseListener activityListener = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			inactivityTimer.reset();
		}
	};

	private Button phaseA = new Button("Phase A");
	private Button phaseB = new Button("Phase B");
	private Button phaseC = new Button("Phase C");
	private Button exitApp = new Button("Exit simulator");

	// Setting up margins
	@Override
	public Insets getInsets() {
		Insets base = super.getInsets();
		return new Insets(base.top + MARGIN_CONTENT, base.left + MARGIN_CONTENT, base.bottom + MARGIN_CONTENT, base.right + MARGIN_CONTENT);
	}

	private void populateWindow() {

		styleButton(phaseA);
		styleButton(phaseB);
		styleButton(phaseC);
		styleButton(exitApp);

		phaseA.addActionListener((e) -> {
			setEnabled(false);
			new PhaseAWindow(this, storage);
		});

		phaseB.addActionListener((e) -> {
			setEnabled(false);
			new PhaseBWindow(this, storage);
		});

		phaseC.addActionListener((e) -> {
			setEnabled(false);
			new PhaseCWindow(this, storage);
		});

		exitApp.addActionListener((e) -> {
			QuitDialog dialog = new QuitDialog(this);
			if (dialog.ask()) {
				dispose();
				System.exit(0);
			}
		});

		// GridLayout makes all four buttons the same size
		Panel buttons = new Panel(new GridLayout(0, 1, 0, MARGIN_BUTTON));
		buttons.setPreferredSize(new Dimension(220, 200));
		buttons.add(phaseA);
		buttons.add(phaseB);
		buttons.add(phaseC);
		buttons.add(exitApp);

		add(buttons, BorderLayout.CENTER);
	}

	// Re-enables the menu when the open phase is closed
	public void closePhase() {
		setEnabled(true);
		toFront();
	}

	// ------------ INACTIVITY TIMER ------------

	// Adds the reset listener to the given component and all its descendants
	public void registerActivity(Component component) {
		component.addMouseListener(activityListener);
		if (component instanceof Container) {
			for (Component child : ((Container) component).getComponents()) {
				registerActivity(child);
			}
		}
	}

	// Pausing the timer - called by phases B and C in certain situations
	public void pauseInactivity() {
		inactivityTimer.pause();
	}

	public void unpauseInactivity() {
		inactivityTimer.unpause();
	}

	// Shows and hides the MessageDialog
	@Override
	public void onTick(int remaining) {
		if (remaining <= InactivityTimer.WARNING_SECONDS) {
			timeoutDialog.setSeconds(remaining);
			if (!timeoutDialog.isVisible()) {
				timeoutDialog.setVisible(true);
			}
			timeoutDialog.toFront();
		} else if (timeoutDialog.isVisible()) {
			timeoutDialog.setVisible(false);
		}
	}

	// Time is up, shutting down the program
	@Override
	public void onExpire() {
		dispose();
		System.exit(0);
	}

	public MainWindow() {

		setLocation(600, 300);
		setResizable(false);
		setTitle("Flight Simulator");
		setBackground(BACKGROUND);

		populateWindow();

		pack();

		// Countdown dialog; clicking Continue resets the timer and hides the dialog
		timeoutDialog = new TimeoutDialog(this, () -> {
			inactivityTimer.reset();
			timeoutDialog.setVisible(false);
		});

		// Resets the inactivity timer
		registerActivity(this);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				QuitDialog dialog = new QuitDialog(MainWindow.this);
				if (dialog.ask()) {
					dispose();
					System.exit(0);
				}
			}
		});

		setVisible(true);

		inactivityTimer.start();
	}
}