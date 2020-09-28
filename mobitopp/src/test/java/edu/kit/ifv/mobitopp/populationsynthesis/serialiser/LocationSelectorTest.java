package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.opportunities.OpportunityLocationSelector;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.opportunities.Opportunity;

public class LocationSelectorTest {

	private static final ActivityType activityType = ActivityType.HOME;
	private static final Location location = ExampleSetup.location;
	private static final Location anotherLocation = ExampleSetup.anotherLocation;
	private static final ZoneId zoneId = new ZoneId("1", 0);
	private static final int attractivity = 1;
	private static final int anotherAttractivity = 2;
	private static final int totalOpportunities = 2;

	private Opportunity anOpportunity;
	private Opportunity anotherOpportunity;
	private Opportunity sameLocationOpportunity;
	private Zone zone;
	private OpportunityLocationSelector selector;

	@BeforeEach
	public void initialise() {
		zone = mock(Zone.class);
		when(zone.getId()).thenReturn(zoneId);
		anOpportunity = new Opportunity(zoneId, activityType, location, attractivity);
		anotherOpportunity = new Opportunity(zoneId, activityType, anotherLocation,
				anotherAttractivity);
		sameLocationOpportunity = new Opportunity(zoneId, activityType, location, anotherAttractivity);
		selector = LocationSelector.from(opportunities());
	}

	@Test
	public void createLocationsInZone() {
		Map<Location, Integer> locations = createLocations();

		assertThat(locations.get(location), is(attractivity));
		assertThat(locations.get(anotherLocation), is(anotherAttractivity));
	}

	@Test
	public void failsForDuplicatedOpportunity() {
		assertThrows(IllegalArgumentException.class,
				() -> LocationSelector.from(asList(anOpportunity, sameLocationOpportunity)));
	}

	@Test
	public void preservesOrder() {
		Consumer<Location> consumer = mockedConsumer();
		InOrder inOrder = inOrder(consumer);

		Map<Location, Integer> locations = createLocations();

		locations.keySet().forEach(consumer);

		inOrder.verify(consumer).accept(location);
		inOrder.verify(consumer).accept(anotherLocation);
	}

	@SuppressWarnings("unchecked")
	private <T> Consumer<T> mockedConsumer() {
		return mock(Consumer.class);
	}

	private Map<Location, Integer> createLocations() {
		return selector.createLocations(zoneId, activityType, totalOpportunities);
	}

	private List<Opportunity> opportunities() {
		return asList(anOpportunity, anotherOpportunity);
	}
}
