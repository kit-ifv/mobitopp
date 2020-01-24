package edu.kit.ifv.mobitopp.simulation;

import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.populationsynthesis.EconomicalStatus;

public interface BaseHousehold {

	HouseholdId getId();

	Zone homeZone();

	Location homeLocation();

	int monthlyIncomeEur();

	int incomeClass();

	EconomicalStatus economicalStatus();

	int getSize();

	int nominalSize();

	int numberOfNotSimulatedChildren();

	List<? extends BasePerson> getPersons();

	default Stream<? extends BasePerson> persons() {
		return getPersons().stream();
	}

	/**
	 * Returns the nominal number of cars as mentioned in the input data. After a car ownership model
	 * has added cars, this number might not fit. The {@link #getNumberOfOwnedCars()} method returns
	 * the correct number of cars. If you really want to know the number of cars from the input data,
	 * use {@link #getNominalNumberOfCars()}.
	 * 
	 * @deprecated
	 * @see #getNumberOfOwnedCars()
	 * @see #getNominalNumberOfCars()
	 */
	int getTotalNumberOfCars();

	int getNominalNumberOfCars();

	int getNumberOfOwnedCars();

}
