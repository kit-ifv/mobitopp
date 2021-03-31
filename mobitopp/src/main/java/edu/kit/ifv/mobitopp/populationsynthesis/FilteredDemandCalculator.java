package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.populationsynthesis.region.ZoneBasedRegionPredicate;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;

/**
 * Use {@link Predicate} to select the {@link DemandRegion}s to process.
 * 
 * @author Lars Briem
 *
 */
public class FilteredDemandCalculator implements DemandDataForDemandRegionCalculator {

	private final DemandDataForDemandRegionCalculator other;
	private final Predicate<DemandRegion> predicate;

	public FilteredDemandCalculator(final DemandDataForDemandRegionCalculator other,
		final Predicate<DemandRegion> predicate) {
		this.other = other;
		this.predicate = predicate;
	}

	/**
	 * Process all zones configured as
	 * 
	 * <pre>
	 * generatePopulation
	 * </pre>
	 * 
	 * @param other {@link DemandDataForDemandRegionCalculator} to execute if
	 *              {@link Predicate} matches
	 */
	public FilteredDemandCalculator(DemandDataForDemandRegionCalculator other) {
		this(other, new ZoneBasedRegionPredicate());
	}

	@Override
	public void calculateDemandData(DemandRegion region, ImpedanceIfc impedance) {
		if (predicate.test(region)) {
			other.calculateDemandData(region, impedance);
		}
	}

}
