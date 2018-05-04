package edu.kit.ifv.mobitopp.publictransport.matcher.connection;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.time.Time;

public class ArriveAt extends TypeSafeMatcher<Connection> {

	private final Time arrival;

	public ArriveAt(Time arrival) {
		super();
		this.arrival = arrival;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("arrives at ");
		description.appendValue(arrival);
	}

	@Override
	protected boolean matchesSafely(Connection connection) {
		return arrival.equals(connection.arrival());
	}

	@Override
	protected void describeMismatchSafely(Connection item, Description mismatchDescription) {
		mismatchDescription.appendText("arrives at ");
		mismatchDescription.appendValue(item.arrival());
	}
}