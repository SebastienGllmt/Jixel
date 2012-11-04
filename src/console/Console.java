package console;

import java.util.Scanner;

public class Console implements Runnable {

	Scanner scan = new Scanner(System.in);
	Thread thread;
	public boolean isRunning = true;
	VariableManager vm;
	
	public static void main(String[] args) {
		VariableManager var = new VariableManager();
		var.newVar(var.FLAG, "BOOL", true);
		var.newVar(var.INT, "TEST", 5);
		var.newVar(var.STRING, "STRING", "HEY");
		Console cons = new Console(var);
	}
	
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
		if(type == -1){
			System.out.println(answer);
			return;
		}
		if(input.length == 1){
			if(input[0].equals("stop")){
				isRunning=false;
				answer = "Console stopped";
			}
		}else if(input.length == 2){
			
		}else if(input.length == 3){
			if(isNum(input[1].charAt(0))){
				int value = Integer.parseInt(input[1]);
				if(input[2].equals("name")){
					answer = vm.getName(type, value);
				}else if(input[2].equals("value")){
					answer = vm.getValueString(type, value);
				}
			}else{
				if(input[2].equals("id")){
					answer = Integer.toString(vm.getID(type, input[1]));
				}else if(input[2].equals("value")){
					answer = vm.getValueString(type, input[1]);
				}
			}
		}else if(input.length == 4){
			
		}
		System.out.println(answer);
		return;
	}
	
	private boolean isNum(char c){
		byte b = (byte)c;
		if(b>=48 && b<=57){
			return true;
		}
		return false;
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
