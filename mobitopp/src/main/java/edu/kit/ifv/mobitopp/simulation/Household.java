package edu.kit.ifv.mobitopp.simulation;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.populationsynthesis.EconomicalStatus;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;

public interface Household
{
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

	int getTotalNumberOfCars();
	int getNumberOfAvailableCars();
	boolean canChargePrivately();

	int getOid();
	HouseholdId getId();

	Zone homeZone();

	int getSize();
	int numberOfNotSimulatedChildren();

	int monthlyIncomeEur();
	int incomeClass();
	EconomicalStatus economicalStatus();
	Location homeLocation();

	int nominalNumberOfCars();
	int nominalSize();
	
	HouseholdAttributes attributes();
}
