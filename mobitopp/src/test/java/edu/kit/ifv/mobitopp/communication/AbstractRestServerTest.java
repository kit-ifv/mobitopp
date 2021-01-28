package edu.kit.ifv.mobitopp.communication;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractRestServerTest {
	
	protected RestResource resource;
	protected RestServerResourceRegistry server;
	protected String expectedResponse;
	protected static int cnt = 0;

	@BeforeEach
	public void setUp() throws IOException {
		expectedResponse = "Hello World!";
		resource = createResource();
		server = new RestServerResourceRegistry(9876 + (cnt++));
	}
	
	public static String getResponse(int port, String resourcePath) throws Exception {
	    HttpClient client = HttpClient.newHttpClient();
	    HttpRequest request = HttpRequest.newBuilder()
	          .uri(URI.create(uriString(port, resourcePath)))
	          .build();

	    HttpResponse<String> response =
	          client.send(request, BodyHandlers.ofString());

	    return response.body();
	}

	
	private static String uriString(int port, String resourcePath) {
		return "http://localhost:" + port + resourcePath;
	}
	
	protected abstract RestResource createResource();
}
