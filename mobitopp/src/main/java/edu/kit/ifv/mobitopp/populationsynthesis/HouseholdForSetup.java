package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.HouseholdAttributes;
import edu.kit.ifv.mobitopp.simulation.Location;

public interface HouseholdForSetup {

  void addPerson(PersonBuilder person);

  List<PersonBuilder> getPersons();
  
  Stream<PersonBuilder> persons();

  Household toHousehold();

  HouseholdId getId();

  Zone homeZone();

  Location homeLocation();

  int monthlyIncomeEur();
  
  int incomeClass();

  boolean canChargePrivately();

  void ownCars(Collection<PrivateCarForSetup> cars);
  
  Stream<PrivateCarForSetup> ownedCars();

  int getTotalNumberOfCars();

  int getSize();
  
  int getNumberOfPersonsInAgeRange(int fromIncluding, int toIncluding);

  int nominalSize();

  int numberOfMinors();

  int numberOfNotSimulatedChildren();

	HouseholdAttributes attributes();

}
