package edu.kit.ifv.mobitopp.simulation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;



public class HouseholdForDemand
  implements Household, Serializable
{

	private static final long serialVersionUID = 1L;
	private static final byte UNDEFINED_BYTE = -1;

  private final HouseholdId id;

  private final int domCode;
  
  private final Zone homeZone;
  private final Location homeLocation;

  private final byte numberOfCars;
  private final byte numberOfNotSimulatedChildren;

  private final byte nominalSize;

	private final int income;
	private final boolean canChargePrivately;
  
  private List<Person> persons =  new ArrayList<>();

  private List<PrivateCar> availableCars = new ArrayList<PrivateCar>();
  private List<PrivateCar> ownedCars = new ArrayList<PrivateCar>();

	protected static int id_counter = 1;


  public HouseholdForDemand(
		HouseholdId id_,
		int nominalSize,
		int domcode,
		Zone zone,
		Location location,
		int numberOfNotSimulatedChildren,
		int totalNumberOfCars,
		int income,
		boolean canChargePrivately
	)
  {
		this.id = id_;

		this.domCode = domcode;
		this.homeZone = zone;
		this.homeLocation = location;
		this.numberOfNotSimulatedChildren = (byte) numberOfNotSimulatedChildren;
		this.numberOfCars = (byte) totalNumberOfCars;
		this.nominalSize = (byte) nominalSize;
		this.income = income;
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

  public int numberOfNotSimulatedChildren()
  {
		assert  this.numberOfNotSimulatedChildren != UNDEFINED_BYTE;

    return this.numberOfNotSimulatedChildren;
  }

 
  public HouseholdId getId()
  {
		assert this.id != null;

    return this.id;
  }

  public int getOid()
  {
    return this.id.getOid();
  }

	public Zone homeZone()
	{
		return this.homeZone;
	}

  public void addPerson(Person person) {
    assert person != null;
    this.persons.add(person);
  }
  
  public List<Person> getPersons()
  {
    return new ArrayList<>(this.persons);
  }

  public int getSize()
  {
    assert this.nominalSize ==  getPersons().size() + numberOfNotSimulatedChildren() :
							(this.nominalSize + "," + getPersons().size() + "," + numberOfNotSimulatedChildren());

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
			System.out.println("amount: " + this.numberOfCars);
			System.out.println("size: " + cars.size());
			System.out.println(cars);
		}

		assert this.numberOfCars == cars.size();

		for (PrivateCar car: cars) {
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

		PrivateCar car =  nextAvailableCar(person, tourDistanceKm);
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
			List<PrivateCar> carsWithSufficientRange = findCarsWithSufficientRange(carsToChooseFrom, tourDistanceKm);

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

	public int getTotalNumberOfCars() {
		return this.numberOfCars;
	}

	public Collection<PrivateCar> whichCars() {
		return Collections.unmodifiableCollection(this.ownedCars);
	}

	public Location homeLocation() {
		return this.homeLocation;
	}

	public int monthlyIncomeEur() {

		return this.income;
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
		return new HouseholdAttributes(getOid(), getId(), nominalSize(), domCode(), homeZone(),
				homeLocation(), numberOfNotSimulatedChildren(), getTotalNumberOfCars(), monthlyIncomeEur(),
				canChargePrivately());
	}

}
