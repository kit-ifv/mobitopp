package edu.kit.ifv.mobitopp.simulation.tour;


import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;

public interface Tour {

		int numberOfTrips();
		
		ActivityIfc firstActivity();
		ActivityIfc lastActivity();
		
		ActivityIfc mainActivity();
		ActivityType purpose();
		
		boolean containsActivityOf(ActivityType type);
		boolean contains(ActivityIfc activity);

		boolean isFirstActivity(ActivityIfc activity);

		Mode mode();
		
		List<ActivityIfc> asList();
		
		List<ActivityIfc> activitiesBetween(ActivityIfc previousActivity, ActivityIfc mainActivity);
		
		boolean hasPreviousTour();
		Tour previousTour();
		
		int tourNumber();
		
		boolean hasNextActivityOf(ActivityType type, ActivityIfc currentActivity);
		Optional<ActivityIfc> firstActivityOf(ActivityType type);
		Optional<ActivityIfc> nextActivityOf(ActivityType type, ActivityIfc currentActivity);
		
		boolean containsSubtour();
		int numberOfSubtours();
		Subtour nthSubtour(int n);
		
		boolean isStartOfSubtour(ActivityIfc activity);
		boolean isEndOfSubtour(ActivityIfc activity);
		boolean isInSubtour(ActivityIfc activity);
		
		Optional<Subtour> correspondingSubtour(ActivityIfc activity);
		
		String forLogging();
}
