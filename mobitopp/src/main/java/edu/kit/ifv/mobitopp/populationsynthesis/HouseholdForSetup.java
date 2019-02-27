package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Location;

public interface HouseholdForSetup {

  void addPerson(PersonForSetup person);

  List<PersonForSetup> getPersons();

  Household toHousehold();

  HouseholdId getId();

  Zone homeZone();

  Location homeLocation();

  float monthlyIncomeEur();

  boolean canChargePrivately();

  void ownCars(Collection<PrivateCarForSetup> cars);

  int getTotalNumberOfCars();

  int nominalNumberOfCars();

  int getSize();

  int nominalSize();

  int numberOfMinors();

  int numberOfNotSimulatedChildren();
}
