package logic;

// Timer for blinking the selected airport
// Calls onBlink every interval milliseconds, which toggles the blink phase
// Pausing is handled with wait/notify - while work is false, the thread sleeps and doesn't use the CPU
public class BlinkingTimer extends Thread {

	private final long interval;
	private final Runnable onBlink;
	private boolean work = false;

	public BlinkingTimer(long intervalMs, Runnable onBlink) {
		this.interval = intervalMs;
		this.onBlink = onBlink;
	}

	@Override
	public void run() {
		try {
			while (!isInterrupted()) {

				synchronized (this) {
					while (!work) {
						wait();
					}
				}

				sleep(interval);
				onBlink.run();
			}
		} catch (InterruptedException e) {}
	}

	// Starts or resumes blinking
	public synchronized void go() {
		work = true;
		notify();
	}

	// Stops blinking
	public synchronized void pause() {
		work = false;
	}
}