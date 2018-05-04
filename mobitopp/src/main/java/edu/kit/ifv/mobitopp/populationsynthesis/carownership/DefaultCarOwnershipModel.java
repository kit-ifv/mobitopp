package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;
import edu.kit.ifv.mobitopp.simulation.car.ConventionalCar;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;
import edu.kit.ifv.mobitopp.simulation.car.DefaultPrivateCar;


class DefaultCarOwnershipModel
	extends PersonalCarOwnershipModel 
	implements CarOwnershipModel 
{

	IdSequence idSequence;
	CarSegmentModel segmentModel;

	public DefaultCarOwnershipModel(
		IdSequence idSequence,
		CarSegmentModel segmentModel
	) {

		super(idSequence, segmentModel, 1234);
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
								new ConventionalCar(this.idSequence, position, segment),
								person.household(),
								person,
								personalUser
					);
	}


}
