package bjc.utils.esodata;

import java.util.ArrayList;

public class Tape<T> {
	private ArrayList<T> backing;
	private int pos;

	public Tape() {
		backing = new ArrayList<>();
	}

	public T item() {
		return backing.get(pos);
	}

	public boolean left() {
		if(pos == 0) return false;
		
		pos -= 1;
		return true;
	}

	public boolean left(int amt) {
		if((pos - amt) < 0) return false;

		pos -= amt;
		return true;
	}

	public boolean right() {
		if(pos == (backing.size() - 1)) return false;

		pos += 1;
		return true;
	}

	public boolean right(int amt) {
		if((pos + amt) >= (backing.size() - 1)) return false;

		pos += amt;
		return true;
	}
}
