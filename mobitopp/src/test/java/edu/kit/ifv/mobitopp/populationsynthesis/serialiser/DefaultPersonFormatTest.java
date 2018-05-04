package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static edu.kit.ifv.mobitopp.util.TestUtil.assertValue;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.Example;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.emobility.EmobilityPerson;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;

public class DefaultPersonFormatTest {

	private static final int year = 2011;
	private static final int zoneOid = 1;
	private static final int personOid = 1;
	private static final int personNumber = personOid;
	private static final int householdOid = 0;

	private DefaultPersonFormat format;
	private Household household;
	private Person personForDemand;
	private Person emobilityPerson;
	private PopulationContext context;
	private Zone zone;

	@Before
	public void initialise() {
		context = mock(PopulationContext.class);
		format = new DefaultPersonFormat();
		household = mock(Household.class);
		zone = mock(Zone.class);
		when(zone.getOid()).thenReturn(zoneOid);
		when(household.getId()).thenReturn(new HouseholdId(year, householdOid));
		when(household.getOid()).thenReturn(householdOid);
		personForDemand = Example.personOf(household, personOid, zone);
		emobilityPerson = Example.emobilityPersonOf(household, personOid, zone);
		when(context.getHouseholdByOid(householdOid)).thenReturn(household);
		when(context.activityScheduleFor(personOid)).thenReturn(Example.activitySchedule());
	}

	@Test
	public void serialisePersonAttributes() throws IOException {
		List<String> prepared = format.prepare(personForDemand, context);

		assertThat(prepared, is(equalTo(personFormat())));
	}

	@Test
	public void serialialiseEmobilityPerson() throws IOException {
		List<String> prepared = format.prepare(emobilityPerson, context);

		assertThat(prepared, is(equalTo(emobilityPerson())));
	}
	
	@Test
	public void parseNormalPerson() {
		Person parsedPerson = format.parse(personFormat(), context);
		
		assertPersons(parsedPerson, personForDemand);
		verify(household).addPerson(parsedPerson);
	}
	
	@Test
	public void parseEmobilityPerson() {
		Person parsedPerson = format.parse(emobilityPerson(), context);

		assertPersons(parsedPerson, emobilityPerson);
		verify(household).addPerson(parsedPerson);
	}

	private void assertPersons(Person person, Person originalPerson) {
		assertValue(Person::getOid, person, originalPerson);
		assertValue(Person::getId, person, originalPerson);
		assertValue(Person::age, person, originalPerson);
		assertValue(Person::employment, person, originalPerson);
		assertValue(Person::gender, person, originalPerson);
		assertValue(Person::hasBike, person, originalPerson);
		assertValue(Person::hasAccessToCar, person, originalPerson);
		assertValue(Person::hasPersonalCar, person, originalPerson);
		assertValue(Person::hasCommuterTicket, person, originalPerson);
		assertValue(Person::getPatternActivityWeek, person, originalPerson);
		assertEmobilityPersons(person, originalPerson);
	}

	private void assertEmobilityPersons(Person person, Person originalPerson) {
		assertThat(person.getClass(), is(equalTo(originalPerson.getClass())));
		if (originalPerson instanceof EmobilityPerson && person instanceof EmobilityPerson) {
			assertEmobilityValuesOf((EmobilityPerson) person, (EmobilityPerson) originalPerson);
		}
	}

	private void assertEmobilityValuesOf(EmobilityPerson person, EmobilityPerson originalPerson) {
		assertValue(EmobilityPerson::eMobilityAcceptance, person, originalPerson);
		assertValue(EmobilityPerson::chargingInfluencesDestinantionChoice, person, originalPerson);
		assertValue(EmobilityPerson::carSharingCustomership, person, originalPerson);
	}

	private List<String> personFormat() {
		return asList( 
				valueOf(personOid), 
				valueOf(personNumber), 
				valueOf(householdOid),
				valueOf(Example.age), 
				valueOf(Example.employment), 
				valueOf(Example.gender),
				valueOf(Example.income), 
				valueOf(Example.hasBike), 
				valueOf(Example.hasAccessToCar),
				valueOf(Example.hasPersonalCar), 
				valueOf(Example.hasCommuterTicket) 
			);
	}

	private List<String> emobilityPerson() {
		List<String> normalPerson = personFormat();
		List<String> emobilityPerson = new ArrayList<>(normalPerson);
		emobilityPerson.add(valueOf(Example.eMobilityAcceptance));
		emobilityPerson.add(valueOf(Example.chargingInfluencesDestinationChoice));
		emobilityPerson.add(Example.carSharingCustomership().toString());
		return emobilityPerson;
	}
}
