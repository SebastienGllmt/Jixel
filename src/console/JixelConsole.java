package console;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import stage.JixelGame;

public class JixelConsole implements Runnable {

	private Thread thread;
	private boolean isRunning = false;
	private List<String> messageList = new ArrayList<String>();
	private int logHeight;

	public JixelConsole() {
		thread = new Thread(this, "Console");
		thread.start();
		this.logHeight = (JixelGame.getScreen().getHeight() - 3 * (JixelGame.getScreen().getTileSize())) / 24;
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
				answer = "Value of " + input[0] + ": " + answer;
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
		} else if (input.length == 3) {
			if(input[0].equals("set")){
				if (JixelGame.getVM().contains(input[1])) {
					if(JixelGame.getVM().setValue(input[1], input[2])){
						answer = "Value of " + input[1] + " set to " + input[2];
					}else{
						answer = null;
					}
				} else {
					answer = "Failed to set " + input[1] + " to " + input[2];
				}
			}
		}
		if(answer != null){
			print(answer);
		}
		return;
	}

	public String getConsoleMsg(){
		return JixelGame.getInput().getConsoleMsg();
	}
	public void startConsoleMsg(int maxLength){
		JixelGame.getInput().startConsoleMsg(maxLength);
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
				if (!JixelGame.getInput().isReading()) {
					String msg = getConsoleMsg();
					if (!msg.isEmpty()) {
						String[] commands = msg.split(" ");
						cInput(commands);
					}
					startConsoleMsg(32);
				}
			}
		}
	}

}