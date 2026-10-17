package gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import data.DataStorage;
import data.FileHandler;
import dialogs.MessageDialog;
import exceptions.FileFormatException;
import objects.Airport;
import objects.Flight;

import static gui.Style.*;

public class PhaseAWindow extends Frame {

	private static final String AIRPORTS = "a";
	private static final String FLIGHTS = "l";

	// The main menu that opened this window - needed when closing the phase window
	private final MainWindow mainWindow;
	
	// A single data storage for the whole application
	private final DataStorage storage;

	// Switching between the airports page and the flights page
	private CardLayout cards = new CardLayout();
	private Panel cardPanel = new Panel(cards);

	private Button showAirports = new Button("Airports");
	private Button showFlights = new Button("Flights");

	// Airport input
	private TextField airportName = new TextField(20);
	private TextField airportCode = new TextField(20);
	private TextField airportX = new TextField(20);
	private TextField airportY = new TextField(20);
	private Button addAirport = new Button("Add");
	private TextArea airportsTable = new TextArea("", 15, 55);

	// Flight input
	private TextField flightFrom = new TextField(20);
	private TextField flightTo = new TextField(20);
	private TextField flightDeparture = new TextField(20);
	private TextField flightDuration = new TextField(20);
	private Button addFlight = new Button("Add");
	private TextArea flightsTable = new TextArea("", 15, 55);

	private Button load = new Button("Load");
	private Button save = new Button("Save");
	private Button back = new Button("Back");

	// Setting up margins
	@Override
	public Insets getInsets() {
		Insets base = super.getInsets();
		return new Insets(base.top + MARGIN_CONTENT, base.left + MARGIN_CONTENT, base.bottom + MARGIN_CONTENT, base.right + MARGIN_CONTENT);
	}

	// Header - choosing what is displayed
	private void createHeader() {
		styleButton(showAirports);
		styleButton(showFlights);

		showAirports.addActionListener((e) -> {
			cards.show(cardPanel, AIRPORTS);
		});

		showFlights.addActionListener((e) -> {
			cards.show(cardPanel, FLIGHTS);
		});

		Panel header = new Panel();
		header.setBackground(BACKGROUND);
		header.add(showAirports);
		header.add(showFlights);

		add(header, BorderLayout.NORTH);
	}

	// Form on the left side
	private Panel createForm(String[] labels, TextField[] fields, Button addButton) {

		Panel content = new Panel(new GridLayout(0, 1, 0, MARGIN_BUTTON));
		content.setBackground(BACKGROUND);

		for (int i = 0; i < labels.length; i++) {
			Panel row = new Panel();
			row.setBackground(BACKGROUND);
			row.add(createLabel(labels[i]));
			row.add(fields[i]);
			content.add(row);
		}

		styleButton(addButton);

		Panel buttonPanel = new Panel();
		buttonPanel.setBackground(BACKGROUND);
		buttonPanel.add(addButton);
		content.add(buttonPanel);

		// NORTH - keeps the rows packed at the top instead of stretching them vertically
		Panel form = new Panel(new BorderLayout());
		form.setBackground(BACKGROUND);
		form.add(content, BorderLayout.NORTH);
		return form;
	}

	// Page - form on the left, table on the right
	private Panel createPage(Panel form, TextArea table) {
		table.setEditable(false);
		table.setFont(TABLE_FONT);

		Panel page = new Panel(new BorderLayout(MARGIN_CONTENT, 0));
		page.setBackground(BACKGROUND);
		page.add(form, BorderLayout.WEST);
		page.add(table, BorderLayout.CENTER);
		return page;
	}

	// Footer - file operations and the "Back" button
	private void createFooter() {
		styleButton(load);
		styleButton(save);
		styleButton(back);

		load.addActionListener((e) -> {
			loadFromFile();
		});
		save.addActionListener((e) -> {
			saveToFile();
		});
		back.addActionListener((e) -> {
			dispose();
			mainWindow.closePhase();
		});

		Panel fileButtons = new Panel();
		fileButtons.setBackground(BACKGROUND);
		fileButtons.add(load);
		fileButtons.add(save);

		Panel backButton = new Panel();
		backButton.setBackground(BACKGROUND);
		backButton.add(back);

		Panel footer = new Panel(new BorderLayout());
		footer.setBackground(BACKGROUND);
		footer.add(fileButtons, BorderLayout.WEST);
		footer.add(backButton, BorderLayout.EAST);

		add(footer, BorderLayout.SOUTH);
	}

	private void populateWindow() {

		setLayout(new BorderLayout(0, MARGIN_CONTENT));

		Panel airportForm = createForm(
				new String[] { "Name:", "Code:", "X:", "Y:" },
				new TextField[] { airportName, airportCode, airportX, airportY },
				addAirport);

		Panel flightForm = createForm(
				new String[] { "From:", "To:", "Departure:", "Duration:" },
				new TextField[] { flightFrom, flightTo, flightDeparture, flightDuration },
				addFlight);

		addAirport.addActionListener((e) ->{
			addAirportFromForm();
		});
		addFlight.addActionListener((e) -> {
			addFlightFromForm();
		});

		cardPanel.setBackground(BACKGROUND);
		cardPanel.add(createPage(airportForm, airportsTable), AIRPORTS);
		cardPanel.add(createPage(flightForm, flightsTable), FLIGHTS);

		createHeader();
		add(cardPanel, BorderLayout.CENTER);
		createFooter();

		refreshTables();
	}

	
	// ----------- ADDING FROM THE FORM -----------
	
	private void addAirportFromForm() {

		try {
			int x = parseCoordinate(airportX.getText());
			int y = parseCoordinate(airportY.getText());

			storage.addAirport(airportName.getText(), airportCode.getText().trim(), x, y);

			clearAirportForm();
			refreshTables();

		} catch (IllegalArgumentException e) {
			new MessageDialog(this, "Error", e.getMessage());
		}
	}

	private void addFlightFromForm() {

		try {
			int duration = parseDuration(flightDuration.getText());

			storage.addFlight(flightFrom.getText().trim(), flightTo.getText().trim(), flightDeparture.getText().trim(), duration);
			
			clearFlightForm();
			refreshTables();

		} catch (IllegalArgumentException e) {
			new MessageDialog(this, "Error", e.getMessage());
		}
	}

	// Converts the field text to an integer
	// An empty field or letters throw an exception
	private int parseCoordinate(String text) {
		String value = text.trim();
		if (value.isEmpty()) {
			throw new IllegalArgumentException("Coordinate must not be empty.");
		}
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Coordinate must be an integer.");
		}
	}

	// Converts the field text to an integer
	// An empty field or letters throw an exception
	private int parseDuration(String text) {
		String value = text.trim();
		if (value.isEmpty()) {
			throw new IllegalArgumentException("Flight duration must not be empty.");
		}
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Flight duration must be a whole number of minutes.");
		}
	}

	private void clearAirportForm() {
		airportName.setText("");
		airportCode.setText("");
		airportX.setText("");
		airportY.setText("");
	}

	private void clearFlightForm() {
		flightFrom.setText("");
		flightTo.setText("");
		flightDeparture.setText("");
		flightDuration.setText("");
	}

	// Refills both tables from the data storage
	private void refreshTables() {

		StringBuilder airportText = new StringBuilder();
		airportText.append(AIRPORT_HEADER).append("\n");
		for (Airport airport : storage.getAirports()) {
			airportText.append(airportRow(airport.getCode(), airport.getName(), airport.getX(), airport.getY()));
			airportText.append("\n");
		}
		airportsTable.setText(airportText.toString());

		StringBuilder flightText = new StringBuilder();
		flightText.append(FLIGHT_HEADER).append("\n");
		for (Flight flight : storage.getFlights()) {
			flightText.append(flightRow(flight.getFrom().getCode(), flight.getTo().getCode(), flight.getDepartureTime(), flight.getDuration()));
			flightText.append("\n");
		}
		flightsTable.setText(flightText.toString());
	}

	
	// ----------- ADDING FROM A FILE -----------

	
	private void loadFromFile() {

		File file = chooseFile(FileDialog.LOAD);
		if (file == null) {
			return;
		}

		try {
			FileHandler handler = FileHandler.chooseHandler(file);
			DataStorage loaded = handler.load(file);

			storage.replaceWith(loaded);
			refreshTables();

			new MessageDialog(this, "Loading",
					"Loaded: " + storage.getAirportCount() + " airports and "+ storage.getFlightCount() + " flights.");

		} catch (FileFormatException e) {
			// The file was opened, but its content is invalid
			new MessageDialog(this, "Error", e.getMessage());
		} catch (IOException e) {
			// The file cannot be opened or read
			new MessageDialog(this, "Error", "The file cannot be opened or read.");
		}
	}
	
	
	// ----------- SAVING TO A FILE -----------
	

	private void saveToFile() {

		if (storage.isEmpty()) {
			new MessageDialog(this, "Error", "There is no data to save. Add airports or flights first.");
			return;
		}

		File file = chooseFile(FileDialog.SAVE);
		if (file == null) {
			return;
		}

		try {
			FileHandler handler = FileHandler.chooseHandler(file);
			handler.save(file, storage);

			new MessageDialog(this, "Saving", "Data saved to file: " + file.getName());

		} catch (FileFormatException e) {
			// Unsupported extension
			new MessageDialog(this, "Error", e.getMessage());
		} catch (IOException e) {
			// The file cannot be saved
			new MessageDialog(this, "Error", "The file cannot be saved.");
		}
	}

	// Opens the system file chooser dialog.
	private File chooseFile(int mode) {

		FileDialog dialog = new FileDialog(this, mode == FileDialog.LOAD ? "Load file" : "Save file", mode);
		dialog.setVisible(true);

		String name = dialog.getFile();
		String directory = dialog.getDirectory();

		if (name == null || directory == null) {
			return null;
		}
		return new File(directory, name);
	}

	public PhaseAWindow(MainWindow mainWindow, DataStorage storage) {

		this.mainWindow = mainWindow;
		this.storage = storage;

		setTitle("Phase A");
		setLocation(400, 200);
		setResizable(true);
		setBackground(BACKGROUND);

		populateWindow();

		pack();

		// Resets the inactivity timer
		mainWindow.registerActivity(this);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
				mainWindow.closePhase();
			}
		});

		setVisible(true);
	}
}