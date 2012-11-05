package console;

import java.util.Scanner;

public class JixelConsole implements Runnable {

	private Scanner scan = new Scanner(System.in);
	private Thread thread;
	public boolean isRunning = true;
	private JixelVariableManager vm;

	public JixelConsole(JixelVariableManager vm) {
		thread = new Thread(this, "Console");
		this.vm = vm;
		thread.start();
	}

	public void print(String message) {
		System.out.println(message);
	}

	public void cInput(String[] input) {
		String answer = "Unknown command.";

		if (input.length == 1) {
			answer = String.valueOf((vm.getValue(input[0])));
			if(!answer.equals("null")){
				print(answer);
			}
		} else if (input.length == 2) {
			answer = "Failed to get";
			if (input[0].equals("save")) {
				if (vm.save(input[1])) {
					answer = "Profile saved.";
				} else {
					answer = "Save failed.";
				}
			} else if (input[0].equals("load")) {
				if (vm.load(input[1])) {
					answer = "Profile loaded.";
				} else {
					answer = "Loading failed.";
				}
			}
			if (input[0].equals("stop")) {
				if (input[1].equals("console")) {
					isRunning = false;
					answer = "Console stopped";
				}
			}
			print(answer);
		} else if (input.length == 3) {
			String previousValue = String.valueOf(vm.getValue(input[1]));
			if (input[0].equals("boolean")) {
				vm.setValue(input[1], Boolean.parseBoolean(input[2]));
			} else if (input[0].equals("byte")) {
				vm.setValue(input[1], Byte.parseByte(input[2]));
			} else if (input[0].equals("char")) {
				vm.setValue(input[1], input[2].charAt(0));
			} else if (input[0].equals("short")) {
				vm.setValue(input[1], Short.parseShort(input[2]));
			} else if (input[0].equals("int")) {
				vm.setValue(input[1], Integer.parseInt(input[2]));
			} else if (input[0].equals("long")) {
				vm.setValue(input[1], Long.parseLong(input[2]));
			} else if (input[0].equals("double")) {
				vm.setValue(input[1], Double.parseDouble(input[2]));
			} else if (input[0].equals("float")) {
				vm.setValue(input[1], Float.parseFloat(input[2]));
			} else if (input[0].equals("String")) {
				vm.setValue(input[1], input[2]);
			}
			String newValue = String.valueOf(vm.getValue(input[1]));
			if(newValue.equals(previousValue)){
				answer = "Failed to set";
			}else{
				answer = "Value set";
			}
			print(answer);
		}
		return;
	}

	@Override
	public void run() {
		while (isRunning) {
			if (scan.hasNextLine()) {
				String[] commands = scan.nextLine().split(" ");
				cInput(commands);
			}
		}
		stop();
	}

	public synchronized void stop() {
		isRunning = false;
		scan.close();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
