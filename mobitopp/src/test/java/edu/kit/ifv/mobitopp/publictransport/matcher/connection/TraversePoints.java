package edu.kit.ifv.mobitopp.publictransport.matcher.connection;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.RoutePoints;

public class TraversePoints extends TypeSafeMatcher<Connection> {

	private final RoutePoints points;

	public TraversePoints(RoutePoints points) {
		super();
		this.points = points;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("traverses points ");
		description.appendValue(points);
	}

	@Override
	protected boolean matchesSafely(Connection connection) {
		return points.equals(connection.points());
	}
}