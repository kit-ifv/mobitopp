package edu.kit.ifv.mobitopp.populationsynthesis.region;

import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandRegion;

public class DemographicSelector {

	public Stream<DemandRegion> select(DemandRegion input) {
		if (input.nominalDemography().hasData()) {
			return Stream.of(input);
		}
		return input.parts().stream().flatMap(this::select);
	}

	
}
