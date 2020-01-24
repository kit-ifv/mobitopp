package edu.kit.ifv.mobitopp.simulation;

import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoicePreferences;

public interface BasePerson {
	
	BaseHousehold household();

	boolean hasAccessToCar();

	boolean hasBike();

	boolean hasCommuterTicket();

	boolean hasDrivingLicense();

	PersonId getId();

	Gender gender();

	Employment employment();

	int age();

	Graduation graduation();

	int getIncome();

	Zone homeZone();

	boolean hasFixedZoneFor(ActivityType activityType);

	Zone fixedZoneFor(ActivityType activityType);

	boolean hasFixedActivityZone();

	Zone fixedActivityZone();

	Location fixedDestinationFor(ActivityType activityType);

	boolean isFemale();

	boolean isMale();

	Stream<FixedDestination> getFixedDestinations();

	ModeChoicePreferences modeChoicePrefsSurvey();

	ModeChoicePreferences modeChoicePreferences();

}