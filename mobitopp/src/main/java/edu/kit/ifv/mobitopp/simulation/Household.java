package edu.kit.ifv.mobitopp.simulation;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;

public interface Household extends BaseHousehold {

	void addPerson(Person person);

	List<Person> getPersons();

	Person getPerson(PersonId id);

	default Stream<Person> persons() {
		return getPersons().stream();
	}

	void ownCars(Collection<PrivateCar> cars);

	void returnCar(PrivateCar car);

	Collection<PrivateCar> whichCars();

	default Stream<PrivateCar> cars() {
		return whichCars().stream();
	}

	PrivateCar nextAvailableCar(Person person, float tourDistanceKm);

	PrivateCar takeAvailableCar(Person person, float tourDistanceKm);

	int getNumberOfAvailableCars();

	boolean canChargePrivately();

	int getOid();

	int nominalNumberOfCars();

	default int getNominalNumberOfCars() {
		return nominalNumberOfCars();
	}

	HouseholdAttributes attributes();

}
