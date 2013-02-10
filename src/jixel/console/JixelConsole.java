package jixel.console;

import java.util.ArrayList;
import java.util.List;

import jixel.stage.JixelGame;


public class JixelConsole implements Runnable {

	private Thread thread;
	private boolean isRunning = false;
	private List<String> messageList = new ArrayList<String>();
	private int LOG_HEIGHT;
	private int MAX_WIDTH;

	public JixelConsole() {
		thread = new Thread(this, "Console");
		thread.start();
		
		LOG_HEIGHT = (JixelGame.getScreen().getHeight() - 3 * (JixelGame.getScreen().getTileSize())) / 24;
		int tileSize = JixelGame.getScreen().getTileSize();
		int width = JixelGame.getScreen().getWidth();
		MAX_WIDTH = width - 3*tileSize;
	}

	public void print(String message) {
		StringBuilder output = new StringBuilder();
		String[] words = message.split(" ");
		for(int i=0; i<words.length; i++){
			if((words[i].length() + output.length()) * 8 > MAX_WIDTH){
				break;
			}
			output.append(" " + words[i]);
		}
		addToList(output.toString());
		if(message.length() > output.length()){
			if(output.length() > 0){
				print(message.substring(output.length()));
			}else{
				addToList(words[0]);
				print(message.substring(words[0].length()));
			}
		}
	}
	
	private void addToList(String message){
		int size = messageList.size();
		if (size - 1 == LOG_HEIGHT) {
			messageList.remove(size - 1);
		}
		messageList.add(0, message);
	}

	public List<String> getMessageList() {
		return messageList;
	}

	public void cInput(String[] input) {
		String answer = "Unknown command.";

		if(input[0].equals("run") && input.length >= 2){
			String[] path = input[1].split("\\.");
			if(path.length == 3){
				String className = path[0]+"."+path[1];
				if(JixelGame.getVM().containsClass(className)){
					Object ans;
					if(input.length > 2){
						Object[] parameters = new Object[input.length-2];
						for(int i=2; i<input.length; i++){
							parameters[i-2] = input[i];
						}
						ans = JixelGame.getVM().runMethod(className, path[2], parameters);
					}else{
						ans = JixelGame.getVM().runMethod(className, path[2], new Object());
					}
					if(ans == null){
						answer = null;
					}else{
						answer = ans.toString();
					}
				}else{
					answer = "The class " + className + " was not added to the Variable Manager";
				}
			}else{
				answer = "Method path must follow the format package.class.method";
			}
		}else if (input.length == 1) {
			if(JixelGame.getVM().containsVar(input[0])){
				answer = String.valueOf((JixelGame.getVM().getValue(input[0])));
				answer = "Value of " + input[0] + ": " + answer;
			}else{
				answer = "No such variable with the name " + input[0] + " exists.";
			}
		} else if (input.length == 2) {
			answer = "Command invalid or not used properly";
			if (input[0].equals("save")) {
				if (JixelGame.getVM().save(input[1])) {
					answer = "Profile saved to " + input[1];
				} else {
					answer = "Save failed.";
				}
			} else if (input[0].equals("load")) {
				if (JixelGame.getVM().load(input[1])) {
					answer = "Profile loaded from " + input[1];
				} else {
					answer = "Loading failed.";
				}
			}
			if (input[0].equals("editor")) {
				if (input[1].equals("start")) {
					setState(false);
					answer = "Editor launched";
				}else if(input[1].equals("quit")){
					setState(false);
					answer = "Editor exited";
				}
			}
		} else if (input.length == 3) {
			if(input[0].equals("set")){
				if (JixelGame.getVM().containsVar(input[1])) {
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
		return JixelGame.getKeyInput().getConsoleMsg();
	}
	public void startConsoleMsg(int maxLength){
		JixelGame.getKeyInput().startConsoleMsg(maxLength);
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
					JixelGame.setPaused(false);
					thread.wait();
				} catch (InterruptedException e) {
					print("Console thread interrupted");
					e.printStackTrace();
				}
			}
			while (isRunning) {
				JixelGame.setPaused(true);
				if (!JixelGame.getKeyInput().isReading()) {
					String msg = getConsoleMsg();
					if (!msg.isEmpty()) {
						String[] commands = msg.split(" ");
						cInput(commands);
					}
					startConsoleMsg((MAX_WIDTH>>3)-1);
				}
			}
		}
	}

}