package edu.kit.ifv.mobitopp.util.matcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.time.Time;

public abstract class RouteMatcher {

	public static Matcher<PublicTransportRoute> arrivesAt(Time arrival) {
		return new TypeSafeMatcher<PublicTransportRoute>() {
	
			@Override
			public void describeTo(Description description) {
				description.appendText("arrives at ");
				description.appendValue(arrival);
			}
	
			@Override
			protected boolean matchesSafely(PublicTransportRoute route) {
				return arrival.equals(route.arrival());
			}
	
			@Override
			protected void describeMismatchSafely(
					PublicTransportRoute item, Description mismatchDescription) {
				mismatchDescription.appendText("arrives at ");
				mismatchDescription.appendValue(item.arrival());
			}
		};
	}

}
