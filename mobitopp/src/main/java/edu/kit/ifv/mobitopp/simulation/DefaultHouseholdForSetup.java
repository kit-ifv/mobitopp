package edu.kit.ifv.mobitopp.simulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonForSetup;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;

public class DefaultHouseholdForSetup implements HouseholdForSetup {

  private final HouseholdId householdId;
  private final int nominalSize;
  private final int domcode;
  private final Zone homeZone;
  private final Location homeLocation;
  private final int numberOfMinors;
  private final int numberOfNotSimulatedChildren;
  private final int totalNumberOfCars;
  private final int income;
  private final boolean canChargePrivately;
  private final List<PersonForSetup> persons;
  private final List<PrivateCar> ownedCars;

  public DefaultHouseholdForSetup(
      HouseholdId householdId, int nominalSize, int domcode, Zone zone, Location location,
      int numberOfMinors, int numberOfNotSimulatedChildren, int totalNumberOfCars, int income,
      boolean canChargePrivately) {
    super();
    this.householdId = householdId;
    this.nominalSize = nominalSize;
    this.domcode = domcode;
    this.homeZone = zone;
    this.homeLocation = location;
    this.numberOfMinors = numberOfMinors;
    this.numberOfNotSimulatedChildren = numberOfNotSimulatedChildren;
    this.totalNumberOfCars = totalNumberOfCars;
    this.income = income;
    this.canChargePrivately = canChargePrivately;
    this.persons = new ArrayList<>();
    this.ownedCars = new ArrayList<>();
  }

  @Override
  public void addPerson(PersonForSetup person) {
    persons.add(person);
  }

  @Override
  public List<PersonForSetup> getPersons() {
    return Collections.unmodifiableList(persons);
  }

  @Override
  public Household toHousehold() {
    HouseholdForDemand household = new HouseholdForDemand(householdId, nominalSize, domcode,
        homeZone, homeLocation, numberOfNotSimulatedChildren, totalNumberOfCars, income,
        canChargePrivately);
    persons.stream().map(person -> person.toPerson(household)).forEach(household::addPerson);
    household.ownCars(ownedCars);
    return household;
  }

  @Override
  public HouseholdId getId() {
    return householdId;
  }

  @Override
  public Zone homeZone() {
    return homeZone;
  }

  @Override
  public Location homeLocation() {
    return homeLocation;
  }

  @Override
  public float monthlyIncomeEur() {
    return income;
  }

  @Override
  public boolean canChargePrivately() {
    return canChargePrivately;
  }

  @Override
  public void ownCars(Collection<PrivateCar> cars) {
    assert this.totalNumberOfCars == cars.size();
    ownedCars.addAll(cars);
  }

  @Override
  public int getTotalNumberOfCars() {
    return totalNumberOfCars;
  }

  @Override
  public int nominalNumberOfCars() {
    return totalNumberOfCars;
  }

  @Override
  public int getSize() {
    return getPersons().size() + numberOfNotSimulatedChildren;
  }

  @Override
  public int nominalSize() {
    return nominalSize;
  }

  @Override
  public int numberOfMinors() {
    return numberOfMinors;
  }

  @Override
  public int numberOfNotSimulatedChildren() {
    return numberOfNotSimulatedChildren;
  }
}
