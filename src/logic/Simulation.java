package logic;

import java.util.ArrayList;

import data.DataStorage;
import objects.Airport;
import objects.Flight;
import shapes.PlaneShape;

// Flight simulation logic
// Simulated time is measured in minutes since 00:00

// At most one plane departs from a single airport every ten minutes
// The actual departure time is calculated in prepareDepartures and stored in each flight
// The simulation only releases the planes
public class Simulation {

	// Refresh interval in milliseconds
	public static final int REFRESH_MS = 200;
	// How many simulated minutes pass per tick (1s = 10min => 200ms = 2min)
	private static final int SIM_MINUTES_PER_STEP = 2;
	// Length of a single departure slot
	private static final int SLOT = 10;
	// How many pixels away from the given position still count as a match (==)
	private static final double PLACE_EQUALITY = 3;

	private final DataStorage storage;

	// Current simulated time in minutes since 00:00
	private int simTime = 0;

	public Simulation(DataStorage storage) {
		this.storage = storage;
		prepareDepartures();
	}

	// Calculates the actual departure time for each flight (because of the 10-minute rule)
	private void prepareDepartures() {

		// Flights departing from this airport, sorted by time
		for (Airport airport : storage.getAirports()) {
			ArrayList<Flight> departing = new ArrayList<>();
			for (Flight flight : storage.getFlights()) {
				if (flight.getFrom().equals(airport)) {
					departing.add(flight);
				}
			}

			departing.sort((a, b) -> a.getDeparture() - b.getDeparture());

			// lastBlock - the last occupied slot, in 10-minute units
			int lastBlock = -1;
			for (Flight flight : departing) {
				int wantedBlock = flight.getDeparture() / SLOT;
				
				int actualDeparture;
				if (wantedBlock > lastBlock) {
					// Slot is free
					actualDeparture = flight.getDeparture();
					lastBlock = wantedBlock;
				} else {
					// Slot is taken - depart at the start of the next free one
					lastBlock = lastBlock + 1;
					actualDeparture = lastBlock * SLOT;
				}

				flight.setActualDeparture(actualDeparture);
			}
		}
	}

	// Advances the simulated time by one step
	public void step() {
		simTime += SIM_MINUTES_PER_STEP;
	}

	// Resets the time to the initial state
	public void reset() {
		simTime = 0;
	}

	public int getSimTime() {
		return simTime;
	}

	// Turns the plane around - reverses its direction back to the departure airport.
	public void turnAround(Flight flight) {
		flight.setReturning(true);
		flight.setTurnTime(simTime);
	}

	// Current X coordinate of the plane in the simulation
	public double currentX(Flight flight) {
		Airport from = flight.getFrom();
		Airport to = flight.getTo();
		return from.getX() + (to.getX() - from.getX()) * progressOf(flight);
	}

	// Current Y coordinate of the plane in the simulation
	public double currentY(Flight flight) {
		Airport from = flight.getFrom();
		Airport to = flight.getTo();
		return from.getY() + (to.getY() - from.getY()) * progressOf(flight);
	}

	// Fraction of the route the plane has covered: 0 - departure airport, 1 - arrival airport
	// For a returning plane, the value decreases back toward 0
	private double progressOf(Flight flight) {
		int departure = flight.getActualDeparture();
		if (flight.isReturning()) {
			double pTurn = (double) (flight.getTurnTime() - departure) / flight.getDuration();
			double pNow = (double) (simTime - departure) / flight.getDuration();
			return 2 * pTurn - pNow;
		}
		return (double) (simTime - departure) / flight.getDuration();
	}

	// Builds the list of planes currently in the air, with their computed geographic position
	public ArrayList<PlaneShape> getPlanes() {

		ArrayList<PlaneShape> planes = new ArrayList<>();

		for (Flight flight : storage.getFlights()) {

			int departure = flight.getActualDeparture();
			int arrival = departure + flight.getDuration();

			if (simTime >= departure && simTime < arrival) {

				if (flight.isReturning() && progressOf(flight) <= 0) {
					continue;
				}
				
				planes.add(new PlaneShape(currentX(flight), currentY(flight)));
			}
		}

		return planes;
	}
	
	// Finds the plane at the given geographic position
	public Flight findFlightAt(double geoX, double geoY) {
	    for (Flight flight : storage.getFlights()) {
	        int departure = flight.getActualDeparture();
	        int arrival = departure + flight.getDuration();
	        if (simTime >= departure && simTime < arrival && !flight.isReturning()) {
	            double dx = currentX(flight) - geoX;
	            double dy = currentY(flight) - geoY;
	            if (Math.sqrt(dx * dx + dy * dy) < PLACE_EQUALITY) {
	                return flight;
	            }
	        }
	    }
	    return null;
	}

}