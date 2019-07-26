package bjc.utils.ioutils.format;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;

import bjc.utils.ioutils.SimpleProperties;
import bjc.utils.ioutils.format.directives.*;

public class CLTokenizer implements Iterator<Decree> {
	private Matcher mat;

	private Decree dir;

	public CLTokenizer(String strang) {
		this.mat = CLPattern.getDirectiveMatcher(strang);
	}

	@Override
	public boolean hasNext() {
		return !mat.hitEnd();
	}

	@Override
	public Decree next() {
		Decree tk = getNext();

		return tk;
	}

	private Decree getNext() {
		if (!hasNext()) return null;

		if (dir != null) {
			Decree tmp = dir;

			dir = null;

			return tmp;
		}

		StringBuffer sb = new StringBuffer();

		while (mat.find()) {
			mat.appendReplacement(sb, "");
			
			String tmp = sb.toString();

			{
				String dirName   = mat.group("name");
				String dirFunc   = mat.group("funcname");
				String dirMods   = mat.group("modifiers");
				String dirParams = mat.group("params");

				if(dirMods == null) {
					dirMods = "";
				}

				if(dirParams == null) {
					dirParams = "";
				}

				boolean isUser = dirName == null && dirFunc != null;

				dir = new Decree(dirName, isUser,
						CLParameters.fromDirective(dirParams),
						CLModifiers.fromString(dirMods));
			}

			if (tmp.equals("")) {
				Decree dcr = dir;

				dir = null;

				return dcr;
			}

			return new Decree(sb.toString());
		}

		mat.appendTail(sb);

		return new Decree(sb.toString());
	}
}
