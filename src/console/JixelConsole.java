package console;

import java.util.Scanner;

import stage.JixelGame;

public class JixelConsole implements Runnable {

	private Scanner scan = new Scanner(System.in);
	private Thread thread;
	private boolean isRunning = false;
	private boolean isActive = true;
	private JixelGame game;
	
	public JixelConsole(JixelGame game){
		this.game = game;
		thread = new Thread(this, "Console");
		thread.start();
	}

	public void print(String message) {
		System.out.println(message);
	}

	public void cInput(String[] input) {
		String answer = "Unknown command.";

		if (input.length == 1) {
			answer = String.valueOf((game.getVM().getValue(input[0])));
			if(!answer.equals("null")){
				print(answer);
			}
		} else if (input.length == 2) {
			answer = "Failed to get";
			if (input[0].equals("save")) {
				if (game.getVM().save(input[1])) {
					answer = "Profile saved.";
				} else {
					answer = "Save failed.";
				}
			} else if (input[0].equals("load")) {
				if (game.getVM().load(input[1])) {
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
			String previousValue = String.valueOf(game.getVM().getValue(input[1]));
			if (input[0].equals("boolean")) {
				game.getVM().setValue(input[1], Boolean.parseBoolean(input[2]));
			} else if (input[0].equals("byte")) {
				game.getVM().setValue(input[1], Byte.parseByte(input[2]));
			} else if (input[0].equals("char")) {
				game.getVM().setValue(input[1], input[2].charAt(0));
			} else if (input[0].equals("short")) {
				game.getVM().setValue(input[1], Short.parseShort(input[2]));
			} else if (input[0].equals("int")) {
				game.getVM().setValue(input[1], Integer.parseInt(input[2]));
			} else if (input[0].equals("long")) {
				game.getVM().setValue(input[1], Long.parseLong(input[2]));
			} else if (input[0].equals("double")) {
				game.getVM().setValue(input[1], Double.parseDouble(input[2]));
			} else if (input[0].equals("float")) {
				game.getVM().setValue(input[1], Float.parseFloat(input[2]));
			} else if (input[0].equals("String")) {
				game.getVM().setValue(input[1], input[2]);
			}
			String newValue = String.valueOf(game.getVM().getValue(input[1]));
			if(newValue.equals(previousValue)){
				answer = "Failed to set";
			}else{
				answer = "Value set";
			}
			print(answer);
		}
		return;
	}
	
	public boolean isRunning(){
		return isRunning;
	}
	
	public void setState(boolean state){
		isRunning = state;
	}
	public void shutdown(){
		isActive=false;
	}

	@Override
	public void run() {
		while(isActive){
			game.keys().updateKeyboard();
			isRunning = game.keys().consoleKey;
			while (isRunning) {
				/*
				if (scan.hasNextLine()) {
					String[] commands = scan.nextLine().split(" ");
					cInput(commands);
				}
				*/
			}
			
		}
	}

}