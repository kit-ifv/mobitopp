package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import edu.kit.ifv.mobitopp.data.DemandRegion;

public interface IterationFactory {

	AttributeResolver createAttributeResolverFor(DemandRegion region);

}