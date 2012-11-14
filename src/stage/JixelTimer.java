package stage;

public class JixelTimer {

	private double fpsNS;
	private double upsNS;
	private long timer;
	private long lastTime;
	private long now;
	private double deltaUps;
	private double deltaFps;
	private int frames;
	private int updates;
	
	private final String GAME_TITLE;
	
	public JixelTimer(String gameTitle){
		GAME_TITLE = gameTitle;
	}
	
	public void startTimer(double fps, double ups) {
		fpsNS = 1000000000.0 / fps;
		upsNS = 1000000000.0 / ups;

		timer = System.currentTimeMillis();
		lastTime = System.nanoTime();

		deltaUps = 0;
		deltaFps = 0;
		frames = 0;
		updates = 0;

		now = System.nanoTime();
	}

	public void updateTime() {

		if (System.currentTimeMillis() - timer > 1000) {
			timer += 1000;
			JixelGame.getScreen().setTitle(GAME_TITLE + " Ups: " + updates + " , Fps: " + frames);
			updates = 0;
			frames = 0;
		}

		now = System.nanoTime();
		deltaFps += (now - lastTime) / fpsNS;
		deltaUps += (now - lastTime) / upsNS;
		lastTime = now;
	}

	public boolean timeForFrame() {
		if (deltaFps >= 1) {
			frames++;
			deltaFps--;
			return true;
		} else {
			return false;
		}
	}
	public boolean timeForUpdate() {
		if (deltaUps >= 1) {
			updates++;
			deltaUps--;
			return true;
		} else {
			return false;
		}
	}
}
