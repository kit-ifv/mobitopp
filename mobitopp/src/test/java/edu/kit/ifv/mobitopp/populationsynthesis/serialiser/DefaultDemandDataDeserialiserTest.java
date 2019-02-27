package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.ExtendedPatternActivity;
import edu.kit.ifv.mobitopp.populationsynthesis.DataForZone;
import edu.kit.ifv.mobitopp.populationsynthesis.Population;
import edu.kit.ifv.mobitopp.populationsynthesis.opportunities.OpportunityLocationSelector;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;
import edu.kit.ifv.mobitopp.simulation.opportunities.Opportunity;
import edu.kit.ifv.mobitopp.simulation.opportunities.OpportunityDataForZone;
import edu.kit.ifv.mobitopp.util.ReflectionHelper;

public class DefaultDemandDataDeserialiserTest {

	private static final int personOid = 1;
  private static final int personNumber = 1;
  private static final int householdOid = 1;
  private static final short householdYear = 2000;
  private static final long householdNumber = 1;
	private Deserialiser<Household> householdDeserialiser;
	private Deserialiser<PersonPatternActivity> activityDeserialiser;
	private ForeignKeyDeserialiser<Person> personDeserialiser;
	private ForeignKeyDeserialiser<PrivateCar> carDeserialiser;
	private Deserialiser<PersonFixedDestination> fixedDestinationDeserialiser;
	private Deserialiser<Opportunity> opportunityDeserialiser;
	
	private HouseholdId householdId;
	private Household household;
	private PersonId personId;
	private Person person;
	private PersonPatternActivity personActivity;
	private ExtendedPatternActivity patternActivity;
	private PrivateCar car;
	private PersonFixedDestination personDestination;
	private FixedDestination fixedDestination;
	private Opportunity opportunity;
	private OpportunityLocationSelector locationSelector;

	@BeforeClass
	public static void resetHouseholdIdSequence() throws ReflectiveOperationException {
		ReflectionHelper.resetHouseholdIdSequence();
	}

	@SuppressWarnings("unchecked")
	@Before
	public void initialise() throws IOException {
	  householdId = new HouseholdId(householdOid, householdYear, householdNumber);
	  household = mock(Household.class);
		personId = new PersonId(personOid, householdId, personNumber);
		person = mock(Person.class);
		personActivity = mock(PersonPatternActivity.class);
		patternActivity = mock(ExtendedPatternActivity.class);
		car = mock(PrivateCar.class);
		fixedDestination = mock(FixedDestination.class);
		personDestination = new PersonFixedDestination(personId, fixedDestination);
		opportunity = mock(Opportunity.class);
		locationSelector = mock(LocationSelector.class);
		activityDeserialiser = mock(Deserialiser.class);
		householdDeserialiser = mock(Deserialiser.class);
		personDeserialiser = mock(ForeignKeyDeserialiser.class);
		carDeserialiser = mock(ForeignKeyDeserialiser.class);
		fixedDestinationDeserialiser = mock(Deserialiser.class);
		opportunityDeserialiser = mock(Deserialiser.class);
		
		when(household.getId()).thenReturn(householdId);
		when(household.getOid()).thenReturn(householdOid);
		when(person.household()).thenReturn(household);
		when(person.getId()).thenReturn(personId);
		when(person.getOid()).thenReturn(personOid);
		when(personActivity.personOid()).thenReturn(personOid);
		when(personActivity.pattern()).thenReturn(patternActivity);
		when(car.owner()).thenReturn(household);
		when(car.isPersonal()).thenReturn(true);
		when(car.personalUser()).thenReturn(personId);
		when(activityDeserialiser.deserialise()).thenReturn(asList(personActivity));
		when(householdDeserialiser.deserialise()).thenReturn(asList(household));
		when(fixedDestinationDeserialiser.deserialise()).thenReturn(asList(personDestination));
		when(personDeserialiser.deserialise(any())).thenReturn(asList(person));
		when(carDeserialiser.deserialise(any())).thenReturn(asList(car));
		when(opportunityDeserialiser.deserialise()).thenReturn(asList(opportunity));
	}
	
	@Test
	public void closesDeserialisers() throws Exception {
		deserialiser().close();
		
		verify(householdDeserialiser).close();
		verify(personDeserialiser).close();
		verify(activityDeserialiser).close();
		verify(carDeserialiser).close();
		verify(fixedDestinationDeserialiser).close();
		verify(opportunityDeserialiser).close();
	}

	@Test
	public void parsePopulation() throws IOException {
		DemandDataDeserialiser deserialiser = deserialiser();

		Population population = deserialiser.loadPopulation();

		assertThat(population.households().collect(toList()), contains(household));
		verify(householdDeserialiser).deserialise();
		verify(personDeserialiser).deserialise(population);
		verify(activityDeserialiser).deserialise();
		verify(personActivity).personOid();
		verify(personActivity).pattern();
		verify(carDeserialiser).deserialise(population);
		verify(household).ownCars(asList(car));
		verify(fixedDestinationDeserialiser).deserialise();
		verify(person).assignPersonalCar(car);
	}
	
	@Test
	public void parseOpportunities() throws IOException {
		Zone zone = mock(Zone.class);
		DataForZone demandData = mock(DataForZone.class);
		OpportunityDataForZone opportunities = mock(OpportunityDataForZone.class);
		ZoneRepository zoneRepository = mock(ZoneRepository.class);
		when(demandData.opportunities()).thenReturn(opportunities);
		when(zone.getDemandData()).thenReturn(demandData);
		when(zoneRepository.getZones()).thenReturn(asList(zone));
		
		deserialiser().addOpportunitiesTo(zoneRepository);
		
		verify(zoneRepository).getZones();
		verify(opportunities).createLocations(any());
	}

	private DemandDataDeserialiser deserialiser() {
		return new DefaultDemandDataDeserialiser(householdDeserialiser, personDeserialiser,
				activityDeserialiser, carDeserialiser, fixedDestinationDeserialiser, opportunityDeserialiser) {
			@Override
			OpportunityLocationSelector locationSelectorFrom(List<Opportunity> opportunities) {
				return locationSelector;
			}
		};
	}

}
