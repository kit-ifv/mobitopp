package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonForSetup;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;
import edu.kit.ifv.mobitopp.simulation.car.ConventionalCar;
import edu.kit.ifv.mobitopp.simulation.car.DefaultPrivateCar;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;


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
	  PersonForSetup person, 
		CarPosition position,
		Car.Segment segment,
		boolean personal
	) {

		HouseholdId householdId = person.household().getId();
    PersonId personId = person.getId();
    PersonId personalUserId = personal ? person.getId() : null;
    return new DefaultPrivateCar(
								new ConventionalCar(this.idSequence, position, segment),
								householdId,
								personId,
								personalUserId
					);
	}


}
