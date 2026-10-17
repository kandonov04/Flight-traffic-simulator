package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import exceptions.FileFormatException;
import objects.Airport;
import objects.Flight;

// Reads and writes data in JSON format
public class JsonFileHandler implements FileHandler {

	private static final int FIELDS = 4;

	// Reading
	@Override
	public DataStorage load(File file) throws IOException, FileFormatException {

		String text = readFile(file);

		checkFormat(text);

		DataStorage storage = new DataStorage();

		readAirports(text, storage);
		readFlights(text, storage);

		return storage;
	}
	
	// Loads the whole file into a single string
	private String readFile(File file) throws IOException {

		StringBuilder text = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(file));

		try {
			String line;
			while ((line = reader.readLine()) != null) {
				text.append(line);
			}
		} finally {
			reader.close();
		}

		return text.toString();
	}

	// Validates the whole format: all { } [ ] brackets and quotes
	private void checkFormat(String text) throws FileFormatException {

		int curly = 0;
		int square = 0;
		boolean inString = false;

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);

			if (c == '"') {
				inString = !inString;
				continue;
			}
			if (inString) {
				continue;
			}

			if (c == '{') {
				curly++;
			} else if (c == '}') {
				curly--;
			} else if (c == '[') {
				square++;
			} else if (c == ']') {
				square--;
			}

			if (curly < 0 || square < 0) {
				throw new FileFormatException("Invalid bracket order in the file.");
			}
		}

		if (inString) {
			throw new FileFormatException("Unclosed quote in the file.");
		}
		if (curly != 0 || square != 0) {
			throw new FileFormatException("Brackets in the file are not properly closed.");
		}
	}

	// Reads all airports from the airports array
	private void readAirports(String text, DataStorage storage) throws FileFormatException {

		String block = getArray(text, "airports");

		for (String object : splitObjects(block)) {

			checkFieldCount(object);

			String code = getString(object, "code");
			String name = getString(object, "name");
			int x = getInt(object, "x");
			int y = getInt(object, "y");

			try {
				storage.addAirport(name, code, x, y);
			} catch (IllegalArgumentException e) {
				throw new FileFormatException(e.getMessage());
			}
		}
	}

	// Reads all flights from the flights array
	private void readFlights(String text, DataStorage storage) throws FileFormatException {

		String block = getArray(text, "flights");

		for (String object : splitObjects(block)) {

			checkFieldCount(object);

			String from = getString(object, "from");
			String to = getString(object, "to");
			String departure = getString(object, "departure");
			int duration = getInt(object, "duration");

			try {
				storage.addFlight(from, to, departure, duration);
			} catch (IllegalArgumentException e) {
				throw new FileFormatException(e.getMessage());
			}
		}
	}

	// Returns the content between [ ] for the given field
	// Returns an empty string if the field doesn't exist
	private String getArray(String text, String field) {

		int start = text.indexOf("\"" + field + "\"");
		if (start < 0) {
			return "";
		}

		int open = text.indexOf('[', start);
		int close = text.indexOf(']', start);
		return text.substring(open + 1, close);
	}

	// Checks that the object has exactly FIELDS fields
	private void checkFieldCount(String object) throws FileFormatException {

		int count = 0;
		boolean inString = false;

		for (int i = 0; i < object.length(); i++) {
			char c = object.charAt(i);
			if (c == '"') {
				inString = !inString;
			} else if (c == ':' && !inString) {
				count++;
			}
		}

		if (count != FIELDS) {
			throw new FileFormatException("Expected " + FIELDS + " fields in the object, found " + count + ".");
		}
	}

	// Splits the array content into individual { } objects
	private ArrayList<String> splitObjects(String block) {

		ArrayList<String> objects = new ArrayList<>();

		int position = 0;
		while (true) {
			int open = block.indexOf('{', position);
			if (open < 0) {
				break;
			}
			int close = block.indexOf('}', open);
			objects.add(block.substring(open + 1, close));
			position = close + 1;
		}
		return objects;
	}

	// Reads a text field of the form field : value
	private String getString(String object, String field) throws FileFormatException {

		int valueStart = findValue(object, field);

		int open = object.indexOf('"', valueStart);
		int close = object.indexOf('"', open + 1);
		return object.substring(open + 1, close);
	}

	// Reads an integer field of the form field : value
	private int getInt(String object, String field) throws FileFormatException {

		int valueStart = findValue(object, field);
		int end = valueStart;
		while (end < object.length() && object.charAt(end) != ',') {
			end++;
		}

		String value = object.substring(valueStart, end).trim();

		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new FileFormatException("Field " + field + " must be an integer.");
		}
	}

	// Finds the position after the colon for the given field
	private int findValue(String object, String field) throws FileFormatException {

		String key = "\"" + field + "\"";
		int keyPos = object.indexOf(key);
		if (keyPos < 0) {
			throw new FileFormatException("Object does not contain the field " + field + ".");
		}

		int colon = object.indexOf(':', keyPos);
		if (colon < 0) {
			throw new FileFormatException("Field " + field + " has no value.");
		}

		return colon + 1;
	}

	// Writing
	@Override
	public void save(File file, DataStorage storage) throws IOException {

		BufferedWriter writer = new BufferedWriter(new FileWriter(file));

		try {
			writer.write("{");
			writer.newLine();

			writer.write("\"airports\":[");
			writer.newLine();

			ArrayList<Airport> airports = storage.getAirports();
			for (int i = 0; i < airports.size(); i++) {
				Airport airport = airports.get(i);
				writer.write("{\"code\":\"" + airport.getCode() + "\",\"name\":\"" + airport.getName() + "\",\"x\":" + airport.getX() + ",\"y\":" + airport.getY() + "}");
				if (i < airports.size() - 1) {
					writer.write(",");
				}
				writer.newLine();
			}

			writer.write("],");
			writer.newLine();

			writer.write("\"flights\":[");
			writer.newLine();

			ArrayList<Flight> flights = storage.getFlights();
			for (int i = 0; i < flights.size(); i++) {
				Flight flight = flights.get(i);
				writer.write("{\"from\":\"" + flight.getFrom().getCode() + "\",\"to\":\"" + flight.getTo().getCode() + "\",\"departure\":\"" + flight.getDepartureTime() + "\",\"duration\":" + flight.getDuration() + "}");
				if (i < flights.size() - 1) {
					writer.write(",");
				}
				writer.newLine();
			}

			writer.write("]");
			writer.newLine();

			writer.write("}");
			writer.newLine();
		} finally {
			writer.close();
		}
	}
}