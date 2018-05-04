package edu.kit.ifv.mobitopp.publictransport.matcher.stop;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import edu.kit.ifv.mobitopp.publictransport.model.Neighbourhood;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;

public class NeighbourMatcher extends TypeSafeMatcher<Stop> {

	private final Stop stop;

	public NeighbourMatcher(Stop stop) {
		super();
		this.stop = stop;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("has neighbour that is " + stop);
	}

	@Override
	protected boolean matchesSafely(Stop other) {
		Neighbourhood neighbours = other.neighbours();
		return neighbours.walkTimeTo(stop).isPresent();
	}

	@Override
	protected void describeMismatchSafely(Stop item, Description mismatchDescription) {
		mismatchDescription.appendText("only contains neighbours ");
		mismatchDescription.appendValue(item.neighbours());
	}
}