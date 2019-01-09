package edu.kit.ifv.mobitopp.populationsynthesis;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.ExampleZones;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.ExtendedPatternActivity;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPattern;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.PersonFixedDestination;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.time.Time;

public class PopulationTest {

	private static final int householdId = 1;
	private static final int aPersonOid = 2;
	private static final int otherPersonOid = 3;
  private static final int aPersonNumber = 1;
  private static final short year = 2000;
  private static final long householdNumber = 1;
	
	private Household household;
	private Person aPerson;
	private Person otherPerson;
	private Population population;
	private ExtendedPatternActivity aPattern;
	private ExtendedPatternActivity otherPattern;
  private HouseholdId aHouseholdId;
  private PersonId aPersonId;
	
	@Before
	public void initialise() {
		aPattern = mock(ExtendedPatternActivity.class);
		otherPattern = mock(ExtendedPatternActivity.class);
		when(aPattern.isMainActivity()).thenReturn(true);
		when(otherPattern.isMainActivity()).thenReturn(false);
		when(aPattern.startTime()).thenReturn(Time.start);
		when(otherPattern.startTime()).thenReturn(Time.start.plusMinutes(5));
		aPerson = mock(Person.class);
		otherPerson = mock(Person.class);
		household = mock(Household.class);
		aHouseholdId = new HouseholdId(householdId, year, householdNumber);
		aPersonId =  new PersonId(aPersonOid, aHouseholdId, aPersonNumber);
    when(aPerson.getId()).thenReturn(aPersonId);
		when(aPerson.getOid()).thenReturn(aPersonOid);
		when(otherPerson.getOid()).thenReturn(otherPersonOid);
		when(household.getOid()).thenReturn(householdId);
		when(household.getPersons()).thenReturn(asList(aPerson, otherPerson));
		
		population = Population.empty();
	}

	@Test
	public void manageHouseholds() {
		addHousehold();
		
		assertThat(population.households().collect(toList()), contains(household));
		assertThat(population.householdOids(), contains(householdId));
		assertThat(population.getHouseholdByOid(householdId), hasValue(household));
	}

	private void addHousehold() {
		population.add(household);
	}
	
	@Test
	public void managePersonsViaHousehold() {
		addHousehold();
		
		assertThat(population.getPersonOids(), containsInAnyOrder(aPersonOid, otherPersonOid));
		assertThat(population.getPersonByOid(aPersonOid), hasValue(aPerson));
		assertThat(population.getPersonByOid(otherPersonOid), hasValue(otherPerson));
	}
	
	@Test
	public void managePersons() {
		population.add(aPerson);
		
		assertThat(population.getPersonOids(), containsInAnyOrder(aPersonOid));
		assertThat(population.getPersonByOid(aPersonOid), hasValue(aPerson));
	}
	
	@Test
	public void manageActivityPatterns() {
		TourBasedActivityPattern expectedSchedule = TourBasedActivityPattern.fromExtendedPatternActivities(asList(aPattern, otherPattern));
		
		population.add(aPersonOid, aPattern);
		population.add(aPersonOid, otherPattern);
		TourBasedActivityPattern activitySchedule = population.activityScheduleFor(aPersonOid);
		
		assertThat(activitySchedule, is(equalTo(expectedSchedule)));
	}
	
	@Test
	public void manageActivityPatternsForDifferentPersons() {
		TourBasedActivityPattern anExpectedSchedule = TourBasedActivityPattern.fromExtendedPatternActivities(asList(aPattern, otherPattern));
		TourBasedActivityPattern otherExpectedSchedule = TourBasedActivityPattern.fromExtendedPatternActivities(asList(otherPattern, aPattern));
		
		population.add(aPersonOid, aPattern);
		population.add(aPersonOid, otherPattern);
		population.add(otherPersonOid, otherPattern);
		population.add(otherPersonOid, aPattern);
		TourBasedActivityPattern aSchedule = population.activityScheduleFor(aPersonOid);
		TourBasedActivityPattern otherSchedule = population.activityScheduleFor(otherPersonOid);
		
		assertThat(aSchedule, is(equalTo(anExpectedSchedule)));
		assertThat(otherSchedule, is(equalTo(otherExpectedSchedule)));
	}
	
	@Test
	public void removesPersonsWhenHouseholdIsRemoved() {
		addHousehold();
		
		population.removeHousehold(householdId);
		
		assertThat(population.getPersonOids(), not(hasItems(aPersonOid, otherPersonOid)));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void manageUnknownActivityPatterns() {
		population.add(aPersonOid, aPattern);
		
		population.activityScheduleFor(otherPersonOid);
	}
	
	@Test
  public void getPersonById() {
    addHousehold();
    
    Person person = population.getPerson(aPersonId);
    
    assertThat(person, is(equalTo(aPerson)));
  }
	
	@Test
  public void getFixedDestinationsForPerson() {
    Zone zone = ExampleZones.create().someZone();
    Location location = zone.centroidLocation();
    FixedDestination destination = new FixedDestination(ActivityType.WORK, zone, location);
    PersonFixedDestination fixedDestination = new PersonFixedDestination(aPersonId, destination);
    
    population.add(fixedDestination);
    List<FixedDestination> destinations = population.destinations(aPersonId).collect(toList());
    
    assertThat(destinations, contains(destination));
  }
}
