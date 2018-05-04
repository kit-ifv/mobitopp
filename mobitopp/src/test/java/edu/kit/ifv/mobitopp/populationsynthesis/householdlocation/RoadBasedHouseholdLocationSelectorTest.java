package edu.kit.ifv.mobitopp.populationsynthesis.householdlocation;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.network.Edge;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.network.Zone;
import edu.kit.ifv.mobitopp.network.ZoneArea;
import edu.kit.ifv.mobitopp.populationsynthesis.SynthesisContext;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.util.ReflectionHelper;

public class RoadBasedHouseholdLocationSelectorTest {

	private static final long seed = 1;
	private static final double maxDistance = 2.0d;

	private RoadBasedHouseholdLocationSelector selector;
	private EdgeFilter edgeFilter;
	private SimpleRoadNetwork network;
	private SynthesisContext context;

	@Before
	public void initialise() throws NoSuchFieldException, SecurityException, IllegalAccessException {
		ReflectionHelper.clearTransportSystemSetCache();
		edgeFilter = mock(EdgeFilter.class);
		context = mock(SynthesisContext.class);
		network = Example.createNetwork();
		when(context.roadNetwork()).thenReturn(network);
		when(context.seed()).thenReturn(seed);

		selector = newSelector();
	}

	@Test
	public void selectsLocation() {
		Zone zone = mock(Zone.class);
		ZoneArea zoneArea = mock(ZoneArea.class);
		int id = 1;
		when(zone.id()).thenReturn(id);
		when(zone.totalArea()).thenReturn(zoneArea);
		when(zoneArea.contains(any())).thenReturn(true);
		List<Edge> edges = edges();
		when(edgeFilter.filter(any())).thenReturn(edges);

		Location location = selector.selectLocation(zone);

		assertThat(location.coordinate.getX(), is(closeTo(2.417d, 1e-3)));
		assertThat(location.coordinate.getY(), is(closeTo(4.124d, 1e-3)));
	}

	private List<Edge> edges() {
		return asList(network.edge(Example.anotherLinkId));
	}

	private RoadBasedHouseholdLocationSelector newSelector() {
		return new RoadBasedHouseholdLocationSelector(context, maxDistance, edgeFilter);
	}
}
