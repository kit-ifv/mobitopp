package edu.kit.ifv.mobitopp.populationsynthesis.householdlocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.network.Edge;
import edu.kit.ifv.mobitopp.network.SimpleEdge;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.network.Zone;
import edu.kit.ifv.mobitopp.util.randomvariable.DiscreteRandomVariable;

public class EdgeDistributions {

	private final EdgeFilter edgeFilter;
	private final Map<Integer, DiscreteRandomVariable<Edge>> edgeDistributions;
	private final SimpleRoadNetwork network;

	public EdgeDistributions(SimpleRoadNetwork network, EdgeFilter edgeFilter) {
		super();
		this.network = network;
		this.edgeFilter = edgeFilter;
		edgeDistributions = new HashMap<>();
	}

	public Edge selectEdgeIn(Zone zone, double random) {
		if (!edgeDistributions.containsKey(zone.id())) {
			createEdgeDistributionFor(zone);
		}
		return findEdgeIn(zone, random);
	}

	private Edge findEdgeIn(Zone zone, double random) {
		DiscreteRandomVariable<Edge> distribution = edgeDistributions.get(zone.id());
		return distribution.realization(random);
	}

	private void createEdgeDistributionFor(Zone zone) {
		Map<Integer, Edge> edges = zone.containedEdges(network);
		List<Edge> filtered = filterEdges(edges.values());
		DiscreteRandomVariable<Edge> distribution = createCumulativeDistribution(filtered);
		edgeDistributions.put(zone.id(), distribution);
	}

	List<Edge> filterEdges(Collection<Edge> edges) {
		return edgeFilter.filter(edges);
	}

	private DiscreteRandomVariable<Edge> createCumulativeDistribution(List<Edge> edges) {
		Map<Edge, Double> lengths = new LinkedHashMap<Edge, Double>();
		for (Edge e : edges) {
			SimpleEdge edge = (SimpleEdge) e;
			double length = edge.length();
			lengths.put(edge, length);
		}
		return new DiscreteRandomVariable<>(lengths);
	}

}
