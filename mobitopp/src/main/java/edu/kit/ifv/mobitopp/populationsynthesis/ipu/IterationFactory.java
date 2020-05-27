package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import edu.kit.ifv.mobitopp.data.DemandRegion;

public interface IterationFactory {

	Iteration createIterationFor(DemandRegion region);

	AttributeResolver createAttributeResolverFor(DemandRegion region);

}