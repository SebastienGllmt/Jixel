package console;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import stage.JixelGame;

public class JixelConsole implements Runnable {

	private Thread thread;
	private boolean isRunning = false;
	private JixelGame game;
	private List<String> messageList = new ArrayList<String>();
	private int logHeight;

	public JixelConsole(JixelGame game) {
		this.game = game;
		thread = new Thread(this, "Console");
		thread.start();
		this.logHeight = (game.getHeight() - 3 * (game.tileSize)) / 24;
	}

	public void print(String message) {
		int size = messageList.size();
		if (size - 1 == logHeight) {
			messageList.remove(size - 1);
		}
		messageList.add(0, message);
	}

	public List<String> getMessageList() {
		return messageList;
	}

	public void cInput(String[] input) {
		String answer = "Unknown command.";

		if (input.length == 1) {
			answer = String.valueOf((JixelGame.getVM().getValue(input[0])));
			if (!answer.equals("null")) {
				print("Value of " + input[0] + ": " + answer);
			}
		} else if (input.length == 2) {
			answer = "Failed to get";
			if (input[0].equals("save")) {
				if (JixelGame.getVM().save(input[1])) {
					answer = "Profile saved.";
				} else {
					answer = "Save failed.";
				}
			} else if (input[0].equals("load")) {
				if (JixelGame.getVM().load(input[1])) {
					answer = "Profile loaded.";
				} else {
					answer = "Loading failed.";
				}
			}
			if (input[0].equals("stop")) {
				if (input[1].equals("console")) {
					setState(false);
					answer = "Console stopped";
				}
			}
			print(answer);
		} else if (input.length == 3) {
			if (JixelGame.getVM().contains(input[1])) {
				Class<?> clazz = JixelGame.getVM().getValue(input[1]).getClass();
				if (clazz.equals(String.class)) {
					JixelGame.getVM().setValue(input[1], input[2]);
					answer = "Value of " + input[1] + " set to " + input[2];
				} else {
					Method[] methods = clazz.getMethods();
					for (Method method : methods) {
						if (method.getName().startsWith("parse")) {
							try {
								JixelGame.getVM().setValue(input[1], method.invoke(clazz, input[2]));
								answer = "Value of " + input[1] + " set to " + input[2];
							} catch (IllegalArgumentException e) {
								answer = "Illegal Argument Exception: Failed to set value of " + input[1] + " to " + input[2];
							} catch (IllegalAccessException e) {
								answer = "Illegal Access Exception: Failed to set value of " + input[1] + " to " + input[2];
							} catch (InvocationTargetException e) {
								answer = "Invocation Target Exception: Failed to set value of " + input[1] + " to " + input[2];
							}
							break;
						}
					}
				}

			} else {
				answer = "Value failed to set";
			}
			print(answer);
		}
		return;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setState(boolean state) {
		isRunning = state;
		if(isRunning){
			synchronized(thread){
				thread.notifyAll();
			}
		}
	}

	@Override
	public void run() {
		while(true){
			synchronized(thread){
				try {
					thread.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			while (isRunning) {
				if (!game.getInput().isReading()) {
					String msg = game.getInput().getConsoleMsg();
					if (!msg.isEmpty()) {
						String[] commands = msg.split(" ");
						cInput(commands);
					}
					int enterKey = JixelGame.getVM().getValue("Jixel_enterKey");
					game.getInput().startConsoleMsg(enterKey, 32);
				}
			}
		}
	}

}