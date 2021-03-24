package edu.kit.ifv.mobitopp.populationsynthesis;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.ExampleZones;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.ExtendedPatternActivity;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPattern;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.PersonFixedDestination;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.time.Time;

public class PopulationTest {

	private static final int householdId = 1;
	private static final int aPersonOid = 2;
	private static final int otherPersonOid = 3;
	private static final int aPersonNumber = 1;
	private static final short year = 2000;
	private static final long householdNumber = 1;

	private HouseholdForSetup household;
	private PersonBuilder aPerson;
	private PersonBuilder otherPerson;
	private Population population;
	private ExtendedPatternActivity aPattern;
	private ExtendedPatternActivity otherPattern;
	private HouseholdId aHouseholdId;
	private PersonId aPersonId;

	@BeforeEach
	public void initialise() {
		aPattern = mock(ExtendedPatternActivity.class);
		otherPattern = mock(ExtendedPatternActivity.class);
		when(aPattern.isMainActivity()).thenReturn(true);
		when(otherPattern.isMainActivity()).thenReturn(false);
		when(aPattern.startTime()).thenReturn(Time.start);
		when(otherPattern.startTime()).thenReturn(Time.start.plusMinutes(5));
		aPerson = mock(PersonBuilder.class);
		otherPerson = mock(PersonBuilder.class);
		household = mock(HouseholdForSetup.class);
		aHouseholdId = new HouseholdId(householdId, year, householdNumber);
		aPersonId = new PersonId(aPersonOid, aHouseholdId, aPersonNumber);
		PersonId otherPersonId = new PersonId(otherPersonOid, aHouseholdId, aPersonNumber);
		when(aPerson.getId()).thenReturn(aPersonId);
		when(otherPerson.getId()).thenReturn(otherPersonId);
		when(household.getId()).thenReturn(aHouseholdId);
		when(household.getPersons()).thenReturn(asList(aPerson, otherPerson));

		population = Population.empty();
	}

	@Test
	public void manageHouseholds() {
		addHousehold();

		assertThat(population.householdsForSetup().collect(toList())).contains(household);
		assertThat(population.getHouseholdForSetupByOid(householdId)).hasValue(household);
	}

	private void addHousehold() {
		population.add(household);
	}

	@Test
	public void managePersonsViaHousehold() {
		addHousehold();

		assertThat(population.getPersonOids()).contains(aPersonOid, otherPersonOid);
		assertThat(population.getPersonBuilderByOid(aPersonOid)).hasValue(aPerson);
		assertThat(population.getPersonBuilderByOid(otherPersonOid)).hasValue(otherPerson);
	}

	@Test
	public void managePersons() {
		population.add(aPerson);

		assertThat(population.getPersonOids()).contains(aPersonOid);
		assertThat(population.getPersonBuilderByOid(aPersonOid)).hasValue(aPerson);
	}

	@Test
	public void manageActivityPatterns() {
		TourBasedActivityPattern expectedSchedule = TourBasedActivityPattern
			.fromExtendedPatternActivities(asList(aPattern, otherPattern));

		population.add(aPersonOid, aPattern);
		population.add(aPersonOid, otherPattern);
		TourBasedActivityPattern activitySchedule = population.activityScheduleFor(aPersonOid);

		assertThat(activitySchedule).isEqualTo(expectedSchedule);
	}

	@Test
	public void manageActivityPatternsForDifferentPersons() {
		TourBasedActivityPattern anExpectedSchedule = TourBasedActivityPattern
			.fromExtendedPatternActivities(asList(aPattern, otherPattern));
		TourBasedActivityPattern otherExpectedSchedule = TourBasedActivityPattern
			.fromExtendedPatternActivities(asList(otherPattern, aPattern));

		population.add(aPersonOid, aPattern);
		population.add(aPersonOid, otherPattern);
		population.add(otherPersonOid, otherPattern);
		population.add(otherPersonOid, aPattern);
		TourBasedActivityPattern aSchedule = population.activityScheduleFor(aPersonOid);
		TourBasedActivityPattern otherSchedule = population.activityScheduleFor(otherPersonOid);

		assertThat(aSchedule).isEqualTo(anExpectedSchedule);
		assertThat(otherSchedule).isEqualTo(otherExpectedSchedule);
	}

	@Test
	public void removesPersonsWhenHouseholdIsRemoved() {
		addHousehold();

		population.removeHousehold(householdId);

		assertThat(population.getPersonOids()).doesNotContain(aPersonOid, otherPersonOid);
	}

	@Test
	public void removesPersons() {
		addHousehold();

		population.removePerson(aPersonOid);

		assertThat(population.getPersonOids()).doesNotContain(aPersonOid);
	}

	@Test
	public void manageUnknownActivityPatterns() {
		population.add(aPersonOid, aPattern);

		assertThrows(IllegalArgumentException.class, () -> population.activityScheduleFor(otherPersonOid));
	}

	@Test
	public void getPersonById() {
		addHousehold();

		Optional<PersonBuilder> person = population.getPerson(aPersonId);

		assertThat(person).hasValue(aPerson);
	}

	@Test
	public void getFixedDestinationsForPerson() {
		Zone zone = ExampleZones.create().someZone();
		Location location = zone.centroidLocation();
		FixedDestination destination = new FixedDestination(ActivityType.WORK, zone, location);
		PersonFixedDestination fixedDestination = new PersonFixedDestination(aPersonId,
			destination);

		population.add(fixedDestination);
		List<FixedDestination> destinations = population.destinations(aPersonId).collect(toList());

		assertThat(destinations).contains(destination);
	}
	
	@Test
	void clearLongTermData() throws Exception {
		addHousehold();
		
		population.clearLongTermData();

		assertThat(population.householdsForSetup()).isEmpty();
		assertThat(population.getPersonBuilderByOid(aPersonOid)).isEmpty();
	}

}
