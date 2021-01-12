package edu.kit.ifv.mobitopp.populationsynthesis.commutation;

import java.util.function.BiPredicate;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;

public class CommuterRelations {

	public static BiPredicate<DemandRegion, DemandRegion> filterByTravelTime(ImpedanceIfc impedance,
		int maxCommutingTime) {
		return new CommutingTimePredicate(impedance, maxCommutingTime);
	}

}
