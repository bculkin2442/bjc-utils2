package bjc.utils.ioutils.format;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;

import bjc.utils.ioutils.SimpleProperties;
import bjc.utils.ioutils.format.directives.*;

public class CLTokenizer implements Iterator<String> {
	private Matcher mat;

	private String dir;

	public CLTokenizer(String strang) {
		this.mat = CLPattern.getDirectiveMatcher(strang);
	}

	@Override
	public boolean hasNext() {
		return !mat.hitEnd();
	}

	@Override
	public String next() {
		String tk = getNext();
		// System.out.printf("\tToken: %s\n", tk);

		return tk;
	}

	private String getNext() {
		if (!hasNext()) return "";

		if (dir != null) {
			String tmp = dir;

			dir = null;
			return tmp;
		}

		StringBuffer sb = new StringBuffer();

		while (mat.find()) {
			mat.appendReplacement(sb, "");
			
			dir = mat.group();

			String tmp = sb.toString();
			if (tmp.equals("")) {
				dir = null;

				return mat.group();
			}

			return sb.toString();
		}

		mat.appendTail(sb);

		return sb.toString();
	}
}
