package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import static edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarType.bev;
import static edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarType.conventional;
import static edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarType.erev;

import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Car.Segment;

public class SegmentProbabilities implements CarTypeSelector {

	private final Segment segment;
	private final BevProbabilities bevProbabilities;
	private final double probabilityElectricCar;

	public SegmentProbabilities(
			Segment segment, double probabilityElectricCar, BevProbabilities bevProbabilities) {
		this.segment = segment;
		this.probabilityElectricCar = probabilityElectricCar;
		this.bevProbabilities = bevProbabilities;
	}

	@Override
	public CarType carType(double randomNumber) {
		if (randomNumber < probabilityElectricCar) {
			if (needsSmallCar()) {
				if (randomNumber < probabilityBevSmall()) {
					return bev;
				} else {
					return erev;
				}
			} else if (needsMidsizeCar()) {
				if (randomNumber < probabilityBevMidsize()) {
					return bev;
				} else {
					return erev;
				}
			} else if (needsLargeCar()) {
				if (randomNumber < probabilityBevLarge()) {
					return bev;
				} else {
					return erev;
				}
			} else {
				throw incorrectCarSegment();
			}
		} else {
			return conventional;
		}
	}

	private boolean needsLargeCar() {
		return segment == Car.Segment.LARGE;
	}

	private boolean needsMidsizeCar() {
		return segment == Car.Segment.MIDSIZE;
	}

	private boolean needsSmallCar() {
		return segment == Car.Segment.SMALL;
	}

	private AssertionError incorrectCarSegment() {
		return new AssertionError("Incorrect car segment");
	}

	private double probabilityBevLarge() {
		return probabilityElectricCar * bevProbabilities.large();
	}

	private double probabilityBevMidsize() {
		return probabilityElectricCar * bevProbabilities.midSize();
	}

	private double probabilityBevSmall() {
		return probabilityElectricCar * bevProbabilities.small();
	}

}
