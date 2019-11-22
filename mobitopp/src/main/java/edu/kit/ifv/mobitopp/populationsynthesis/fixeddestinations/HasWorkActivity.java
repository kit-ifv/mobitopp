package edu.kit.ifv.mobitopp.populationsynthesis.fixeddestinations;

import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.Activity;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.simulation.ActivityType;

public class HasWorkActivity implements Predicate<PersonBuilder> {

	@Override
	public boolean test(PersonBuilder person) {
		return person
				.getActivityPattern()
				.asActivities()
				.stream()
				.map(Activity::activityType)
				.anyMatch(ActivityType.WORK::equals);
	}

}
