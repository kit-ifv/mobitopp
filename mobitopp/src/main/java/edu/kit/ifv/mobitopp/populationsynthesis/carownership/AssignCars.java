package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import java.util.Collection;

import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PrivateCarForSetup;

public class AssignCars {

	private final CarOwnershipModel carOwnershipModel;

	public AssignCars(CarOwnershipModel carOwnershipModel) {
		this.carOwnershipModel = carOwnershipModel;
	}
	
	public void assignCars(HouseholdForSetup household) {
		Collection<PrivateCarForSetup> cars = carOwnershipModel.createCars(household);
		household.ownCars(cars);
	}

}
