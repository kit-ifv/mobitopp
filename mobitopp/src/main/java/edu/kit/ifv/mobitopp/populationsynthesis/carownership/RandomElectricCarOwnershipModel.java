package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonForSetup;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.car.BatteryElectricCar;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;
import edu.kit.ifv.mobitopp.simulation.car.ConventionalCar;
import edu.kit.ifv.mobitopp.simulation.car.DefaultPrivateCar;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;

public class RandomElectricCarOwnershipModel
	extends PersonalCarOwnershipModel 
	implements CarOwnershipModel 
{

	private static final float fullBattery = 1.0f;
	private static final float minimumChargingLevel = 0.5f;
	final float probabilityForElectric;


	public RandomElectricCarOwnershipModel(
		IdSequence idSequence,
		CarSegmentModel segmentModel,
		float probabilityForElectric,
		long seed
	) {
		super(idSequence, segmentModel, seed);

		this.probabilityForElectric = probabilityForElectric;
	}

	
	@Override
	protected PrivateCar createCar(
		PersonForSetup person, 
		CarPosition position,
		Car.Segment segment,
		boolean personal
	) {

		float rand = random.nextFloat();

		Car car;

		if (rand > this.probabilityForElectric) {
				car = new ConventionalCar(this.idSequence, position, segment);
		} else { 
				car = new BatteryElectricCar(this.idSequence, position, segment, fullBattery, 85, 16.0f, minimumChargingLevel);
		}

		HouseholdId householdId = person.household().getId();
    PersonId mainUserId = person.getId();
    PersonId personalId = personal ? person.getId() : null;
    return new DefaultPrivateCar(car, householdId, mainUserId, personalId);
	}


}
