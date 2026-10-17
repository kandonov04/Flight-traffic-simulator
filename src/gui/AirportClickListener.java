package gui;

import objects.Airport;

// Listens for clicks on an airport on the map
// MapCanvas reports which airport was clicked => phase B selects the airport, phase C currently does nothing (open for extension)
public interface AirportClickListener {
	void onAirportClicked(Airport airport);
}