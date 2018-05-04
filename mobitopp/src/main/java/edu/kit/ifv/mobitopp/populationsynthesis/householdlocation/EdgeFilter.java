package edu.kit.ifv.mobitopp.populationsynthesis.householdlocation;

import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.network.Edge;

public interface EdgeFilter {

	List<Edge> filter(Collection<Edge> edges);

}