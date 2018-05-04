package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import static edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarType.bev;
import static edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarType.erev;
import static edu.kit.ifv.mobitopp.simulation.Car.Segment.LARGE;
import static edu.kit.ifv.mobitopp.simulation.Car.Segment.MIDSIZE;

import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;
import edu.kit.ifv.mobitopp.simulation.car.BatteryElectricCar;
import edu.kit.ifv.mobitopp.simulation.car.ExtendedRangeElectricCar;
import edu.kit.ifv.mobitopp.simulation.car.ConventionalCar;
import edu.kit.ifv.mobitopp.simulation.car.DefaultPrivateCar;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.util.ParameterFileParser;

public class GenericElectricCarOwnershipModel
	extends PersonalCarOwnershipModel
	implements CarOwnershipModel
{

	private static final float maximumEnergyLevel = 1.0f;

	ProbabilityForElectricCarOwnershipModel probabilityCalculator;

	private final Integer SMALL_BEV_RANGE = null;
	private final Double  SMALL_BEV_BATTERY = null;
	private final Double  SMALL_BEV_MINIMUM_CHARGING_LEVEL = null;
	private final Double  SMALL_BEV_MINIMUM_BATTERY_LEVEL = null;

	private final Integer MIDSIZE_BEV_RANGE = null;
	private final Double  MIDSIZE_BEV_BATTERY = null;
	private final Double  MIDSIZE_BEV_MINIMUM_CHARGING_LEVEL = null;
	private final Double  MIDSIZE_BEV_MINIMUM_BATTERY_LEVEL = null;

	private final Integer LARGE_BEV_RANGE = null;
	private final Double  LARGE_BEV_BATTERY = null;
	private final Double  LARGE_BEV_MINIMUM_CHARGING_LEVEL = null;
	private final Double  LARGE_BEV_MINIMUM_BATTERY_LEVEL = null;

	private final Integer SMALL_EREV_RANGE = null;
	private final Double  SMALL_EREV_BATTERY = null;
	private final Double  SMALL_EREV_MINIMUM_CHARGING_LEVEL = null;
	private final Double  SMALL_EREV_MINIMUM_BATTERY_LEVEL = null;

	private final Integer MIDSIZE_EREV_RANGE = null;
	private final Double  MIDSIZE_EREV_BATTERY = null;
	private final Double  MIDSIZE_EREV_MINIMUM_CHARGING_LEVEL = null;
	private final Double  MIDSIZE_EREV_MINIMUM_BATTERY_LEVEL = null;

	private final Integer LARGE_EREV_RANGE = null;
	private final Double  LARGE_EREV_BATTERY = null;
	private final Double  LARGE_EREV_MINIMUM_CHARGING_LEVEL = null;
	private final Double  LARGE_EREV_MINIMUM_BATTERY_LEVEL = null;

	private final Integer SMALL_EREV_TOTAL_RANGE = null;
	private final Integer MIDSIZE_EREV_TOTAL_RANGE = null;
	private final Integer LARGE_EREV_TOTAL_RANGE = null;


	public GenericElectricCarOwnershipModel(
		IdSequence idSequence,
		CarSegmentModel segmentModel,
		long seed,
		ProbabilityForElectricCarOwnershipModel probabilityCalculator,
		String configFileCarConfiguration
	) {
		super(idSequence,segmentModel,seed);

		this.probabilityCalculator = probabilityCalculator;

		new ParameterFileParser().parseConfig(configFileCarConfiguration, this);
	}


	@Override
	protected PrivateCar createCar(
		Person person,
		CarPosition position,
		Car.Segment segment,
		boolean personal
	) {

		Person personalUser = personal ? person : null;

		return new DefaultPrivateCar(
						createCarInternal(person, position, segment),
						person.household(),
						person,
						personalUser
					);
	}

	protected Car createCarInternal(
		Person person,
		CarPosition position,
		Car.Segment segment
	) {

		double randomNumber = random.nextDouble();

		CarTypeSelector selector = probabilityCalculator.calculateProbabilities(person, segment);

		CarType carType = selector.carType(randomNumber);
		if (bev.equals(carType)) {
			if (MIDSIZE.equals(segment)) {
				return makeMidsizeBEV(position, segment);
			} else if (LARGE.equals(segment)) {
				return makeLargeBEV(position, segment);
			}
			return makeSmallBEV(position, segment);
		} else if (erev.equals(carType)) {
			if (MIDSIZE.equals(segment)) {
				return makeMidsizeEREV(position, segment);
			} else if (LARGE.equals(segment)) {
				return makeLargeEREV(position, segment);
			}
			return makeSmallEREV(position, segment);
		}
		return makeConventionalCar(position, segment);

	}

	BatteryElectricCar makeSmallBEV(
		CarPosition position,
		Car.Segment segment
	) {
		return new BatteryElectricCar(	idSequence, position, segment,
																		batteryLevel(SMALL_BEV_MINIMUM_BATTERY_LEVEL.floatValue()),
																		SMALL_BEV_RANGE.intValue(),
																		SMALL_BEV_BATTERY.floatValue(),
																		SMALL_BEV_MINIMUM_CHARGING_LEVEL.floatValue());
	}

	protected float batteryLevel(float minimumEnergy) {
		return minimumEnergy + (maximumEnergyLevel - minimumEnergy) * nextFloat();
	}

	float nextFloat() {
		return random.nextFloat();
	}

	BatteryElectricCar makeMidsizeBEV(
		CarPosition position,
		Car.Segment segment
	) {
		return new BatteryElectricCar(	idSequence, position, segment,
																		batteryLevel(MIDSIZE_BEV_MINIMUM_BATTERY_LEVEL.floatValue()),
																		MIDSIZE_BEV_RANGE.intValue(),
																		MIDSIZE_BEV_BATTERY.floatValue(),
																		MIDSIZE_BEV_MINIMUM_CHARGING_LEVEL.floatValue());
	}

	BatteryElectricCar makeLargeBEV(
		CarPosition position,
		Car.Segment segment
	) {
		return new BatteryElectricCar(	idSequence, position, segment,
																		batteryLevel(LARGE_BEV_MINIMUM_BATTERY_LEVEL.floatValue()),
																		LARGE_BEV_RANGE.intValue(),
																		LARGE_BEV_BATTERY.floatValue(),
																		LARGE_BEV_MINIMUM_CHARGING_LEVEL.floatValue());
	}

	ExtendedRangeElectricCar makeSmallEREV(
		CarPosition position,
		Car.Segment segment
	) {
		return new ExtendedRangeElectricCar(idSequence, position, segment,
																				batteryLevel(SMALL_EREV_MINIMUM_BATTERY_LEVEL.floatValue()),
																				SMALL_EREV_RANGE.intValue(),
																				SMALL_EREV_TOTAL_RANGE.intValue()-SMALL_EREV_RANGE.intValue(),
																				SMALL_EREV_TOTAL_RANGE.intValue(),
																				SMALL_EREV_BATTERY.floatValue(),
																				SMALL_EREV_MINIMUM_CHARGING_LEVEL.floatValue());
	}

	ExtendedRangeElectricCar makeMidsizeEREV(
		CarPosition position,
		Car.Segment segment
	) {
		return new ExtendedRangeElectricCar(idSequence, position, segment,
																				batteryLevel(MIDSIZE_EREV_MINIMUM_BATTERY_LEVEL.floatValue()),
																				MIDSIZE_EREV_RANGE.intValue(),
																				MIDSIZE_EREV_TOTAL_RANGE.intValue()-MIDSIZE_EREV_RANGE.intValue(),
																				MIDSIZE_EREV_TOTAL_RANGE.intValue(),
																				MIDSIZE_EREV_BATTERY.floatValue(),
																				MIDSIZE_EREV_MINIMUM_CHARGING_LEVEL.floatValue());
	}

	ExtendedRangeElectricCar makeLargeEREV(
		CarPosition position,
		Car.Segment segment
	) {
		return new ExtendedRangeElectricCar(idSequence, position, segment,
																				batteryLevel(LARGE_EREV_MINIMUM_BATTERY_LEVEL.floatValue()),
																				LARGE_EREV_RANGE.intValue(),
																				LARGE_EREV_TOTAL_RANGE.intValue()-LARGE_EREV_RANGE.intValue(),
																				LARGE_EREV_TOTAL_RANGE.intValue(),
																				LARGE_EREV_BATTERY.floatValue(),
																				LARGE_EREV_MINIMUM_CHARGING_LEVEL.floatValue());
	}


	ConventionalCar makeConventionalCar(CarPosition position, Car.Segment segment) {
		return new ConventionalCar(idSequence, position, segment);
	}


}
