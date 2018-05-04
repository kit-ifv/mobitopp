package edu.kit.ifv.mobitopp.populationsynthesis.householdlocation;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.network.Edge;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.network.Zone;
import edu.kit.ifv.mobitopp.network.ZoneArea;
import edu.kit.ifv.mobitopp.util.ReflectionHelper;

public class EdgeDistributionsTest {
	
	private EdgeFilter edgeFilter;
	private EdgeDistributions distributions;
	private SimpleRoadNetwork network;

	@Before
	public void initialise() throws NoSuchFieldException, SecurityException, IllegalAccessException {
		ReflectionHelper.clearTransportSystemSetCache();
		
		edgeFilter = mock(EdgeFilter.class);
		network = Example.createNetwork();
		distributions = new EdgeDistributions(network, edgeFilter);
	}

	@Test
	public void filtersEdges() {
		Collection<Edge> edges = new ArrayList<>();
		List<Edge> filteredEdges = new ArrayList<>();
		when(edgeFilter.filter(edges)).thenReturn(filteredEdges);

		List<Edge> delegated = distributions.filterEdges(edges);

		assertThat(delegated, is(sameInstance(filteredEdges)));
		verify(edgeFilter).filter(edges);
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

		double random = 0.375d;
		Edge edge = distributions.selectEdgeIn(zone, random);
		
		assertThat(edge, is(equalTo(network.edge(Example.someLinkId))));
	}

	private List<Edge> edges() {
		return new ArrayList<>(network.edges());
	}
}
