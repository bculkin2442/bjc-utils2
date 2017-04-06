package bjc.utils.funcutils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

/**
 * A wrapper around a {@link ReadWriteLock} to ensure that the lock is used
 * properly.
 * 
 * @author EVE
 *
 */
public class LambdaLock {
	private Lock	readLock;
	private Lock	writeLock;

	/**
	 * Create a new lambda-enabled lock around a new lock.
	 */
	public LambdaLock() {
		this(new ReentrantReadWriteLock());
	}

	/**
	 * Create a new lambda-enabled lock.
	 * 
	 * @param lck
	 *                The lock to wrap.
	 */
	public LambdaLock(ReadWriteLock lck) {
		readLock = lck.readLock();
		writeLock = lck.writeLock();
	}

	/**
	 * Execute an action with the read lock taken.
	 * 
	 * @param supp
	 *                The action to call.
	 * 
	 * @return The result of the action.
	 */
	public <T> T read(Supplier<T> supp) {
		readLock.lock();

		try {
			return supp.get();
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * Execute an action with the write lock taken.
	 * 
	 * @param supp
	 *                The action to call.
	 * 
	 * @return The result of the action.
	 */
	public <T> T write(Supplier<T> supp) {
		writeLock.lock();

		try {
			return supp.get();
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Execute an action with the read lock taken.
	 * 
	 * @param action
	 *                The action to call.
	 * 
	 * @return The result of the action.
	 */
	public void read(Runnable action) {
		readLock.lock();

		try {
			action.run();
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * Execute an action with the write lock taken.
	 * 
	 * @param action
	 *                The action to call.
	 */
	public void write(Runnable action) {
		writeLock.lock();

		try {
			action.run();
		} finally {
			writeLock.unlock();
		}
	}
}