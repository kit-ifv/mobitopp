package edu.kit.ifv.mobitopp.simulation;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.populationsynthesis.EconomicalStatus;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HouseholdForDemand implements Household, Serializable {

	private static final long serialVersionUID = 1L;
	private static final byte UNDEFINED_BYTE = -1;

	private final HouseholdId id;

	private final int domCode;
	private final int type;

	private final Zone homeZone;
	private final Location homeLocation;

	private final byte numberOfCars;
	private final byte numberOfMinors;
	private final byte numberOfNotSimulatedChildren;

	private final byte nominalSize;

	private final int income;
	private final int incomeClass;
	private final EconomicalStatus economicalStatus;
	private final boolean canChargePrivately;

	private Map<PersonId, Person> persons = new LinkedHashMap<>();

	private List<PrivateCar> availableCars = new ArrayList<PrivateCar>();
	private List<PrivateCar> ownedCars = new ArrayList<PrivateCar>();

	protected static int id_counter = 1;

	public HouseholdForDemand(final HouseholdId id, final int nominalSize, final int domcode,
		final int type, final Zone zone, final Location location, final int numberOfMinors,
		final int numberOfNotSimulatedChildren, final int totalNumberOfCars, final int income,
		final int incomeClass, final EconomicalStatus economicalStatus,
		final boolean canChargePrivately) {
		this.id = id;
		this.domCode = domcode;
		this.type = type;
		this.homeZone = zone;
		this.homeLocation = location;
		this.numberOfMinors = (byte) numberOfMinors;
		this.numberOfNotSimulatedChildren = (byte) numberOfNotSimulatedChildren;
		this.numberOfCars = (byte) totalNumberOfCars;
		this.nominalSize = (byte) nominalSize;
		this.income = income;
		this.incomeClass = incomeClass;
		this.economicalStatus = economicalStatus;
		this.canChargePrivately = canChargePrivately;
	}

	public int nominalNumberOfCars() {
		return this.numberOfCars;
	}

	public int nominalSize() {
		return this.nominalSize;
	}

	private int domCode() {
		return domCode;
	}

	public int type() {
		return type;
	}

	public int numberOfNotSimulatedChildren() {
		assert this.numberOfNotSimulatedChildren != UNDEFINED_BYTE;

		return this.numberOfNotSimulatedChildren;
	}

	public HouseholdId getId() {
		assert this.id != null;

		return this.id;
	}

	public int getOid() {
		return this.id.getOid();
	}

	public Zone homeZone() {
		return this.homeZone;
	}

	public void addPerson(Person person) {
		assert person != null;
		this.persons.put(person.getId(), person);
	}

	public List<Person> getPersons() {
		return new ArrayList<>(this.persons.values());
	}

	@Override
	public Stream<Person> persons() {
		return this.persons.values().stream();
	}

	@Override
	public Person getPerson(PersonId id) {
  	if (persons.containsKey(id)) {
  		return persons.get(id);
  	}
  	throw warn(new IllegalArgumentException("No person found for id: " + id), log);
	}

	public int getSize() {
		assert this.nominalSize == getPersons().size() + numberOfNotSimulatedChildren()
			: (this.nominalSize + "," + getPersons().size() + "," + numberOfNotSimulatedChildren());

		return getPersons().size() + numberOfNotSimulatedChildren();
	}

	@Override
	public boolean canChargePrivately() {
		return canChargePrivately;
	}

	public void returnCar(PrivateCar car) {
		this.availableCars.add(car);
	}

	public void ownCars(Collection<PrivateCar> cars) {

		if (this.numberOfCars != cars.size()) {
			log.warn("amount: " + this.numberOfCars);
			log.warn("size: " + cars.size());
			log.warn(cars.toString());
		}

		assert this.numberOfCars == cars.size();

		for (PrivateCar car : cars) {
			this.availableCars.add(car);
			this.ownedCars.add(car);
		}
	}

	public void removeCar(PrivateCar car) {
		this.availableCars.remove(car);
	}

	public int getNumberOfAvailableCars() {
		return this.availableCars.size();
	}

	public PrivateCar takeAvailableCar(Person person, float tourDistanceKm) {
		assert !availableCars.isEmpty();

		PrivateCar car = nextAvailableCar(person, tourDistanceKm);
		availableCars.remove(car);

		return car;
	}

	public PrivateCar nextAvailableCar(Person person, float tourDistanceKm) {
		assert !availableCars.isEmpty();

		if (person.hasPersonalCarAssigned()) {
			PrivateCar personalCar = person.personalCar();

			boolean available = availableCars.contains(personalCar);

			if (available) {
				return personalCar;
			}
		}

		List<PrivateCar> carsToChooseFrom = findNonPersonalCars(availableCars);

		if (carsToChooseFrom.isEmpty()) {
			carsToChooseFrom = availableCars;
		}

		if (carsToChooseFrom.size() > 1) {
			List<PrivateCar> carsWithSufficientRange = findCarsWithSufficientRange(carsToChooseFrom,
				tourDistanceKm);

			if (!carsWithSufficientRange.isEmpty()) {
				carsToChooseFrom = carsWithSufficientRange;
			}
		}

		return carsToChooseFrom.get(0);
	}

	/**
	 * Check that car is not the personal car of somebody else
	 * 
	 * @param cars
	 * @return
	 */
	private List<PrivateCar> findNonPersonalCars(List<PrivateCar> cars) {

		List<PrivateCar> result = new ArrayList<PrivateCar>();

		for (PrivateCar car : cars) {
			if (!car.isPersonal()) {
				result.add(car);
			}
		}

		return result;
	}

	private List<PrivateCar> findCarsWithSufficientRange(List<PrivateCar> cars, float distance) {

		List<PrivateCar> result = new ArrayList<PrivateCar>();

		for (PrivateCar car : cars) {
			if (car.effectiveRange() >= distance) {
				result.add(car);
			}
		}

		return result;
	}

	@Override
	public int getTotalNumberOfCars() {
		return this.numberOfCars;
	}

	@Override
	public int getNumberOfOwnedCars() {
		return this.numberOfCars;
	}

	public Collection<PrivateCar> whichCars() {
		return Collections.unmodifiableCollection(this.ownedCars);
	}

	@Override
	public Stream<PrivateCar> cars() {
		return this.ownedCars.stream();
	}

	public Location homeLocation() {
		return this.homeLocation;
	}

	public int monthlyIncomeEur() {

		return this.income;
	}

	public int incomeClass() {
		return this.incomeClass;
	}

	@Override
	public EconomicalStatus economicalStatus() {
		return economicalStatus;
	}

	public String forLogging() {

		StringBuffer buffer = new StringBuffer();

		buffer.append("HH; ");
		buffer.append(getOid() + "; ");
		buffer.append(getId().getHouseholdNumber() + "; ");
		buffer.append(getId().getYear() + "; ");
		buffer.append(homeZone().getId().getExternalId() + ";");
		buffer.append(homeLocation().toString() + ";");
		buffer.append(getSize()).append("; ");
		buffer.append(this.domCode).append("; ");
		buffer.append(getTotalNumberOfCars());

		return buffer.toString();
	}

	@Override
	public HouseholdAttributes attributes() {
		return new HouseholdAttributes(getOid(), getId(), nominalSize(), domCode(), type(),
			homeZone(), homeLocation(), numberOfMinors, numberOfNotSimulatedChildren(),
			getTotalNumberOfCars(), monthlyIncomeEur(), incomeClass(), economicalStatus(),
			canChargePrivately());
	}

}
