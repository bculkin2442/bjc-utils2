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

	public int size() {
		return backing.size();
	}

	public void item(T itm) {
		backing.set(pos, itm);
	}

	public void append(T itm) {
		backing.add(itm);
	}

	// Add an item before the current
	public void insert(T itm) {
		backing.add(pos, itm);
	}

	public T remove() {
		if(pos != 0) pos -= 1;
		return backing.remove(pos);
	}

	public void first() {
		pos = 0;
	}

	public void last() {
		pos = backing.size() - 1;
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
