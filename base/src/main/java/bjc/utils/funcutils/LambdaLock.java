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
 */
public class LambdaLock {
	/* The read lock. */
	private final Lock readLock;
	/* The write lock. */
	private final Lock writeLock;

	/** Create a new lambda-enabled lock around a new lock. */
	public LambdaLock() {
		this(new ReentrantReadWriteLock());
	}

	/**
	 * Create a new lambda-enabled lock.
	 *
	 * @param lck
	 *            The lock to wrap.
	 */
	public LambdaLock(final ReadWriteLock lck) {
		readLock = lck.readLock();
		writeLock = lck.writeLock();
	}

	/**
	 * Execute an action with the read lock taken.
	 *
	 * @param <T> The type returned by the action.
	 * 
	 * @param supp The action to call.
	 *
	 * @return The result of the action.
	 */
	public <T> T read(final Supplier<T> supp) {
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
	 * @param <T> The type returned by the action.
     * 
	 * @param supp The action to call.
	 *
	 * @return The result of the action.
	 */
	public <T> T write(final Supplier<T> supp) {
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
	 *               The action to call.
	 */
	public void read(final Runnable action) {
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
	 *               The action to call.
	 */
	public void write(final Runnable action) {
		writeLock.lock();

		try {
			action.run();
		} finally {
			writeLock.unlock();
		}
	}
}
