package edu.kit.ifv.mobitopp.simulation.publictransport;

import edu.kit.ifv.mobitopp.routing.NodeFromVisumNode;
import edu.kit.ifv.mobitopp.visum.VisumNode;

@FunctionalInterface
public interface VisumNodeFactory {

	NodeFromVisumNode create(VisumNode node);
}
