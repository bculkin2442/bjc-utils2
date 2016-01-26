package bjc.utils.dice;

import java.util.Random;

public class Die {
	private static Random rng = new Random();
	
	private int nSides;
	
	public Die(int nSides) {
		this.nSides = nSides;
	}
	
	public int roll() {
		return rng.nextInt(nSides + 1);
	}
}
