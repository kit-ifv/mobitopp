package edu.kit.ifv.mobitopp.populationsynthesis.householdlocation;

import java.util.Collection;

import edu.kit.ifv.mobitopp.network.Edge;

public interface EdgeFilter {

	EdgeFilter allEdges = edges -> edges;

	Collection<Edge> filter(Collection<Edge> edges);

}