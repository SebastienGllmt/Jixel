package jixel.stage;

public class JixelTimer {

	private double fpsNS;
	private long timer;
	private long lastTime;
	private long now;
	private double deltaFps;
	private int frames, lastFrameCount;
	
	private final long BOOT_TIME;
	private int fps;
	
	public JixelTimer(){
		BOOT_TIME = System.currentTimeMillis();
	}
	
	/**
	 * Restarts the timer with a given fps
	 * @param fps
	 */
	private void startTimer(int fps) {	
		fpsNS = 1000000000.0 / fps;
		timer = System.currentTimeMillis();
		lastTime = System.nanoTime();
		deltaFps = 0;
		frames = 0;
		now = System.nanoTime();
	}

	/**
	 * Updates the timer
	 */
	public void updateTime() {
		if (System.currentTimeMillis() - timer > 1000) {
			timer += 1000;
			lastFrameCount = frames;
			frames = 0;
		}
		now = System.nanoTime();
		deltaFps += (now - lastTime) / fpsNS;
		lastTime = now;
	}

	/**
	 * @return whether or not its time to update
	 */
	public boolean timeForFrame() {
		if (deltaFps >= 1) {
			frames++;
			deltaFps=0;
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * @return The last fps count
	 */
	public int getLastFrameCount(){
		return lastFrameCount;
	}
	
	/**
	 * @return The time in milliseconds the game was booted
	 */
	public long getBootTime(){
		return BOOT_TIME;
	}
	
	/**
	 * @return Gets the fps the game should run at
	 */
	public int getFPS(){
		return fps;
	}
	/**
	 * Sets the game to run at a new fps
	 * @param fps
	 */
	public void setFPS(int fps){
		startTimer(fps);
	}
}
