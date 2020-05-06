package edu.kit.ifv.mobitopp.populationsynthesis.fixeddestinations;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.Attractivities;
import edu.kit.ifv.mobitopp.data.ExampleZones;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.community.CommunitySelector;
import edu.kit.ifv.mobitopp.populationsynthesis.community.OdPair;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;

@ExtendWith(MockitoExtension.class)
public class CommunityBasedZoneSelectorTest {

	private static final float randomNumber = 0.42f;
	@Mock
	private PersonBuilder person;
	@Mock
	private CommunitySelector communitySelector;

	@Test
	void doesNothingForMissingZones() throws Exception {
		CommunityBasedZoneSelector selector = newSelector();

		Collection<OdPair> relations = emptyList();
		selector.select(person, relations, randomNumber);

		verifyZeroInteractions(person);
		verifyZeroInteractions(communitySelector);
	}

	@Test
	void assignsZone() throws Exception {
		Zone homeZone = ExampleZones.zoneWithId("1", 0);
		Zone someZone = ExampleZones.zoneWithId("2", 1);
		Zone otherZone = ExampleZones.zoneWithId("3", 2);
		Collection<OdPair> relations = asList(new OdPair(homeZone, someZone),
				new OdPair(homeZone, otherZone));
		when(person.homeZone()).thenReturn(homeZone);
		CommunityBasedZoneSelector selector = newSelector();
		selector.select(person, relations, randomNumber);

		verify(person)
				.addFixedDestination(
						new FixedDestination(ActivityType.WORK, someZone, someZone.centroidLocation()));
		verify(communitySelector).notifyAssignedRelation(homeZone, someZone);
	}
	
	@Test
	void filterZonesWithoutAttractivity() throws Exception {
		Zone homeZone = ExampleZones.zoneWithId("1", 0);
		Zone someZone = ExampleZones.zoneWithAttractivities("2", 1, noAttractivities());
		Zone otherZone = ExampleZones.zoneWithAttractivities("3", 2, noAttractivities());
		Collection<OdPair> relations = asList(new OdPair(homeZone, someZone),
				new OdPair(homeZone, otherZone));
		CommunityBasedZoneSelector selector = newSelector();
		selector.select(person, relations, randomNumber);
		
		verifyZeroInteractions(person, communitySelector);
	}

	private Attractivities noAttractivities() {
		Attractivities attractivities = new Attractivities();
		attractivities.addAttractivity(ActivityType.WORK, 0);
		return attractivities;
	}

	private CommunityBasedZoneSelector newSelector() {
		return new CommunityBasedZoneSelector(communitySelector, () -> 0.42d);
	}
}
