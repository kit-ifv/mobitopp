package edu.kit.ifv.mobitopp.populationsynthesis;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Person;

public class PopulationTest {

	private static final int householdId = 1;
	private static final int aPersonOid = 2;
	private static final int otherPersonOid = 3;
	
	private Household household;
	private Person aPerson;
	private Person otherPerson;
	private Population population;
	private PatternActivity aPattern;
	private PatternActivity otherPattern;
	
	@Before
	public void initialise() {
		aPattern = mock(PatternActivity.class);
		otherPattern = mock(PatternActivity.class);
		aPerson = mock(Person.class);
		otherPerson = mock(Person.class);
		household = mock(Household.class);
		when(aPerson.getOid()).thenReturn(aPersonOid);
		when(otherPerson.getOid()).thenReturn(otherPersonOid);
		when(household.getOid()).thenReturn(householdId);
		when(household.getPersons()).thenReturn(asList(aPerson, otherPerson));
		
		population = Population.empty();
	}

	@Test
	public void manageHouseholds() {
		addHousehold();
		
		assertThat(population.households(), contains(household));
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
		PatternActivityWeek expectedSchedule = new PatternActivityWeek(asList(aPattern, otherPattern));
		
		population.add(aPersonOid, aPattern);
		population.add(aPersonOid, otherPattern);
		PatternActivityWeek activitySchedule = population.activityScheduleFor(aPersonOid);
		
		assertThat(activitySchedule, is(equalTo(expectedSchedule)));
	}
	
	@Test
	public void manageActivityPatternsForDifferentPersons() {
		PatternActivityWeek anExpectedSchedule = new PatternActivityWeek(asList(aPattern));
		PatternActivityWeek otherExpectedSchedule = new PatternActivityWeek(asList(otherPattern));
		
		population.add(aPersonOid, aPattern);
		population.add(otherPersonOid, otherPattern);
		PatternActivityWeek aSchedule = population.activityScheduleFor(aPersonOid);
		PatternActivityWeek otherSchedule = population.activityScheduleFor(otherPersonOid);
		
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
}
