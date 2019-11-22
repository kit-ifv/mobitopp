package edu.kit.ifv.mobitopp.populationsynthesis.fixeddestinations;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.ExampleZones;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonOfPanelDataBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.community.Community;
import edu.kit.ifv.mobitopp.populationsynthesis.community.OdPair;
import edu.kit.ifv.mobitopp.populationsynthesis.community.OdPairSelector;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelDataId;

@ExtendWith(MockitoExtension.class)
public class PanelDistanceSelectorTest {

	private static final float noDistance = 0.0f;
	private static final float shortDistance = 1.0f;
	private static final float longDistance = 2.0f;
	private static final int householdOid = 0;
	private static final int personNumber = 0;
	@Mock
	private PanelDataRepository dataRepository;
	@Mock
	private OdPairSelector pairSelector;
	@Mock
	private ImpedanceIfc impedance;
	private Zone homeZone;
	private Zone otherZone;
	private HouseholdForSetup household;
	private PersonBuilder person;

	@BeforeEach
	public void beforeEach() {
		homeZone = ExampleZones.create().someZone();
		otherZone = ExampleZones.create().otherZone();
		household = ExampleSetup.household(homeZone, householdOid);
		person = ExampleSetup.personOf(household, personNumber, homeZone);
	}

	@Test
	void selectByMatchingPoleDistance() throws Exception {
		configurePanelPerson();
		configureDefaultDistances();
		configureDefaultOdRelations();
		PanelDistanceSelector selector = newSelector();

		Collection<OdPair> relations = selector.select(person);

		assertThat(relations, contains(new OdPair(homeZone, otherZone)));
	}

	@Test
	void selectByRange() throws Exception {
		int range = 2;
		configurePanelPerson();
		configureDefaultDistances();
		configureDefaultOdRelations();
		PanelDistanceSelector selector = newSelector(range);

		Collection<OdPair> relations = selector.select(person);

		assertThat(relations,
				containsInAnyOrder(new OdPair(homeZone, homeZone), new OdPair(homeZone, otherZone)));
	}

	@Test
	void selectOutOfRange() throws Exception {
		int range = 0;
		configureDistanceTo(otherZone, shortDistance);
		configurePanelPerson();
		configureDefaultDistances();
		configureDefaultOdRelations();
		PanelDistanceSelector selector = newSelector(range);

		Collection<OdPair> relations = selector.select(person);

		assertThat(relations, contains(new OdPair(homeZone, otherZone)));
	}

	@Test
	void failsOnMissingOdPairs() throws Exception {
		when(pairSelector.select(person)).thenReturn(emptyList());

		assertThrows(IllegalArgumentException.class, () -> newSelector().select(person));
	}

	@Test
	void scalesCommuterRelations() throws Exception {
		int numberOfCommuters = 1;
		Community community = mock(Community.class);

		newSelector().scale(community, numberOfCommuters);

		verify(pairSelector).scale(community, numberOfCommuters);
	}

	private PanelDistanceSelector newSelector() {
		return new PanelDistanceSelector(dataRepository, pairSelector, impedance);
	}

	private PanelDistanceSelector newSelector(int range) {
		return new PanelDistanceSelector(dataRepository, pairSelector, impedance, range);
	}

	private void configureDefaultOdRelations() {
		when(pairSelector.select(person))
				.thenReturn(asList(new OdPair(homeZone, homeZone), new OdPair(homeZone, otherZone)));
	}

	private void configureDefaultDistances() {
		configureDistanceTo(homeZone, noDistance);
		configureDistanceTo(otherZone, longDistance);
	}

	private void configurePanelPerson() {
		PersonOfPanelDataId panelId = PersonOfPanelDataId.fromPersonId(person.getId());
		PersonOfPanelData panelPerson = new PersonOfPanelDataBuilder()
				.withId(panelId)
				.withDistanceWork(longDistance)
				.build();
		when(dataRepository.getPerson(panelId)).thenReturn(panelPerson);
	}

	private void configureDistanceTo(Zone zone, float distance) {
		when(impedance.getDistance(homeZone.getId(), zone.getId())).thenReturn(distance);
	}
}
