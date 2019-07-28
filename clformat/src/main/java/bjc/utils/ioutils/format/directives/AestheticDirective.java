package bjc.utils.ioutils.format.directives;

import java.io.*;

import bjc.utils.esodata.*;
import bjc.utils.ioutils.format.*;

/**
 * Implementation of the A directive.
 * 
 * @author student
 *
 */
public class AestheticDirective implements Directive {

	@Override
	public void format(FormatParameters dirParams) throws IOException {
		Edict edt = compile(dirParams.toCompileCTX());

		edt.format(dirParams.toFormatCTX());
	}

	@Override
	public Edict compile(CompileContext compCTX) {
		CLParameters params = compCTX.decr.parameters;
		CLModifiers mods = compCTX.decr.modifiers;

		// Parameter values
		CLValue mincol = CLValue.nil();
		CLValue colinc = CLValue.nil();
		CLValue minpad = CLValue.nil();

		CLValue padchar = CLValue.nil();

		// We take 0, 1 or 4 parameters
		switch (params.length()) {
		case 0:	
			// Zero parameters, use all defaults
			break;
		case 1:
			params.mapIndices("mincol");

			mincol = params.resolveKey("mincol");
			break;
		case 4:
			params.mapIndices("mincol", "colinc", "minpad", "padchar");

			mincol = params.resolveKey("mincol");
			colinc = params.resolveKey("colinc");
			minpad = params.resolveKey("minpad");

			padchar = params.resolveKey("padchar");
			break;
		default:
			throw new IllegalArgumentException("Must provide either zero, one or four arguments to A directive");
		}

		return new AestheticEdict(mods.atMod, padchar, mincol, colinc, minpad);
	}
}

class AestheticEdict implements Edict {
	private boolean padBefore;

	private CLValue padcharPar;
	private CLValue mincolPar;
	private CLValue colincPar;
	private CLValue minpadPar;

	public AestheticEdict(boolean padBefore, CLValue padPar, CLValue minPar,
			CLValue colPar, CLValue mpadPar) {
		this.padBefore = padBefore;
		
		this.padcharPar = padPar;
		this.mincolPar = minPar;
		this.colincPar = colPar;
		this.minpadPar = mpadPar;
	}

	@Override
	public void format(FormatContext formCTX) throws IOException {
		Tape<Object> itemTape = formCTX.items;

		// Check that we have an item
		CLFormatter.checkItem(itemTape.item(), 'A');

		String tmp = itemTape.item().toString();

		StringBuilder work = new StringBuilder();

		char padchar = padcharPar.asChar(itemTape, "padding character", "A", ' '); 

		int mincol = mincolPar.asInt(itemTape, "minimum column count", "A", 0);
		int colinc = colincPar.asInt(itemTape, "padding increment", "A", 1);
		int minpad = minpadPar.asInt(itemTape, "minumum amount of padding", "A", 0);

		if (padBefore) {
			for (int i = 0; i < minpad; i++) {
				work.append(padchar);
			}

			for (int i = work.length() + tmp.length(); i < mincol; i++) {
				for (int k = 0; k < colinc; k++) {
					work.append(padchar);
				}
			}
		}

		work.append(tmp);

		if (!padBefore) {
			for (int i = 0; i < minpad; i++) {
				work.append(padchar);
			}

			for (int i = work.length(); i < mincol; i++) {
				for (int k = 0; k < colinc; k++) {
					work.append(padchar);
				}
			}
		}

		formCTX.writer.write(work.toString());

		itemTape.right();
	}
}
