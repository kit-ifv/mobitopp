package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.ExtendedPatternActivity;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPattern;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.BasePerson;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoicePreferences;

public interface PersonBuilder extends BasePerson {

	/**
	 * Generates a {@link Person} for the {@link Household} based on the configured attributes.
	 * 
	 * @param household
	 *          household of the person
	 * @return person belonging to the given household with all attributes set
	 */
	Person toPerson(Household household);

	HouseholdForSetup household();

	Zone homeZone();

	PersonBuilder addFixedDestination(FixedDestination fixedDestination);

	PersonBuilder clearFixedDestinations();

	Optional<FixedDestination> getFixedDestination(ActivityType activityType);

	Stream<FixedDestination> fixedDestinations();

	@Override
	default Stream<FixedDestination> getFixedDestinations() {
		return fixedDestinations();
	}

	@Override
	default Location fixedDestinationFor(ActivityType activityType) {
		return getFixedDestination(activityType)
				.map(FixedDestination::location)
				.orElseThrow(() -> new IllegalArgumentException(
						"Could not find a fixed destination for activity type: " + activityType));
	}

	ModeChoicePreferences getModeChoicePrefsSurvey();

	@Override
	default ModeChoicePreferences modeChoicePrefsSurvey() {
		return getModeChoicePrefsSurvey();
	}

	PersonBuilder setHasBike(boolean hasBike);

	PersonBuilder setHasAccessToCar(boolean hasAccessToCar);

	boolean hasPersonalCar();

	PersonBuilder setHasPersonalCar(boolean hasPersonalCar);

	PersonBuilder setHasCommuterTicket(boolean hasCommuterTicket);

	PersonBuilder setHasDrivingLicense(boolean hasDrivingLicense);

	ModeChoicePreferences getModeChoicePreferences();

	PersonBuilder setModeChoicePreferences(ModeChoicePreferences modeChoicePreferences);

	PersonBuilder setTravelTimeSensitivity(ModeChoicePreferences travelTimeSensitivity);

	@Override
	default ModeChoicePreferences modeChoicePreferences() {
		return getModeChoicePreferences();
	}

	TourBasedActivityPattern getActivityPattern();

	/**
	 * Set the {@link TourBasedActivityPattern} as pattern for the whole week. Calling this method
	 * clears all activities collected by {@link #addPatternActivity(ExtendedPatternActivity)}.
	 * 
	 * @param activityPattern
	 * @return this builder
	 */
	PersonBuilder setPatternActivityWeek(TourBasedActivityPattern activityPattern);

	PatternActivityWeek getPatternActivityWeek();

	/**
	 * Clear the {@link TourBasedActivityPattern} set by
	 * {@link #setPatternActivityWeek(TourBasedActivityPattern)}.
	 * 
	 * @return this builder
	 */
	PersonBuilder clearPatternActivityWeek();

	/**
	 * This method is used to incrementally build up the whole pattern activity week by adding one
	 * activity after the other. Calling this method will clear the {@link TourBasedActivityPattern}
	 * set by {@link #setPatternActivityWeek(TourBasedActivityPattern)}. The collected activities will
	 * be cleared, when {@link #setPatternActivityWeek(TourBasedActivityPattern)} is called.
	 * 
	 * @param activity
	 *          activity to add to the week.
	 * @return this builder
	 */
	PersonBuilder addPatternActivity(ExtendedPatternActivity activity);

	boolean hasActivityOfType(ActivityType... activityType);

	boolean hasActivityOfTypes(Collection<ActivityType> activityTypes);

	PersonBuilder setMobilityProviderMembership(Map<String, Boolean> membership);

	Map<String, Boolean> getMobilityProviderMembership();

}
