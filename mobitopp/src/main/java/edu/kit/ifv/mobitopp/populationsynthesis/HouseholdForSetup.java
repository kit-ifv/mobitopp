package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.simulation.BaseHousehold;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.HouseholdAttributes;

public interface HouseholdForSetup extends BaseHousehold {

	void addPerson(PersonBuilder person);

	List<PersonBuilder> getPersons();

	Stream<PersonBuilder> persons();

	Household toHousehold();

	boolean canChargePrivately();

	void ownCars(Collection<PrivateCarForSetup> cars);

	Stream<PrivateCarForSetup> ownedCars();

	int getNumberOfPersonsInAgeRange(int fromIncluding, int toIncluding);

	int numberOfMinors();

	HouseholdAttributes attributes();

}
