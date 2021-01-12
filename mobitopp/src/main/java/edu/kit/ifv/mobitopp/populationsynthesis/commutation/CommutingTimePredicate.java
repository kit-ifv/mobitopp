package edu.kit.ifv.mobitopp.populationsynthesis.commutation;

import java.util.function.BiPredicate;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CommutingTimePredicate implements BiPredicate<DemandRegion, DemandRegion> {

	private final ImpedanceIfc impedance;
	private final int maxCommutingTime;
	private final Time time;

	/**
	 * Uses {@link Time#start} as time to retrieve the travel time.
	 * 
	 * @param impedance
	 * @param maxCommutingTime
	 */
	public CommutingTimePredicate(ImpedanceIfc impedance, int maxCommutingTime) {
		this(
			impedance,
			maxCommutingTime,
			Time.start);
	}

	@Override
	public boolean test(DemandRegion origin, DemandRegion destination) {
		return matchesTravelTime(origin, destination, StandardMode.CAR, time)
			|| matchesTravelTime(origin, destination, StandardMode.PUBLICTRANSPORT, time);
	}

	private boolean matchesTravelTime(DemandRegion origin, DemandRegion destination, Mode mode,
		Time time) {
		double average = origin
			.zones()
			.flatMapToDouble(originZone -> destination
				.zones()
				.mapToDouble(destinationZone -> impedance
					.getTravelTime(originZone.getId(), destinationZone.getId(), mode, time)))
			.average()
			.orElse(Double.MAX_VALUE);
		return maxCommutingTime > average;
	}

}
