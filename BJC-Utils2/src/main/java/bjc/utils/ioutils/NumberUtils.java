package bjc.utils.ioutils;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.LongPredicate;

import static java.util.Map.Entry;

public class NumberUtils {
	/*
	 * @TODO Use U+305 for large roman numerals, as well as excels 'concise'
	 * numerals (as implemented by roman()).
	 */
	public static String toRoman(long number, boolean classic) {
		StringBuilder work = new StringBuilder();

		long currNumber = number;

		if(currNumber == 0) {
			return "N";
		}

		if(currNumber < 0) {
			currNumber *= -1;

			work.append("-");
		}

		if(currNumber >= 1000) {
			int numM   = (int)(currNumber / 1000);
			currNumber = currNumber % 1000;

			for(int i = 0; i < numM; i++) {
				work.append("M");
			}
		}

		if(currNumber >= 900 && !classic) {
			currNumber = currNumber % 900;

			work.append("CM");
		}

		if(currNumber >= 500) {
			currNumber = currNumber % 500;

			work.append("D");
		}

		if(currNumber >= 400 && !classic) {
			currNumber = currNumber % 400;

			work.append("CD");
		}

		if(currNumber >= 100) {
			int numC   = (int)(currNumber / 100);
			currNumber = currNumber % 100;

			for(int i = 0; i < numC; i++) {
				work.append("C");
			}
		}

		if(currNumber >= 90 && !classic) {
			currNumber = currNumber % 90;

			work.append("XC");
		}

		if(currNumber >= 50) {
			currNumber = currNumber % 50;

			work.append("L");
		}

		if(currNumber >= 40 && !classic) {
			currNumber = currNumber % 40;

			work.append("XL");
		}

		if(currNumber >= 10) {
			int numX   = (int)(currNumber / 10);
			currNumber = currNumber % 10;

			for(int i = 0; i < numX; i++) {
				work.append("X");
			}
		}

		if(currNumber >= 9 && !classic) {
			currNumber = currNumber % 9;

			work.append("IX");
		}

		if(currNumber >= 5) {
			currNumber = currNumber % 5;

			work.append("V");
		}

		if(currNumber >= 4 && !classic) {
			currNumber = currNumber % 4;

			work.append("IV");
		}

		if(currNumber >= 1) {
			int numI   = (int)(currNumber / 1);
			currNumber = currNumber % 1;

			for(int i = 0; i < numI; i++) {
				work.append("I");
			}
		}

		return work.toString();
	}
	
	public static String toCardinal(long number) {
		return toCardinal(number, null);
	}

	private static String[] cardinals = new String[] {
		"zero", "one", "two", "three", "four", "five", "six", "seven",
			"eight", "nine", "ten", "eleven", "twelve", "thirteen",
			"fourteen", "fifteen", "sixteen", "seventeen", "eighteen",
			"nineteen", "twenty", 
	};

	public static class CardinalState {
		public final Map<Long, String> customNumbers;
		public final Map<LongPredicate, BiFunction<Long, CardinalState, String>> customScales;

		public CardinalState(Map<Long, String> customNumbers, Map<LongPredicate, BiFunction<Long, CardinalState, String>> customScales) {
			this.customNumbers = customNumbers;
			this.customScales  = customScales;
		}

		public String handleCustom(long number) {
			if(customNumbers.containsKey(number)) {
				return customNumbers.get(number);
			}

			for(Entry<LongPredicate, BiFunction<Long, CardinalState, String>> ent : customScales.entrySet()) {
				if(ent.getKey().test(number)) {
					return ent.getValue().apply(number, this);
				}
			}

			return null;
		}
	}

	public static String toCardinal(long number, CardinalState custom) {
		if(custom != null) {
			String res = custom.handleCustom(number);

			if(res != null) return res;
		}

		if(number < 0) return "negative " + toCardinal(number * -1, custom);

		if(number <= 20) return cardinals[(int)number]; 

		if(number < 100) {
			if(number % 10 == 0) {
				switch((int)number) {
				case 30:
					return "thirty";
				case 40:
					return "forty";
				case 50:
					return "fifty";
				case 60:
					return "sixty";
				case 70:
					return "seventy";
				case 80:
					return "eighty";
				case 90:
					return "ninety";
				default:
					/* 
					 * Shouldn't happen.
					 */
					assert(false);
				}
			}

			long numTens = (long)(number / 10);
			long numOnes = number % 10;

			return toCardinal(numTens, custom) + "-" + toCardinal(numOnes, custom);
		}

		if(number < 1000) {
			long numHundreds  = (long)(number / 100);
			long rest         = number % 100;

			return toCardinal(numHundreds, custom) + " hundred and " + toCardinal(rest, custom);
		}

		long MILLION = (long)(Math.pow(10, 6));
		if(number < MILLION) {
			long numThousands = (long)(number / 1000);
			long rest         = number % 1000;

			return toCardinal(numThousands, custom) + " thousand, " + toCardinal(rest, custom);
		}

		long BILLION = (long)(Math.pow(10, 9));
		if(number < BILLION) {
			long numMillions = (long)(number / MILLION);
			long rest        = number % MILLION;

			return toCardinal(numMillions, custom) + " million, " + toCardinal(rest, custom);
		}

		long TRILLION = (long)(Math.pow(10, 12));
		if(number < TRILLION) {
			long numBillions = (long)(number / BILLION);
			long rest         = number % BILLION;

			return toCardinal(numBillions, custom) + " billion, " + toCardinal(rest, custom);
		}

		throw new IllegalArgumentException("Numbers greater than or equal to 1 trillion are not supported yet.");
	}

	public static String toOrdinal(long number) {
		if(number < 0) {
			return "minus " + toOrdinal(number);
		}

		if(number < 20) {
			switch((int)number) {
			case 0:
				return "zeroth";
			case 1:
				return "first";
			case 2:
				return "second";
			case 3:
				return "third";
			case 4:
				return "fourth";
			case 5:
				return "fifth";
			case 6:
				return "sixth";
			case 7:
				return "seventh";
			case 8:
				return "eighth";
			case 9:
				return "ninth";
			case 10:
				return "tenth";
			case 11:
				return "eleventh";
			case 12:
				return "twelfth";
			case 13:
				return "thirteenth";
			case 14:
				return "fourteenth";
			case 15:
				return "fifteenth";
			case 16:
				return "sixteenth";
			case 17:
				return "seventeenth";
			case 18:
				return "eighteenth";
			case 19:
				return "nineteenth";
			default:
				/*
				 * Shouldn't happen.
				 */
				assert(false);
			}
		}

		if(number < 100) {
			if(number % 10 == 0) {
				switch((int)number) {
				case 20:
					return "twentieth";
				case 30:
					return "thirtieth";
				case 40:
					return "fortieth";
				case 50:
					return "fiftieth";
				case 60:
					return "sixtieth";
				case 70:
					return "seventieth";
				case 80:
					return "eightieth";
				case 90:
					return "ninetieth";
				}
			}

			long numPostfix = number % 10;
			return toCardinal(number - numPostfix) + "-" + toOrdinal(numPostfix);
		}

		long procNum = number  % 100;
		long tens    = (long)(procNum / 10);
		long ones    = procNum % 10;

		if(tens == 1) {
			return Long.toString(number) + "th";
		} 

		switch((int)ones) {
		case 1:
			return Long.toString(number) + "st";
		case 2:
			return Long.toString(number) + "nd";
		case 3:
			return Long.toString(number) + "rd";
		default:
			return Long.toString(number) + "th";
		}
	}
	
	private static char[] radixChars = new char[62];
	static {
		int idx = 0;

		for(char i = 0; i < 10; i++) {
			radixChars[idx] = (char)('0' + i);

			idx += 1;
		}

		for(char i = 0; i < 26; i++) {
			radixChars[idx] = (char)('A' + i);

			idx += 1;
		}

		for(char i = 0; i < 26; i++) {
			radixChars[idx] = (char)('a' + i);

			idx += 1;
		}
	}

	public static String toCommaString(long val, int mincols, char padchar, int commaInterval, char commaChar, boolean signed, int radix) {
		if(radix > radixChars.length) {
			throw new IllegalArgumentException(String.format("Radix %d is larger than largest supported radix %d", radix, radixChars.length));
		}

		StringBuilder work = new StringBuilder();

		boolean isNeg = false;
		long currVal = val;
		if(currVal < 0) {
			isNeg = true;
			currVal *= -1;
		}

		if(currVal == 0) {
			work.append(radixChars[0]);
		} else {
			int valCounter = 0;

			while(currVal != 0) {
				valCounter += 1;

				int radDigit = (int)(currVal % radix);
				work.append(radixChars[radDigit]);
				currVal = (long)(currVal / radix);

				if(commaInterval != 0 && valCounter % commaInterval == 0) work.append(commaChar);
			}
		}

		if(isNeg)       work.append("-");
		else if(signed) work.append("+");

		work.reverse();

		/* @TODO Should we have some way to specify how to pad? */
		if(work.length() < mincols) {
			for(int i = work.length(); i < mincols; i++) {
				work.append(padchar);
			}
		}

		return work.toString();
	}

	public static String toNormalString(long val, int mincols, char padchar, boolean signed, int radix) {
		return toCommaString(val, mincols, padchar, 0, ',', signed, radix);
	}
}
