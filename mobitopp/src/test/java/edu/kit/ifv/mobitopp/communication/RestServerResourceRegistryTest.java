package edu.kit.ifv.mobitopp.communication;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpExchange;

public class RestServerResourceRegistryTest extends AbstractRestServerTest {
	
	
	
	@Test
	public void provideSimpleResource() throws Exception {
		server.registerResource(resource);
		
		assertEquals(server.isStarted(), false);
		server.start();
		assertEquals(server.isStarted(), true);
		
		String response = getResponse(server.getPort(), resource.getResourcePath());
		assertEquals(expectedResponse, response);
		
		server.stop();
		assertEquals(server.isStarted(), false);
		server = null;
	}

	@Test
	public void provideNoResource() {
		assertEquals(server.isStarted(), false);
		server.start();
		assertEquals(server.isStarted(), true);
		server.stop();
		assertEquals(server.isStarted(), false);
	}
	
	
	
	

	protected RestResource createResource() {
		return new RestResource() {
			
			@Override
			public void handle(HttpExchange exchange) throws IOException {
				String respText = this.getResponse();
				exchange.sendResponseHeaders(200, respText.getBytes().length);

				OutputStream output = exchange.getResponseBody();
				output.write(respText.getBytes());
				output.flush();

				exchange.close();				
			}
			
			@Override
			public String getResponse() {
				return expectedResponse;
			}
			
			@Override
			public String getResourcePath() {
				return "/test/hello";
			}
			
		};
	}
	
}
