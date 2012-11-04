package math;

public class JixelMath {

	public static boolean isStartNum(String s){
		byte b = (byte)s.charAt(0);
		if(b>=48 && b<=57){
			return true;
		}
		return false;
	}
	public static boolean isNum(String s){
		for(int i=0; i<s.length(); i++){
			byte b = (byte)s.charAt(i);
			if(b<48 || b>57){
				return false;
			}
		}
		return true;
	}
}
