package edu.kit.ifv.mobitopp.populationsynthesis.region;

import edu.kit.ifv.mobitopp.data.DemandRegion;

@FunctionalInterface
public interface PopulationSynthesisStep {

	void process(DemandRegion region);

}
