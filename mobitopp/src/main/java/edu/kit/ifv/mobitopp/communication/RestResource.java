package edu.kit.ifv.mobitopp.communication;

import com.sun.net.httpserver.HttpHandler;

/**
 * The Interface RestResource extends the {@link HttpHandler} interface and
 * defines methods that have to be provided by resources so that they can be
 * registered at a {@link RestServerResourceRegistry}.
 */
public interface RestResource extends HttpHandler {

	/**
	 * Gets the resource path on which the resource should be provided..
	 *
	 * @return the resource path
	 */
	String getResourcePath();

	/**
	 * Gets the resource's response to a GET call.
	 *
	 * @return the response
	 */
	String getResponse();

}