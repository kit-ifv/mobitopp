package edu.kit.ifv.mobitopp.communication;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import edu.kit.ifv.mobitopp.simulation.WrittenConfiguration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class RestServerResourceRegistry can register {@link RestResource
 * RestResources} and provide them via a HttpServer on the localhost.
 */
@Slf4j
public class RestServerResourceRegistry {

	private HttpServer server;
	@Getter
	private int port;
	@Getter
	private boolean started;
	private ExecutorService executor;
	private List<HttpContext> registered;

	/**
	 * Instantiates a new rest server resource registry.
	 *
	 * @param port the port
	 * @throws IOException Signals that an I/O exception has occurred while starting
	 *                     the server.
	 */
	public RestServerResourceRegistry(int port) throws IOException {
		this.port = port;
		InetSocketAddress address = new InetSocketAddress(port);
		
		this.server = HttpServer.create(address, 0);
		this.registered = new ArrayList<HttpContext>();
	}

	/**
	 * Instantiates a new rest server resource registry using the default port
	 * defined in {@link WrittenConfiguration}.
	 *
	 * @throws IOException Signals that an I/O exception has occurred while starting
	 *                     the server.
	 */
	public RestServerResourceRegistry() throws IOException {
		this(WrittenConfiguration.defaultPort);
	}

	/**
	 * Returns a new {@link RestServerResourceRegistry} with the default port or
	 * null if the server could not be started.
	 *
	 * @return a rest server resource registry or null
	 */
	public static RestServerResourceRegistry create() {
		return create(WrittenConfiguration.defaultPort);
	}

	/**
	 * Returns a new {@link RestServerResourceRegistry} with the given port or null
	 * if the server could not be started.
	 *
	 * @param port the port
	 * @return the rest server resource registry or null
	 */
	public static RestServerResourceRegistry create(int port) {

		try {
			return new RestServerResourceRegistry(port);
		} catch (IOException e) {
			log.warn("Could not create server on port {}", port);
			return null;
		}

	}

	/**
	 * Register the given {@link RestResource resource}.
	 * 
	 * @param resource the resource to be registered
	 */
	public void registerResource(RestResource resource) {
		registered.add(server.createContext(resource.getResourcePath(), resource));
		log.info("Registered resource at http://localhost:{}{}", port, resource.getResourcePath());
	}

	/**
	 * Starts the server if it is ready.
	 * 
	 * If the server has already been started the server cannot be started again.
	 */
	public void start() {
		if (!isStarted()) {
			log.info("Start server on port {}", port);
			started = true;
			createExecutor();
			this.server.start();
		}
	}

	private void createExecutor() {
		executor = Executors.newFixedThreadPool(1);
		server.setExecutor(executor);
	}

	/**
	 * Stops the server in case it has been started previously. The thread running
	 * the server is shut down.
	 */
	public void stop() {
		if (isStarted()) {
			
			for (HttpContext context: registered) {
				this.server.removeContext(context);
			}
			
			this.server.stop(0);
			
			if (executor != null && !executor.isShutdown()) {
				executor.shutdown();
			}
			started = false;
		}

	}

}
