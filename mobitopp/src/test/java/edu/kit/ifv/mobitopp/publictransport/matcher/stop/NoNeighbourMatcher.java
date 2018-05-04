package edu.kit.ifv.mobitopp.publictransport.matcher.stop;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;

public final class NoNeighbourMatcher extends TypeSafeMatcher<Stop> {

	@Override
	public void describeTo(Description description) {
		description.appendText("has no neighbour");
	}

	@Override
	protected boolean matchesSafely(Stop stop) {
		return stop.neighbours().isEmpty();
	}

	@Override
	protected void describeMismatchSafely(Stop item, Description mismatchDescription) {
		mismatchDescription.appendText("has neighbours");
		mismatchDescription.appendValue(item.neighbours());
	}
}