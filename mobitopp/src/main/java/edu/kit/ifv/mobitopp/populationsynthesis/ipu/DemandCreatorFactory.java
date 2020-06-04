package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import edu.kit.ifv.mobitopp.data.DemandZone;

@FunctionalInterface
public interface DemandCreatorFactory {

	DemandCreator create(DemandZone zone, AttributeResolver attributeResolver);

}