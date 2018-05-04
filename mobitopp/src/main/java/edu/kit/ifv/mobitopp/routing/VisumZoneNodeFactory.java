package edu.kit.ifv.mobitopp.routing;

import edu.kit.ifv.mobitopp.visum.VisumZone;

@FunctionalInterface
public interface VisumZoneNodeFactory {

	Node create(VisumZone zone);
}
