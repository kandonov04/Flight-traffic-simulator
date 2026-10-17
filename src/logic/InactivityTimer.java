package logic;

// Inactivity timer
// Counts down from TOTAL_SECONDS to 0, and every user action resets it via reset()
// When WARNING_SECONDS remain, the window shows a warning through onTick, and when it reaches zero, onExpire closes the program
// Pausing is handled with the paused flag
public class InactivityTimer extends Thread {

	public static final int TOTAL_SECONDS = 60;
	public static final int WARNING_SECONDS = 5;

	private final InactivityListener listener;

	private volatile int remaining = TOTAL_SECONDS;
	private volatile boolean paused = false;

	public InactivityTimer(InactivityListener listener) {
		this.listener = listener;
	}

	@Override
	public void run() {
		try {
			while (!isInterrupted()) {
				sleep(1000);

				if (paused) {
					continue;
				}

				remaining--;
				listener.onTick(remaining);

				if (remaining <= 0) {
					listener.onExpire();
					break;
				}
			}
		} catch (InterruptedException e) {}
	}

	// Resets the countdown to the start
	public void reset() {
		remaining = TOTAL_SECONDS;
	}

	// Temporarily pauses the countdown
	public void pause() {
		paused = true;
	}

	// Resumes the countdown
	public void unpause() {
		paused = false;
	}
}