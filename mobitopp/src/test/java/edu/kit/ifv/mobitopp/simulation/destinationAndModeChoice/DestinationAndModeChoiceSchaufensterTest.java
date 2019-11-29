package edu.kit.ifv.mobitopp.simulation.destinationAndModeChoice;

import static java.util.stream.Collectors.toSet;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeAvailabilityModel;

public class DestinationAndModeChoiceSchaufensterTest {

	private static final Mode chosenMode = StandardMode.PUBLICTRANSPORT;
	private ActivityType activityType;
	private ModeAvailabilityModel modeAvailabilityModel;
	private DestinationAndModeChoiceUtility utilityFunction;
	private DestinationAndModeChoiceSchaufenster model;
	private Person person;
	private Zone origin;
	private Zone destination;
	private ActivityIfc previousActivity;
	private ActivityIfc nextActivity;
	private Set<Mode> fullChoiceSet;
	private double randomNumber;

	@Before
	public void initialise() {
		activityType = ActivityType.WORK;
		modeAvailabilityModel = mock(ModeAvailabilityModel.class);
		utilityFunction = mock(DestinationAndModeChoiceUtility.class);
		Map<ActivityType, DestinationAndModeChoiceUtility> utilityFunctions = Collections
				.singletonMap(activityType, utilityFunction);
		person = mock(Person.class);
		origin = mock(Zone.class);
		destination = mock(Zone.class);
		previousActivity = mock(ActivityIfc.class);
		nextActivity = mock(ActivityIfc.class);
		fullChoiceSet = Mode.CHOICE_SET_FULL;
		randomNumber = 1.0d;
    model = new DestinationAndModeChoiceSchaufenster(modeAvailabilityModel, utilityFunctions);

		when(nextActivity.activityType()).thenReturn(activityType);
	}

	@Test
	public void filterModesBeforeCalculatingUtilities() {
		filterModesByAvailability();
		filterModesByTravelTime();
		calculateUtility();

		Mode selectedMode = model.selectMode(person, origin, destination, previousActivity,
				nextActivity, fullChoiceSet, randomNumber);

		assertThat(selectedMode, is(equalTo(chosenMode)));
		verify(modeAvailabilityModel).filterAvailableModes(person, origin, destination,
				previousActivity, nextActivity, fullChoiceSet);
		verify(modeAvailabilityModel).modesWithReasonableTravelTime(person, origin, destination,
				previousActivity, nextActivity, filteredChoiceSet(), true);
	}

	protected void calculateUtility() {
		when(utilityFunction.calculateUtilitiesForLowerModel(reasonableTimeChoiceSet(), person,
				previousActivity, nextActivity, origin, destination))
						.thenReturn(Collections.singletonMap(chosenMode, 1.0d));
	}

	protected void filterModesByTravelTime() {
		when(modeAvailabilityModel.modesWithReasonableTravelTime(person, origin, destination,
				previousActivity, nextActivity, filteredChoiceSet(), true))
						.thenReturn(reasonableTimeChoiceSet());
	}

	protected void filterModesByAvailability() {
		when(modeAvailabilityModel.filterAvailableModes(person, origin, destination, previousActivity,
				nextActivity, fullChoiceSet)).thenReturn(filteredChoiceSet());
	}

	private Set<Mode> filteredChoiceSet() {
		return fullChoiceSet.stream().filter(mode -> StandardMode.CAR != mode).collect(toSet());
	}

	protected Set<Mode> reasonableTimeChoiceSet() {
		return fullChoiceSet.stream().filter(mode -> StandardMode.BIKE != mode).collect(toSet());
	}
}
