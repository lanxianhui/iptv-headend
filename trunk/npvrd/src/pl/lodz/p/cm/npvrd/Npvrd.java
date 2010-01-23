package pl.lodz.p.cm.npvrd;

public class Npvrd {

	/**
	 * @param args Argumenty przekazane do programu przez linię komend
	 */
	public static void main(String[] args) {
		
		for (int i = 0; i < args.length; i++) {
			System.out.println(args[i]);
			if (args[i].equals("-c")) {
				System.out.println("Configuration file: " + args[++i]);
			}
		}
		
	}

}
