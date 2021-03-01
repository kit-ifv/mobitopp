package edu.kit.ifv.mobitopp.populationsynthesis.region;

import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandRegion;

/**
 * Select all regions with available demographic data.
 * 
 * @author Lars Briem
 *
 */
public class DemographicSelector {

	/**
	 * Return parts of the {@link DemandRegion} which contain demographic
	 * information.
	 * 
	 * @param input region of an upper level
	 * @return if input contains demographic data, return input. Otherwise apply the
	 *         approach to all {@link DemandRegion#parts()} of input.
	 */
	public Stream<DemandRegion> select(DemandRegion input) {
		if (input.nominalDemography().hasData()) {
			return Stream.of(input);
		}
		return input.parts().stream().flatMap(this::select);
	}

}
