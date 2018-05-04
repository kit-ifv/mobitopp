package edu.kit.ifv.mobitopp.simulation;

import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;

public interface Household
{
	void addPerson(Person person);
	List<Person> getPersons();

	void ownCars(Collection<PrivateCar> cars);
	void returnCar(PrivateCar car);
	Collection<PrivateCar> whichCars();

	PrivateCar nextAvailableCar(Person person, float tourDistanceKm);
	PrivateCar takeAvailableCar(Person person, float tourDistanceKm);

	int getTotalNumberOfCars();
	int getNumberOfAvailableCars();
	boolean canChargePrivately();

	int getOid();
	HouseholdId getId();

	Zone homeZone();

	int getSize();
	int numberOfNotSimulatedChildren();

	int monthlyIncomeEur();
	Location homeLocation();

	int nominalNumberOfCars();
	int nominalSize();
	
	HouseholdAttributes attributes();
}
