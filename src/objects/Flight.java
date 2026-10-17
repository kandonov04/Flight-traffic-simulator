package objects;

public class Flight {

	private static final int MINUTES_IN_HOUR = 60;
	private static final int HOURS_IN_DAY = 24;
	private static final int MINUTES_IN_DAY = 24 * MINUTES_IN_HOUR;
	private static final String REGEX = "\\d{1,2}:\\d{2}";

	private final Airport from;
	private final Airport to;

	// Departure time in minutes since midnight - simplifies calculations in the simulation
	private final int departure;

	// Actual departure time in the simulation
	// Initially equal to the scheduled time; phase C changes it as needed
	private int actualDeparture;

	// Whether the plane is currently turning around and returning to the departure airport
	private boolean returning = false;

	// Time when the plane turned around - used to calculate its position on the way back
	private int turnTime = 0;

	// Flight duration in minutes
	private final int duration;

	public Flight(Airport from, Airport to, String departure, int duration) {

		if (from == null || to == null) {
			throw new IllegalArgumentException("Departure and arrival airports must be selected.");
		}
		if (from.equals(to)) {
			throw new IllegalArgumentException("Departure and arrival airports cannot be the same.");
		}
		if (duration <= 0) {
			throw new IllegalArgumentException("Flight duration must be a positive number of minutes (entered: " + duration + ").");
		}

		this.from = from;
		this.to = to;
		this.departure = parseTime(departure);
		this.actualDeparture = this.departure;
		this.duration = duration;
	}

	// Converts an HH:MM string into the number of minutes since midnight.
	public static int parseTime(String time) {

		if (time == null || !time.trim().matches(REGEX)) {
			throw new IllegalArgumentException("Departure time must be in HH:MM format.");
		}

		String[] parts = time.trim().split(":");
		int hours = Integer.parseInt(parts[0]);
		int minutes = Integer.parseInt(parts[1]);

		if (hours >= HOURS_IN_DAY) {
			throw new IllegalArgumentException("Hour must be between 0 and 23 (entered: " + hours + ").");
		}
		if (minutes >= MINUTES_IN_HOUR) {
			throw new IllegalArgumentException("Minute must be between 0 and 59 (entered: " + minutes + ").");
		}

		return hours * MINUTES_IN_HOUR + minutes;
	}

	// Converts a number of minutes into an HH:MM string.
	public static String formatTime(int minutes) {
		int m = minutes % MINUTES_IN_DAY;
		return String.format("%02d:%02d", m / MINUTES_IN_HOUR, m % MINUTES_IN_HOUR);
	}

	public Airport getFrom() {
		return from;
	}

	public Airport getTo() {
		return to;
	}

	// Departure time in minutes since midnight.
	public int getDeparture() {
		return departure;
	}
	
	public int getActualDeparture() {
		return actualDeparture;
	}

	public void setActualDeparture(int actualDeparture) {
		this.actualDeparture = actualDeparture;
	}

	public boolean isReturning() {
		return returning;
	}

	public void setReturning(boolean returning) {
		this.returning = returning;
	}

	public int getTurnTime() {
		return turnTime;
	}

	public void setTurnTime(int turnTime) {
		this.turnTime = turnTime;
	}

	// Departure time as HH:MM, for display and writing to a file.
	public String getDepartureTime() {
		return formatTime(departure);
	}

	public int getDuration() {
		return duration;
	}

	@Override
	public String toString() {
		return from.getCode() + " -> " + to.getCode() + " " + getDepartureTime() + " (" + duration + " min)";
	}
}