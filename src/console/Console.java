package console;

import java.util.Scanner;
import math.JixelMath;

public class Console implements Runnable {

	Scanner scan = new Scanner(System.in);
	Thread thread;
	public boolean isRunning = true;
	VariableManager vm;
	
	public Console(VariableManager vm){
		thread = new Thread(this, "Console");
		this.vm = vm;
		thread.start();
	}
	
	public void print(String message){
		System.out.println(message);
	}
	
	public void cInput(String[] input){
		String answer = "Unknown command.";
		int type=-1;
		for(String cmd : input){
			if(cmd.equals("int")){
				type = vm.INT;
			}else if(cmd.equals("flag")){
				type = vm.FLAG;
			}else if(cmd.equals("string")){
				type = vm.STRING;
			}
		}
		
		if(input.length == 1){
			if(input[0].equals("stop")){
				isRunning=false;
				answer = "Console stopped";
			}
		}else if(input.length == 2){
			if(input[0].equals("save")){
				if(vm.save(input[1])){
					answer = "Profile saved.";
				}else{
					answer = "Save failed.";
				}
			}
			if(input[0].equals("load")){
				if(vm.load(input[1])){
					answer = "Profile loaded.";
				}else{
					answer = "Loading failed.";
				}
			}
		}
		
		if(type == -1){
			System.out.println(answer);
			return;
		}
		
		if(input.length == 3){
			answer = "Failed to get";
			if(JixelMath.isStartNum(input[1])){
				if(JixelMath.isNum(input[1])){
					int id = Integer.parseInt(input[1]);
					if(input[2].equals("name")){
						answer = vm.getName(type, id);
					}else if(input[2].equals("value")){
						answer = vm.getValueString(type, id);
					}
				}else{
					answer = "Invalid variable id.";
				}
			}else{
				if(input[2].equals("id")){
					answer = Integer.toString(vm.getID(type, input[1]));
				}else if(input[2].equals("value")){
					answer = vm.getValueString(type, input[1]);
				}
			}
		}else if(input.length == 4){
			if(input[0].equals("set")){
				answer = "Failed to set";
				if(JixelMath.isStartNum(input[2])){
					if(JixelMath.isNum(input[2])){
						int id = Integer.parseInt(input[2]);
						answer = setVM(type, id, input[3]);
					}else{
						answer = "Invalid variable id.";
					}
				}else{
					int id = vm.getID(type, input[3]);
					answer = setVM(type, id, input[3]);
				}
			}
		}
		System.out.println(answer);
		return;
	}
	
	private String setVM(int type, int id, String input){
		String answer = "Failed to set";
		
		if(type==0){
			if(input.equals("true") || input.equals("1")){
				vm.setValue(type, id, true);
				answer = "Set to true";
			}else if(input.equals("false") || input.equals("0")){
				vm.setValue(type, id, false);
				answer = "Set to false";
			}
		}else if(type==1){
			if(JixelMath.isNum(input)){
				int value = Integer.parseInt(input);
				vm.setValue(type, id, value);
				answer = "Set to " + value;
			}
		}else if(type==2){
			vm.setValue(type, id, input);
			answer = "Set to " + input;
		}
		
		return answer;
	}

	@Override
	public void run() {
		while(isRunning){
			if(scan.hasNextLine()){
				String[] commands = scan.nextLine().split(" ");
				cInput(commands);
			}
		}
		stop();
	}
	
	public synchronized void stop(){
		isRunning = false;
		scan.close();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
