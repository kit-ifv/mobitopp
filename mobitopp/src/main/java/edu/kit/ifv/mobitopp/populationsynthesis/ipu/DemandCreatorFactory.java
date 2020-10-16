package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import edu.kit.ifv.mobitopp.data.DemandRegion;

@FunctionalInterface
public interface DemandCreatorFactory {

  DemandCreator create(final DemandRegion region, final AttributeResolver attributeResolver);

}