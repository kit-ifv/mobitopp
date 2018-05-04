package edu.kit.ifv.mobitopp.publictransport.matcher.connection;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;

public class PartOf extends TypeSafeMatcher<Connection> {

	private final Journey journey;

	public PartOf(Journey journey) {
		super();
		this.journey = journey;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("part of ");
		description.appendValue(journey);
	}

	@Override
	protected boolean matchesSafely(Connection connection) {
		return journey.equals(connection.journey());
	}

	@Override
	protected void describeMismatchSafely(Connection connection, Description mismatchDescription) {
		mismatchDescription.appendText("part of ");
		mismatchDescription.appendValue(connection.journey());
	}

}
