package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.PrivateCarForSetup;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;

abstract class PersonalCarOwnershipModel
	implements CarOwnershipModel 
{

	final IdSequence idSequence;
	final Random random;
	final CarSegmentModel segmentModel;


	public PersonalCarOwnershipModel(
		IdSequence idSequence,
		CarSegmentModel segmentModel,
		long seed
	) {

		this.idSequence = idSequence;
		this.segmentModel = segmentModel;
		this.random = new Random(seed);
	}

	@Override
	public Collection<PrivateCarForSetup> createCars(HouseholdForSetup household) {
	  int numberOfCars = household.getTotalNumberOfCars();
		return createCars(household, numberOfCars);
	}

  public Collection<PrivateCarForSetup> createCars(HouseholdForSetup household, int numberOfCars) {
    List<PersonBuilder> personsWithLicense = getPersonsWithLicense(household);

		List<PrivateCarForSetup> cars = new ArrayList<>();


		if (numberOfCars == 0) return cars;

		// Should not happen, but is available in the data
		if (personsWithLicense.size() == 0) {
		
			List<PersonBuilder> adultPersons = getAdultPersons(household);

			List<PersonBuilder> personsSample = adultPersons.size() > numberOfCars 
																				? sample(household.getPersons(), numberOfCars)
																				: sampleWithReplacement(household.getPersons(), numberOfCars);

			for (int i=0; i<numberOfCars; i++) {
				cars.add(createCar(personsSample.get(i)));
			}

			return cars;
		}


		if (personsWithLicense.size() == numberOfCars) {

			for (int i=0; i<numberOfCars; i++) {
				PrivateCarForSetup car = createPersonalCar(personsWithLicense.get(i));
				cars.add(car);
			}

		} else if (personsWithLicense.size() > numberOfCars) {

			List<PersonBuilder> personsSample = sample(personsWithLicense, numberOfCars);

			for (int i=0; i<numberOfCars; i++) {
			  PersonBuilder person = personsSample.get(i);

				if (person.hasPersonalCar()) {
					cars.add(createPersonalCar(person));
				} else {
					cars.add(createCar(person));
				}
			}

		} else if (personsWithLicense.size() < numberOfCars) {

			int withLicense = personsWithLicense.size();

			List<PersonBuilder> personsSample = sampleWithReplacement(personsWithLicense, numberOfCars-withLicense);

			for (int i=0; i<withLicense; i++) {
				PrivateCarForSetup car = createPersonalCar(personsWithLicense.get(i));
				cars.add(car);
			}
			for (int i=withLicense; i<numberOfCars; i++) {
				PrivateCarForSetup car = createCar(personsSample.get(i-withLicense));
				cars.add(car);
			}

		}

		return cars;
  }

	private List<PersonBuilder> getPersonsWithLicense(HouseholdForSetup household) {

		List<PersonBuilder> withLicense = new ArrayList<>();

		for (PersonBuilder person : household.getPersons()) {

			if (person.hasDrivingLicense()) {
				withLicense.add(person);
			}
		}

		return withLicense;
	}

	private List<PersonBuilder> getAdultPersons(HouseholdForSetup household) {

		List<PersonBuilder> adults = new ArrayList<>();

		for (PersonBuilder person : household.getPersons()) {

			if (person.age() >= 18) {
				adults.add(person);
			}
		}

		return adults;
	}

	private	List<PersonBuilder> sample(
		List<PersonBuilder> persons, 
		int size
	) {
			assert size < persons.size() : ("size=" + size + " person.size()=" + persons.size());
			assert size >= 1;

			List<PersonBuilder> sample = new ArrayList<>();

			List<PersonBuilder> tmp = new ArrayList<>(persons);

			assert tmp.size() > size;

			for (int i=0; i<size; i++) {

				float rand = random.nextFloat();
				int idx = (int) Math.floor(rand*tmp.size());

				assert idx < tmp.size() : ("idx=" + idx + " " + tmp.size());

				PersonBuilder p = tmp.remove(idx);
				sample.add(p);
			}

			return sample;
	}

	private	List<PersonBuilder> sampleWithReplacement(
		List<PersonBuilder> persons, 
		int size
	) {

			assert size >= 1;
			assert persons.size() >= 1 : ("person.size() ==" + persons.size());

			List<PersonBuilder> sample = new ArrayList<>();

			for (int i=0; i<size; i++) {

				float rand = random.nextFloat();
				int idx = (int) Math.floor(rand*persons.size());

				assert idx < persons.size() : ("idx=" + idx + " " + persons.size());

				PersonBuilder p = persons.get(idx);
				sample.add(p);
			}

			return sample;
	}

	protected PrivateCarForSetup createCar(
	    PersonBuilder person
	) {

		Car.Segment segment = segmentModel.determineCarSegment(person);

		HouseholdForSetup household = person.household();

		CarPosition position = new CarPosition(household.homeZone(), household.homeLocation());

		return createCar(person, position, segment, false);
	}

	protected PrivateCarForSetup createPersonalCar(PersonBuilder person) {
		Car.Segment segment = segmentModel.determineCarSegment(person);
		HouseholdForSetup household = person.household();
		CarPosition position = new CarPosition(household.homeZone(), household.homeLocation());
		return createCar(person, position, segment, true);
	}

	abstract protected PrivateCarForSetup createCar(
	    PersonBuilder person, 
		CarPosition position,
		Car.Segment segment, 
		boolean personal
	);


}
