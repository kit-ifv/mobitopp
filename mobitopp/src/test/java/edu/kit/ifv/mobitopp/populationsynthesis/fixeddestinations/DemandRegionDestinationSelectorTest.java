package edu.kit.ifv.mobitopp.populationsynthesis.fixeddestinations;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleDemandZones;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.community.OdPair;
import edu.kit.ifv.mobitopp.populationsynthesis.community.SingleZone;
import edu.kit.ifv.mobitopp.populationsynthesis.region.DemandRegionOdPairSelector;

@ExtendWith(MockitoExtension.class)
public class DemandRegionDestinationSelectorTest {

	private static final double randomNumber = 0.42d;
	@Mock
	private DemandRegionOdPairSelector odPairSelector;
	@Mock
	private HouseholdForSetup household;
	@Mock
	private PersonBuilder somePerson;
	@Mock
	private ZoneSelector zoneSelector;
	@Mock
	private Predicate<PersonBuilder> personFilter;

	private DemandRegion community;
	private Collection<OdPair> relations;

	@BeforeEach
	public void beforeEach() {
		final DemandZone someZone = ExampleDemandZones.create().getSomeZone();
		final DemandZone otherZone = ExampleDemandZones.create().getOtherZone();
		someZone.getPopulation().addHousehold(household);
		community = new SingleZone(someZone);
		relations = createRelation(someZone, otherZone);
		when(household.persons()).then((invocation) -> Stream.of(somePerson));
	}

	@Test
	void selectForEachPerson() throws Exception {
		when(odPairSelector.select(somePerson)).thenReturn(relations);
		acceptPerson();
		final DemandRegionDestinationSelector selector = newSelector();
		selector.process(community);

		verify(odPairSelector).select(somePerson);
		verify(zoneSelector).select(somePerson, relations, randomNumber);
	}

	private DemandRegionDestinationSelector newSelector() {
		return new DemandRegionDestinationSelector(odPairSelector, zoneSelector, personFilter,
				() -> randomNumber);
	}

	@Test
	void filtersPersonsWithWorkActivity() throws Exception {
		rejectPerson();

		final DemandRegionDestinationSelector selector = newSelector();
		selector.process(community);

		verify(personFilter, atLeastOnce()).test(somePerson);
	}

	@Test
	void scalesCommutersToPersonsWithWorkActivity() throws Exception {
		acceptPerson();

		newSelector().process(community);

		verify(odPairSelector).scale(community, 1);
	}

	private void rejectPerson() {
		when(personFilter.test(somePerson)).thenReturn(false);
	}

	private void acceptPerson() {
		when(personFilter.test(somePerson)).thenReturn(true);
	}

	private Collection<OdPair> createRelation(DemandZone homeZone, DemandZone possibleDestination) {
		return asList(new OdPair(homeZone.zone(), possibleDestination.zone()));
	}
}
