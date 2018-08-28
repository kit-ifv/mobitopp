package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.ExtendedPatternActivity;
import edu.kit.ifv.mobitopp.populationsynthesis.Example;
import edu.kit.ifv.mobitopp.populationsynthesis.OpportunityLocations;
import edu.kit.ifv.mobitopp.populationsynthesis.Population;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;
import edu.kit.ifv.mobitopp.simulation.opportunities.Opportunity;
import edu.kit.ifv.mobitopp.util.ReflectionHelper;

public class DefaultDemandDataSerialiserTest {

	private static final int zoneOid = 1;
	private static Population population;

	private Serialiser<Household> householdSerialiser;
	private ForeignKeySerialiser<Person> personSerialiser;
	private Serialiser<PersonPatternActivity> activitySerialiser;
	private ForeignKeySerialiser<PrivateCar> carSerialiser;
	private ForeignKeySerialiser<PersonFixedDestination> fixedDestinationSerialiser;
	private DefaultDemandDataSerialiser serialiser;
	private Serialiser<Opportunity> opportunitySerialiser;

	@BeforeClass
	public static void resetHouseholdIdSequence() throws ReflectiveOperationException {
		ReflectionHelper.resetHouseholdIdSequence();
		Zone zone = mock(Zone.class);
		population = Example.population(zone);
		when(zone.getOid()).thenReturn(zoneOid);
	}

	@SuppressWarnings("unchecked")
	@Before
	public void initialise() {
		householdSerialiser = mock(Serialiser.class);
		activitySerialiser = mock(Serialiser.class);
		carSerialiser = mock(ForeignKeySerialiser.class);
		personSerialiser = mock(ForeignKeySerialiser.class);
		fixedDestinationSerialiser = mock(ForeignKeySerialiser.class);
		opportunitySerialiser = mock(Serialiser.class);
		serialiser = new DefaultDemandDataSerialiser(householdSerialiser, personSerialiser,
				activitySerialiser, carSerialiser, fixedDestinationSerialiser, opportunitySerialiser);
	}
	
	@Test
	public void closesWriter() throws Exception {
		serialiser.close();
		
		verify(householdSerialiser).close();
		verify(activitySerialiser).close();
		verify(personSerialiser).close();
		verify(carSerialiser).close();
		verify(fixedDestinationSerialiser).close();
		verify(opportunitySerialiser).close();
	}
	
	@Test
	public void serialiseHeaderOfPopulation() {
		serialisePopulation();
		
		verifyWrittenHeader();
	}

	private void verifyWrittenHeader() {
		verify(householdSerialiser).writeHeader();
		verify(activitySerialiser).writeHeader();
		verify(personSerialiser).writeHeader();
		verify(carSerialiser).writeHeader();
		verify(fixedDestinationSerialiser).writeHeader();
	}

	@Test
	public void serialiseHouseholdAttributes() throws IOException {
		serialisePopulation();

		verifyWrittenHousehold();
	}

	private void serialisePopulation() {
		serialiser.serialise(population);
	}

	private void verifyWrittenHousehold() throws IOException {
		for (Household household : population.households()) {
			verify(householdSerialiser).write(household);
		}
	}

	@Test
	public void serialisePersonAttributes() throws IOException {
		serialisePopulation();

		verifyWrittenPersonOfHousehold();
	}

	@Test
	public void serialiseActivityAttributes() throws IOException {
		serialisePopulation();

		verifyWrittenPatternActivityWeek();
	}

	@Test
	public void serialiseCarAttributes() throws IOException {
		serialisePopulation();

		verifyWrittenCars();
	}
	
	@Test
	public void serialiseFixedDestinations() throws IOException {
		serialisePopulation();
		
		verifyWrittenFixedDestinations();
	}

	private void verifyWrittenPersonOfHousehold() throws IOException {
		for (Household household : population.households()) {
			for (Person person : household.getPersons()) {
				verify(personSerialiser).write(person, population);
			}
		}
	}

	private void verifyWrittenPatternActivityWeek() throws IOException {
		for (Household household : population.households()) {
			for (Person person : household.getPersons()) {
				for (ExtendedPatternActivity activity : person.tourBasedActivityPattern().asPatternActivities()) {
					PersonPatternActivity personActivity = new PersonPatternActivity(person.getOid(),
							activity);
					verify(activitySerialiser).write(personActivity);
				}
			}
		}
	}

	private void verifyWrittenCars() throws IOException {
		for (Household household : population.households()) {
			for (PrivateCar car : household.whichCars()) {
				verify(carSerialiser).write(car, population);
			}
		}
	}
	
	private void verifyWrittenFixedDestinations() throws IOException {
		for (Household household : population.households()) {
			for (Person person : household.getPersons()) {
				for (FixedDestination destination : person.getFixedDestinations()) {
					PersonFixedDestination personDestination = new PersonFixedDestination(person, destination);
					verify(fixedDestinationSerialiser).write(personDestination, population);
				}
			}
		}
	}
	
	@Test
	public void serialisesOpportunities() {
		OpportunityLocations opportunities = mock(OpportunityLocations.class);
		
		serialiser.serialise(opportunities);
		
		verify(opportunities).forEach(any());
		verify(opportunitySerialiser).writeHeader();
	}
}
