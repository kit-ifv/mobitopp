package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import static edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarType.bev;
import static edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarType.conventional;
import static edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarType.erev;

import java.util.Map;

public class TypeProbabilities implements CarTypeSelector {

	private final Map<CarType, Double> probabilities;

	public TypeProbabilities(Map<CarType, Double> probabilities) {
		super();
		this.probabilities = probabilities;
	}

	@Override
	public CarType carType(double randomNumber) {
		if (randomNumber < probabilityFor(bev)) {
			return bev;
		} else if (randomNumber < probabilityFor(bev) + probabilityFor(erev)) {
			return erev;
		} else {
			return conventional;
		}
	}

	private double probabilityFor(CarType type) {
		return probabilities.get(type);
	}

}
