package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.populationsynthesis.DefaultPrivateCarForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.PrivateCarForSetup;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;
import edu.kit.ifv.mobitopp.simulation.car.ConventionalCar;


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
	protected PrivateCarForSetup createCar(
	  PersonBuilder person, 
		CarPosition position,
		Car.Segment segment,
		boolean personal
	) {

		HouseholdId householdId = person.household().getId();
    PersonId personId = person.getId();
    PersonId personalUserId = personal ? person.getId() : null;
    return new DefaultPrivateCarForSetup(
								new ConventionalCar(this.idSequence, position, segment),
								householdId,
								personId,
								personalUserId
					);
	}


}
