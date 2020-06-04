package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.List;
import java.util.stream.Stream;

@FunctionalInterface
public interface HouseholdReproducer {

	Stream<WeightedHousehold> getHouseholdsToCreate(List<WeightedHousehold> households);

}
