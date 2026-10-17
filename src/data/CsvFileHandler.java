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

// Reads and writes data in CSV format
public class CsvFileHandler implements FileHandler {

	private static final String AIRPORTS_SECTION = "AIRPORTS";
	private static final String FLIGHTS_SECTION = "FLIGHTS";
	private static final String AIRPORTS_HEADER = "CODE,NAME,X,Y";
	private static final String FLIGHTS_HEADER = "FROM,TO,DEPARTURE,DURATION";
	private static final int COLUMNS = 4;

	// Reading
	@Override
	public DataStorage load(File file) throws IOException, FileFormatException {

		DataStorage storage = new DataStorage();
		BufferedReader reader = new BufferedReader(new FileReader(file));

		try {
			int section = 0;
			boolean header = false;
			String line;

			while ((line = reader.readLine()) != null) {
				line = line.trim();
				
				if (line.isEmpty()) {
					continue;
				}

				// First we check the section name
				if (line.startsWith("#")) {
					section = checkSection(line);
					header = true;
					continue;
				}

				if (section == 0) {
					throw new FileFormatException("Data appears before a section marker. The file must start with #.");
				}

				// Then its header
				if (header) {
					checkHeader(line, section);
					header = false;
					continue;
				}

				// And finally we read the data.
				if (section == 1) {
					readAirport(line, storage);
				} else {
					readFlight(line, storage);
				}
			}
		} finally {
			reader.close();
		}

		return storage;
	}

	// Validates the section
	private int checkSection(String line) throws FileFormatException {

		String name = line.substring(1).trim().toUpperCase();

		if (name.equals(AIRPORTS_SECTION)) {
			return 1;
		}
		if (name.equals(FLIGHTS_SECTION)) {
			return 2;
		}
		throw new FileFormatException("Unknown section, allowed sections are #" + AIRPORTS_SECTION + " and #" + FLIGHTS_SECTION + ".");
	}

	// Validates the header
	private void checkHeader(String line, int section) throws FileFormatException {

		String format;
		if(section == 1) {
			format = AIRPORTS_HEADER;
		} else {
			format = FLIGHTS_HEADER;
		}

		String text = line.replace(" ", "").toUpperCase();

		if (!text.equals(format)) {
			throw new FileFormatException("The header is not in the expected format. Check the header format or choose another file.");
		}
	}

	private void readAirport(String line, DataStorage storage) throws FileFormatException {

		String[] parts = line.split(",");
		if (parts.length != COLUMNS) {
			throw new FileFormatException("Expected " + COLUMNS + " columns, found " + parts.length + ".");
		}
		for (int i = 0; i < parts.length; i++) {
			parts[i] = parts[i].trim();
		}

		try {
			int x = Integer.parseInt(parts[2]);
			int y = Integer.parseInt(parts[3]);
			storage.addAirport(parts[1], parts[0], x, y);
		} catch (NumberFormatException e) {
			throw new FileFormatException("Coordinates must be integers.");
		} catch (IllegalArgumentException e) {
			throw new FileFormatException(e.getMessage());
		}
	}

	private void readFlight(String line, DataStorage storage) throws FileFormatException {

		String[] parts = line.split(",");
		if (parts.length != COLUMNS) {
			throw new FileFormatException("Expected " + COLUMNS + " columns, found " + parts.length + ".");
		}
		for (int i = 0; i < parts.length; i++) {
			parts[i] = parts[i].trim();
		}

		int duration;
		try {
			duration = Integer.parseInt(parts[3]);
			storage.addFlight(parts[0], parts[1], parts[2], duration);
		} catch (NumberFormatException e) {
			throw new FileFormatException("Flight duration must be an integer.");
		} catch (IllegalArgumentException e) {
			throw new FileFormatException(e.getMessage());
		}
	}

	// Writing
	@Override
	public void save(File file, DataStorage storage) throws IOException {

		BufferedWriter writer = new BufferedWriter(new FileWriter(file));

		try {
			writer.write("# " + AIRPORTS_SECTION);
			writer.newLine();
			writer.write(AIRPORTS_HEADER);
			writer.newLine();

			for (Airport airport : storage.getAirports()) {
				writer.write(airport.getCode() + "," + airport.getName() + "," + airport.getX() + "," + airport.getY());
				writer.newLine();
			}

			writer.write("# " + FLIGHTS_SECTION);
			writer.newLine();
			writer.write(FLIGHTS_HEADER);
			writer.newLine();

			for (Flight flight : storage.getFlights()) {
				writer.write(flight.getFrom().getCode() + "," + flight.getTo().getCode() + "," + flight.getDepartureTime() + "," + flight.getDuration());
				writer.newLine();
			}
		} finally {
			writer.close();
		}
	}
}