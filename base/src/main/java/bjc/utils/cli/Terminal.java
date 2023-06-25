package bjc.utils.cli;

import java.util.*;
import java.util.concurrent.TimeUnit;

import bjc.data.Either;

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
	 * 
	 * @return The ID of the request
	 */
	long submitRequest(String req);

	/**
	 * Await a reply for a given request. Will block the current thread until a
	 * response is available.
	 * 
	 * @param id The ID of the request
	 * 
	 * @return The response to that request
	 * 
	 * @throws InterruptedException If we are interrupted waiting for the reply
	 */
	String awaitReply(long id) throws InterruptedException;
	
	/**
	 * Await a reply for a given request. Will block the current thread until a
	 * response is available or the specified timeout occurs.
	 * 
	 * @param id The ID of the request
	 * @param unit The unit of time to wait
	 * @param delay The amount of units to wait before timing out
	 * 
	 * @return The response to that request, or an empty optional if it timed out
	 * 
	 * @throws InterruptedException If we are interrupted waiting for the reply
	 */
	Optional<String> awaitReply(long id, TimeUnit unit, long delay) throws InterruptedException;

	/**
	 * Check if a reply for a request is available, without blocking.
	 * 
	 * @param id The ID of the request
	 * 
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
	String submitRequestSync(String req) throws InterruptedException;

	/**
	 * Submit a request and await the reply to it. Will block the current thread
	 * until a response is received or the timeout occurs.
	 * 
	 * @param req The request to submit
	 * @param unit The unit of time to wait
	 * @param delay The amount of units to wait before timing out
	 * 
	 * @return The reply to the request, or the ID of the request if the timeout occurred
	 * 
	 * @throws InterruptedException If we are interrupted waiting for the reply
	 */
	Either<String, Long> submitRequestSync(String req, TimeUnit unit, long delay) throws InterruptedException;

}