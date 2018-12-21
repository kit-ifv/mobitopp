package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;

public interface HouseholdForSetup {

  void addPerson(PersonForSetup person);

  List<PersonForSetup> getPersons();

  Household toHousehold();

  int numberOfNotSimulatedChildren();

  HouseholdId getId();

  int getSize();

  void ownCars(Collection<PrivateCar> cars);

  int getTotalNumberOfCars();

  Zone homeZone();

  Location homeLocation();

  boolean canChargePrivately();

  float monthlyIncomeEur();

  int nominalNumberOfCars();

  int nominalSize();

}
