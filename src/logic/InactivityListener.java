package logic;

// Listens to the inactivity timer
// Through this interface the timer communicates with the window without knowing about program shutdown itself
public interface InactivityListener {

	// Called every second with the number of remaining seconds
	void onTick(int remaining);

	// Called when the time runs out
	void onExpire();
}