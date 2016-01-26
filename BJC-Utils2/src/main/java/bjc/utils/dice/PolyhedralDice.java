package bjc.utils.dice;

public class PolyhedralDice {
	public static Dice d10(int nDice) {
		return new Dice(nDice, 10);
	}
	
	public static Dice d100(int nDice) {
		return new Dice(nDice, 100);
	}
	
	public static Dice d12(int nDice) {
		return new Dice(nDice, 12);
	}
	
	public static Dice d20(int nDice) {
		return new Dice(nDice, 20);
	}
	
	public static Dice d4(int nDice) {
		return new Dice(nDice, 4);
	}
	
	public static Dice d6(int nDice) {
		return new Dice(nDice, 6);
	}
	
	public static Dice d8(int nDice) {
		return new Dice(nDice, 8);
	}
}
