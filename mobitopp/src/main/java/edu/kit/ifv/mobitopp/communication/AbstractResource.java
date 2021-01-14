package edu.kit.ifv.mobitopp.communication;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;

/**
 * AbstractResource is an abstract {@link RestResource} specifying how to handle
 * GET request while leaving open the concrete response as well as the resource
 * path which can be implemented by a concrete subclass.
 */
public abstract class AbstractResource implements RestResource {

	private static final String GET = "GET";

	/**
	 * Handle.
	 *
	 * @param exchange the exchange
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	public final void handle(HttpExchange exchange) throws IOException {

		if (exchange.getRequestMethod().equals(GET)) {// Only GET is allowed

			String respText = this.getResponse();
			exchange.sendResponseHeaders(200, respText.getBytes().length);

			OutputStream output = exchange.getResponseBody();
			output.write(respText.getBytes());
			output.flush();

		} else {
			exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
		}

		exchange.close();

	}

}
