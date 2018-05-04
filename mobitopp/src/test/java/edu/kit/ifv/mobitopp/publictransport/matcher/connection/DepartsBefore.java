package edu.kit.ifv.mobitopp.publictransport.matcher.connection;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.time.Time;

public class DepartsBefore extends TypeSafeMatcher<Connection> {

	private final Time time;

	public DepartsBefore(Time time) {
		super();
		this.time = time;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("departs before");
		description.appendValue(time);
	}

	@Override
	protected boolean matchesSafely(Connection connection) {
		return connection.departsBefore(time);
	}
}