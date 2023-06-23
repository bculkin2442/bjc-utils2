package bjc.utils.cli;

import java.util.*;

/**
 * A terminal with support for asking multiple questions, and retrieving the
 * results piecemeal.
 * 
 * This class is heavily inspired by the way that the old IBM MVS terminal
 * worked, where the terminal would send you a series of requests and you would
 * reply to them in whatever order you wanted.
 * 
 * @author bjcul
 */
public interface Terminal {
	/**
	 * Submit a request to the terminal
	 * 
	 * @param req The body of the request
	 * @return The ID of the request
	 */
	long submitRequest(String req);

	/**
	 * Await a reply for a given request. Will block the current thread until a
	 * response is available.
	 * 
	 * @param id The ID of the request
	 * @return The response to that request
	 * @throws InterruptedException If we are interrupted waiting for the reply
	 */
	String awaitReply(long id) throws InterruptedException;

	/**
	 * Check if a reply for a request is available, without blocking.
	 * 
	 * @param id The ID of the request
	 * @return The reply to the request if one is available, and empty otherwise
	 */
	Optional<String> checkReply(long id);

	/**
	 * Submit a request and await the reply to it. Will block the current thread
	 * until a response is received.
	 * 
	 * @param req The request to submit
	 * @return The reply to the request
	 * @throws InterruptedException If we are interrupted waiting for the reply
	 */
	String submitRequestSync(String req) throws InterruptedException;;
}