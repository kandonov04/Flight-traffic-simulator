package data;

import java.util.ArrayList;

import objects.Airport;
import objects.Flight;


// Stores all airports and flights
// RULES: an airport code must be unique, and a flight must connect two existing airports
public class DataStorage {

	private final ArrayList<Airport> airports = new ArrayList<>();
	private final ArrayList<Flight> flights = new ArrayList<>();

	
	// --------------- AIRPORTS ---------------

	
	// Creates and adds an airport
	// Throws an exception if the code already exists
	public void addAirport(String name, String code, int x, int y) {

		Airport airport = new Airport(name, code, x, y);

		if (airports.contains(airport)) {
			throw new IllegalArgumentException("Airport with code " + airport.getCode() + " already exists in the system.");
		}
		
		airports.add(airport);
	}

	
	// Finds an airport by code
	public Airport findAirport(String code) {

		if (code == null) {
			return null;
		}
		
		for (Airport airport : airports) {
			if (airport.getCode().equals(code)) {
				return airport;
			}
		}
		return null;
	}

	
	// Removes an airport and all flights that use it
	public void removeAirport(Airport airport) {

		if (airport == null) {
			return;
		}

		// The loop goes from size - 1 down to 0 because of removal!
		for (int i = flights.size() - 1; i >= 0; i--) {
			Flight flight = flights.get(i);
			if (flight.getFrom().equals(airport) || flight.getTo().equals(airport)) {
				flights.remove(i);
			}
		}
		airports.remove(airport);
	}

	
	// --------------- FLIGHTS ---------------

	
	// Creates and adds a flight based on airport codes
	public void addFlight(String fromCode, String toCode, String departure, int duration) {

		Airport from = findAirport(fromCode);
		if (from == null) {
			throw new IllegalArgumentException("Airport with code " + fromCode + " does not exist. Enter an existing airport.");
		}

		Airport to = findAirport(toCode);
		if (to == null) {
			throw new IllegalArgumentException("Airport with code " + toCode + " does not exist. Enter an existing airport.");
		}

		Flight flight = new Flight(from, to, departure, duration);
		flights.add(flight);
	}

	// Removes a flight
	public void removeFlight(Flight flight) {
		flights.remove(flight);
	}


	public ArrayList<Airport> getAirports() {
		return airports;
	}

	public ArrayList<Flight> getFlights() {
		return flights;
	}

	public int getAirportCount() {
		return airports.size();
	}

	public int getFlightCount() {
		return flights.size();
	}

	public boolean isEmpty() {
		return airports.isEmpty() && flights.isEmpty();
	}

	public void clear() {
		airports.clear();
		flights.clear();
	}

	// Takes over the content of another data storage
	// Used after loading a file, since reading returns a new data storage
	public void replaceWith(DataStorage other) {

		if (other == null || other == this) {
			return;
		}

		airports.clear();
		flights.clear();
		airports.addAll(other.airports);
		flights.addAll(other.flights);
	}
}