package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;

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

	public Collection<PrivateCar> createCars(Household household, int numberOfCars) {

		List<Person> personsWithLicense = getPersonsWithLicense(household);

		List<PrivateCar> cars = new ArrayList<PrivateCar>();


		if (numberOfCars == 0) return cars;

		// Should not happen, but is available in the data
		if (personsWithLicense.size() == 0) {
		
			List<Person> adultPersons = getAdultPersons(household);

			List<Person> personsSample = adultPersons.size() > numberOfCars 
																				? sample(household.getPersons(), numberOfCars)
																				: sampleWithReplacement(household.getPersons(), numberOfCars);

			for (int i=0; i<numberOfCars; i++) {
				cars.add(createCar(personsSample.get(i)));
			}

			return cars;
		}


		if (personsWithLicense.size() == numberOfCars) {

			for (int i=0; i<numberOfCars; i++) {
				PrivateCar car = createPersonalCar(personsWithLicense.get(i));
				cars.add(car);
			}

		} else if (personsWithLicense.size() > numberOfCars) {

			List<Person> personsSample = sample(personsWithLicense, numberOfCars);

			for (int i=0; i<numberOfCars; i++) {
				Person person = personsSample.get(i);

				if (person.hasPersonalCar()) {
					cars.add(createPersonalCar(person));
				} else {
					cars.add(createCar(person));
				}
			}

		} else if (personsWithLicense.size() < numberOfCars) {

			int withLicense = personsWithLicense.size();

			List<Person> personsSample = sampleWithReplacement(personsWithLicense, numberOfCars-withLicense);

			for (int i=0; i<withLicense; i++) {
				PrivateCar car = createPersonalCar(personsWithLicense.get(i));
				cars.add(car);
			}
			for (int i=withLicense; i<numberOfCars; i++) {
				PrivateCar car = createCar(personsSample.get(i-withLicense));
				cars.add(car);
			}

		}

		return cars;
	}

	private List<Person> getPersonsWithLicense(Household household) {

		List<Person> withLicense = new ArrayList<Person>();

		for (Person person : household.getPersons()) {

			if (person.hasDrivingLicense()) {
				withLicense.add(person);
			}
		}

		return withLicense;
	}

	private List<Person> getAdultPersons(Household household) {

		List<Person> adults = new ArrayList<Person>();

		for (Person person : household.getPersons()) {

			if (person.age() >= 18) {
				adults.add(person);
			}
		}

		return adults;
	}

	private	List<Person> sample(
		List<Person> persons, 
		int size
	) {
			assert size < persons.size() : ("size=" + size + " person.size()=" + persons.size());
			assert size >= 1;

			List<Person> sample = new ArrayList<Person>();

			List<Person> tmp = new ArrayList<Person>(persons);

			assert tmp.size() > size;

			for (int i=0; i<size; i++) {

				float rand = random.nextFloat();
				int idx = (int) Math.floor(rand*tmp.size());

				assert idx < tmp.size() : ("idx=" + idx + " " + tmp.size());

				Person p = tmp.remove(idx);
				sample.add(p);
			}

			return sample;
	}

	private	List<Person> sampleWithReplacement(
		List<Person> persons, 
		int size
	) {

			assert size >= 1;
			assert persons.size() >= 1 : ("person.size() ==" + persons.size());

			List<Person> sample = new ArrayList<Person>();

			for (int i=0; i<size; i++) {

				float rand = random.nextFloat();
				int idx = (int) Math.floor(rand*persons.size());

				assert idx < persons.size() : ("idx=" + idx + " " + persons.size());

				Person p = persons.get(idx);
				sample.add(p);
			}

			return sample;
	}

	protected PrivateCar createCar(
		Person person
	) {

		Car.Segment segment = segmentModel.determineCarSegment(person);

		Household household = person.household();

		CarPosition position = new CarPosition(household.homeZone(), household.homeLocation());

		return createCar(person, position, segment, false);
	}

	protected PrivateCar createPersonalCar(Person person) {

		Car.Segment segment = segmentModel.determineCarSegment(person);

		Household household = person.household();

		CarPosition position = new CarPosition(household.homeZone(), household.homeLocation());

		PrivateCar car = createCar(person, position, segment, true);

		person.assignPersonalCar(car);

		return car;
	}

	abstract protected PrivateCar createCar(
		Person person, 
		CarPosition position,
		Car.Segment segment, 
		boolean personal
	);


}
