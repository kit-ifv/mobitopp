package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.opportunities.OpportunityLocationSelector;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.opportunities.Opportunity;

public class LocationSelectorTest {

	private static final ActivityType activityType = ActivityType.HOME;
	private static final Location location = ExampleSetup.location;
	private static final Location anotherLocation = ExampleSetup.anotherLocation;
	private static final int attractivity = 1;
	private static final int anotherAttractivity = 2;
	private static final int totalOpportunities = 2;
	
	private Opportunity anOpportunity;
	private Opportunity anotherOpportunity;
	private Opportunity sameLocationOpportunity;
	private Zone zone;
	private OpportunityLocationSelector selector;
	
	@Before
	public void initialise() {
		zone = mock(Zone.class);
		anOpportunity = new Opportunity(zone, activityType, location, attractivity);
		anotherOpportunity = new Opportunity(zone, activityType, anotherLocation, anotherAttractivity);
		sameLocationOpportunity = new Opportunity(zone, activityType, location, anotherAttractivity);
		selector = LocationSelector.from(opportunities());
	}

	@Test
	public void createLocationsInZone() {
		Map<Location, Integer> locations = createLocations();
		
		assertThat(locations.get(location), is(attractivity));
		assertThat(locations.get(anotherLocation), is(anotherAttractivity));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void failsForDuplicatedOpportunity() {
		LocationSelector.from(asList(anOpportunity, sameLocationOpportunity));
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
		return selector.createLocations(zone, activityType, totalOpportunities);
	}

	private List<Opportunity> opportunities() {
		return asList(anOpportunity, anotherOpportunity);
	}
}
