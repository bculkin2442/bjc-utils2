package bjc.utils.ioutils.format;

public class CLModifiers {
	public final boolean atMod;
	public final boolean colonMod;

	public CLModifiers(boolean at, boolean colon) {
		atMod = at;
		colonMod = colon;
	}

	public static CLModifiers fromString(String modString) {
		boolean atMod = false;
		boolean colonMod = false;
		if (modString != null) {
			atMod = modString.contains("@");
			colonMod = modString.contains(":");
		}

		return new CLModifiers(atMod, colonMod);
	}
}