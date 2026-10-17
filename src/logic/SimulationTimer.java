package logic;

// Simulation timer
// Calls onTick every interval milliseconds
// Pausing is handled with wait/notify - while work is false, the thread sleeps and doesn't use the CPU
public class SimulationTimer extends Thread {

	private final long interval;
	private final Runnable onTick;
	private boolean work = false;

	public SimulationTimer(long intervalMs, Runnable onTick) {
		this.interval = intervalMs;
		this.onTick = onTick;
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
				onTick.run();
			}
		} catch (InterruptedException e) {}
	}

	// Starts or resumes ticking
	public synchronized void go() {
		work = true;
		notify();
	}

	// Stops ticking
	public synchronized void pause() {
		work = false;
	}
}