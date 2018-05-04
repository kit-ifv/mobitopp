package edu.kit.ifv.mobitopp.publictransport.matcher.connection;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;

public class Valid extends TypeSafeMatcher<Connection> {

	@Override
	public void describeTo(Description description) {
		description.appendText("valid ");
	}

	@Override
	protected boolean matchesSafely(Connection connection) {
		return connection.isValid();
	}
}