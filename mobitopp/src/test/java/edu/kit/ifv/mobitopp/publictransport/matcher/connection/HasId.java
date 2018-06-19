package edu.kit.ifv.mobitopp.publictransport.matcher.connection;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.ConnectionId;


public class HasId extends TypeSafeMatcher<Connection> {

	private final ConnectionId id;

	public HasId(ConnectionId id) {
		super();
		this.id = id;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("has id ");
		description.appendValue(id);
	}

	@Override
	protected boolean matchesSafely(Connection connection) {
		return id == connection.id();
	}

	@Override
	protected void describeMismatchSafely(Connection item, Description mismatchDescription) {
		mismatchDescription.appendText("has id ");
		mismatchDescription.appendValue(item.id());
	}

}
