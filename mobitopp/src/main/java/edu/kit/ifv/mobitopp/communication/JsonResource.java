package edu.kit.ifv.mobitopp.communication;

import com.google.gson.Gson;

/**
 * The Class JsonResource is a {@link RestResource} that provides the json
 * representation of an arbitrary {@link Object}.
 */
public class JsonResource extends AbstractResource {

	private Object resourceObject;
	private Gson gson;
	private String resourcePath;

	/**
	 * Instantiates a new json resource with the given {@link Object resource
	 * object}. The json representation of that object will be provided at the given
	 * path.
	 * 
	 * E.g. for the given path '/test/example', this resource can be found at
	 * 'http://localhost:PORT/test/example', once it is registered at
	 * {@link RestServerResourceRegistry}.
	 * 
	 * @param resourceObject the resource object
	 * @param path           the resource path
	 */
	public JsonResource(Object resourceObject, String path) {
		super();
		this.resourceObject = resourceObject;
		this.gson = new Gson();
		this.resourcePath = path;
	}

	/**
	 * Instantiates a new json resource with the given {@link Object resource
	 * object} and a default path. 
	 * The default path consists of '/rest/' followed by the given object's
	 * classname in lowercase.
	 * 
	 * E.g. the default path for an object of type 'TestData' is '/rest/testdata'.
	 *
	 * @param resourceObject the resource object
	 */
	public JsonResource(Object resourceObject) {
		this(
			resourceObject,
			"/rest/" + resourceObject.getClass().getSimpleName().toLowerCase());
	}

	/**
	 * Gets the json representation of the resource object as response.
	 *
	 * @return the json representation of the resource object
	 */
	@Override
	public String getResponse() {
		return gson.toJson(resourceObject);
	}

	/**
	 * Gets the resource path.
	 *
	 * @return the resource path
	 */
	@Override
	public String getResourcePath() {
		return resourcePath;
	}

	/**
	 * Register the given objects as {@link JsonResource json resources} at the given {@link RestServerResourceRegistry}.
	 *
	 * @param server  the server
	 * @param objects the objects
	 */
	public static void registerObjectsAsResources(RestServerResourceRegistry server,
		Object... objects) {

		for (Object o : objects) {
			server.registerResource(new JsonResource(o));
		}
	}

}
