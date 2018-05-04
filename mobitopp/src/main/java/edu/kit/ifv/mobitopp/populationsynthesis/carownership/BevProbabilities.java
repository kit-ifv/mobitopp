package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import edu.kit.ifv.mobitopp.simulation.Car.Segment;
import edu.kit.ifv.mobitopp.util.ParameterFileParser;

public class BevProbabilities {

	private final Double PROBABILITY_BEV_SMALL = null;
	private final Double PROBABILITY_BEV_MIDSIZE = null;
	private final Double PROBABILITY_BEV_LARGE = null;

	public BevProbabilities(String configFile) {
		super();

		new ParameterFileParser().parseConfig(configFile, this);
	}

	public double large() {
		return PROBABILITY_BEV_LARGE;
	}

	public double midSize() {
		return PROBABILITY_BEV_MIDSIZE;
	}

	public double small() {
		return PROBABILITY_BEV_SMALL;
	}

	public CarTypeSelector createFor(Segment segment, double probabilityElectricCar) {
		return new SegmentProbabilities(segment, probabilityElectricCar , this);
	}
}
