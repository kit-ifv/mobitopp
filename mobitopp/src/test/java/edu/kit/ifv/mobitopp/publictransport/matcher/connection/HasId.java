package edu.kit.ifv.mobitopp.publictransport.matcher.connection;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;


public class HasId extends TypeSafeMatcher<Connection> {

	private final int id;

	public HasId(int id) {
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
