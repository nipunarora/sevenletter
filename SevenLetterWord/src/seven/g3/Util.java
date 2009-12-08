package seven.g3;

public class Util {

	public static final boolean DEBUG = false;

	public static void println(String s) {
		if (DEBUG)
			System.out.println(s);
	}

	public static void println() {
		if (DEBUG)
			System.out.println();
		
	}
	
	public static void print(String s) {
		if (DEBUG)
			System.out.print(s);
	}
}
