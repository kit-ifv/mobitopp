package edu.kit.ifv.mobitopp.simulation;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.populationsynthesis.EconomicalStatus;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.PrivateCarForSetup;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;

public class DefaultHouseholdForSetup implements HouseholdForSetup {

	private final HouseholdId householdId;
	private final int nominalSize;
	private final int domcode;
	private final Zone homeZone;
	private final Location homeLocation;
	private final int numberOfMinors;
	private final int numberOfNotSimulatedChildren;
	private final int nominalNumberOfCars;
	private final int income;
	private final int incomeClass;
	private final EconomicalStatus economicalStatus;
	private final boolean canChargePrivately;
	private final List<PersonBuilder> persons;
	private final List<PrivateCarForSetup> ownedCars;
	private HouseholdForDemand household;

	public DefaultHouseholdForSetup(
			HouseholdId householdId, int nominalSize, int domcode, Zone zone, Location location,
			int numberOfMinors, int numberOfNotSimulatedChildren, int nominalNumberOfCars, int income,
			int incomeClass, EconomicalStatus economicalStatus, boolean canChargePrivately) {
		super();
		this.householdId = householdId;
		this.nominalSize = nominalSize;
		this.domcode = domcode;
		this.homeZone = zone;
		this.homeLocation = location;
		this.numberOfMinors = numberOfMinors;
		this.numberOfNotSimulatedChildren = numberOfNotSimulatedChildren;
		this.nominalNumberOfCars = nominalNumberOfCars;
		this.income = income;
		this.incomeClass = incomeClass;
		this.economicalStatus = economicalStatus;
		this.canChargePrivately = canChargePrivately;
		this.persons = new ArrayList<>();
		this.ownedCars = new ArrayList<>();
	}

	@Override
	public void addPerson(PersonBuilder person) {
		persons.add(person);
	}

	@Override
	public List<PersonBuilder> getPersons() {
		return Collections.unmodifiableList(persons);
	}

	@Override
	public Stream<PersonBuilder> persons() {
		return persons.stream();
	}

	@Override
	public Household toHousehold() {
		household = new HouseholdForDemand(getId(), nominalSize(), domcode, homeZone(), homeLocation(),
				numberOfMinors(), numberOfNotSimulatedChildren(), ownedCars.size(), monthlyIncomeEur(),
				incomeClass(), economicalStatus(), canChargePrivately());
		persons.stream().map(person -> person.toPerson(household)).forEach(household::addPerson);
		List<PrivateCar> cars = ownedCars.stream().map(car -> car.toCar(household)).collect(toList());
		household.ownCars(cars);
		cars.stream().filter(PrivateCar::isPersonal).forEach(this::assignPersonalCar);
		household.homeZone().getDemandData().getPopulationData().addHousehold(household);
		return household;
	}

	private void assignPersonalCar(PrivateCar car) {
		household.getPerson(car.personalUser()).assignPersonalCar(car);
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
	public int monthlyIncomeEur() {
		return income;
	}

	@Override
	public int incomeClass() {
		return incomeClass;
	}

	@Override
	public EconomicalStatus economicalStatus() {
		return economicalStatus;
	}

	@Override
	public boolean canChargePrivately() {
		return canChargePrivately;
	}

	@Override
	public void ownCars(Collection<PrivateCarForSetup> cars) {
		checkNumberOfCars(cars);
		ownedCars.addAll(cars);
	}

	private void checkNumberOfCars(Collection<PrivateCarForSetup> cars) {
		if (this.nominalNumberOfCars != cars.size()) {
			System.out
					.println(String
							.format("Nominal number of cars (%s) deviates from generated number of cars (%s).",
									nominalNumberOfCars, cars.size()));
		}
	}

	@Override
	public Stream<PrivateCarForSetup> ownedCars() {
		return ownedCars.stream();
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
	@Override
	public int getTotalNumberOfCars() {
		return nominalNumberOfCars;
	}
	
	@Override
	public int getNominalNumberOfCars() {
		return nominalNumberOfCars;
	}
	
	@Override
	public int getNumberOfOwnedCars() {
		return ownedCars.size();
	}

	@Override
	public int getSize() {
		return getPersons().size() + numberOfNotSimulatedChildren;
	}

	@Override
	public int getNumberOfPersonsInAgeRange(int fromIncluding, int toIncluding) {
		return Math
				.toIntExact(persons
						.stream()
						.mapToInt(PersonBuilder::age)
						.filter(age -> fromIncluding <= age)
						.filter(age -> toIncluding >= age)
						.count());
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

	@Override
	public HouseholdAttributes attributes() {
		return new HouseholdAttributes(householdId.getOid(), householdId, nominalSize, domcode,
				homeZone, homeLocation, numberOfMinors, numberOfNotSimulatedChildren, getNumberOfOwnedCars(),
				income, incomeClass, economicalStatus, canChargePrivately);
	}

}
