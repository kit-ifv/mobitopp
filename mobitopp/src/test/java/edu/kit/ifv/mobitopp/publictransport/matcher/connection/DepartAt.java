package edu.kit.ifv.mobitopp.publictransport.matcher.connection;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.time.Time;

public class DepartAt extends TypeSafeMatcher<Connection> {

	private final Time departure;

	public DepartAt(Time departure) {
		super();
		this.departure = departure;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("departs at ");
		description.appendValue(departure);
	}

	@Override
	protected boolean matchesSafely(Connection connection) {
		return departure.equals(connection.departure());
	}
}