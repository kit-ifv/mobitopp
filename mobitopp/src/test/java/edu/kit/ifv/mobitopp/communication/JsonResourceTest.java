package edu.kit.ifv.mobitopp.communication;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class JsonResourceTest extends AbstractRestServerTest {
	
	private TestData data;
	
	private int id;
	private String text;
	
	
	@Test
	public void staticJsonResource() throws Exception {
		server.registerResource(resource);
		server.start();
		
		String response = getResponse(server.getPort(), resource.getResourcePath());
		assertEquals("{\"id\":42,\"msg\":\"I am 42!\"}", response);
		
		server.stop();
	}
	
	@Test 
	public void dynamicJsonResurce() throws Exception {
		server.registerResource(resource);
		server.start();
		
		String response = getResponse(server.getPort(), resource.getResourcePath());
		assertEquals("{\"id\":42,\"msg\":\"I am 42!\"}", response);
		
		data.setId(13);
		data.setMsg("Hello World!");
		
		response = getResponse(server.getPort(), resource.getResourcePath());
		assertEquals("{\"id\":13,\"msg\":\"Hello World!\"}", response);
		
		server.stop();
	}
	
	@Test 
	public void defaultPath() {
		JsonResource json = new JsonResource(data);
		assertEquals("/rest/testdata", json.getResourcePath());
	}
	
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	private class TestData {
		private int id;
		private String msg;
	}

	@Override
	protected RestResource createResource() {
		id = 42;
		text = "I am 42!";
		data = new TestData(id, text);
		
		String path = "/test/data";
		resource = new JsonResource(data, path);
		
		return resource;
	}
}
