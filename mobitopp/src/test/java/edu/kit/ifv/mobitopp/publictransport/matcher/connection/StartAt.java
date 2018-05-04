package edu.kit.ifv.mobitopp.publictransport.matcher.connection;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;

public class StartAt extends TypeSafeMatcher<Connection> {

	private final Stop stop;

	public StartAt(Stop stop) {
		super();
		this.stop = stop;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("starts at ");
		description.appendValue(stop);
	}

	@Override
	protected boolean matchesSafely(Connection connection) {
		return stop.equals(connection.start());
	}
}