package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonForSetup;
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

	public Collection<PrivateCarForSetup> createCars(HouseholdForSetup household) {
	  int numberOfCars = household.getTotalNumberOfCars();
		List<PersonForSetup> personsWithLicense = getPersonsWithLicense(household);

		List<PrivateCarForSetup> cars = new ArrayList<>();


		if (numberOfCars == 0) return cars;

		// Should not happen, but is available in the data
		if (personsWithLicense.size() == 0) {
		
			List<PersonForSetup> adultPersons = getAdultPersons(household);

			List<PersonForSetup> personsSample = adultPersons.size() > numberOfCars 
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

			List<PersonForSetup> personsSample = sample(personsWithLicense, numberOfCars);

			for (int i=0; i<numberOfCars; i++) {
			  PersonForSetup person = personsSample.get(i);

				if (person.hasPersonalCar()) {
					cars.add(createPersonalCar(person));
				} else {
					cars.add(createCar(person));
				}
			}

		} else if (personsWithLicense.size() < numberOfCars) {

			int withLicense = personsWithLicense.size();

			List<PersonForSetup> personsSample = sampleWithReplacement(personsWithLicense, numberOfCars-withLicense);

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

	private List<PersonForSetup> getPersonsWithLicense(HouseholdForSetup household) {

		List<PersonForSetup> withLicense = new ArrayList<>();

		for (PersonForSetup person : household.getPersons()) {

			if (person.hasDrivingLicense()) {
				withLicense.add(person);
			}
		}

		return withLicense;
	}

	private List<PersonForSetup> getAdultPersons(HouseholdForSetup household) {

		List<PersonForSetup> adults = new ArrayList<>();

		for (PersonForSetup person : household.getPersons()) {

			if (person.age() >= 18) {
				adults.add(person);
			}
		}

		return adults;
	}

	private	List<PersonForSetup> sample(
		List<PersonForSetup> persons, 
		int size
	) {
			assert size < persons.size() : ("size=" + size + " person.size()=" + persons.size());
			assert size >= 1;

			List<PersonForSetup> sample = new ArrayList<>();

			List<PersonForSetup> tmp = new ArrayList<>(persons);

			assert tmp.size() > size;

			for (int i=0; i<size; i++) {

				float rand = random.nextFloat();
				int idx = (int) Math.floor(rand*tmp.size());

				assert idx < tmp.size() : ("idx=" + idx + " " + tmp.size());

				PersonForSetup p = tmp.remove(idx);
				sample.add(p);
			}

			return sample;
	}

	private	List<PersonForSetup> sampleWithReplacement(
		List<PersonForSetup> persons, 
		int size
	) {

			assert size >= 1;
			assert persons.size() >= 1 : ("person.size() ==" + persons.size());

			List<PersonForSetup> sample = new ArrayList<>();

			for (int i=0; i<size; i++) {

				float rand = random.nextFloat();
				int idx = (int) Math.floor(rand*persons.size());

				assert idx < persons.size() : ("idx=" + idx + " " + persons.size());

				PersonForSetup p = persons.get(idx);
				sample.add(p);
			}

			return sample;
	}

	protected PrivateCarForSetup createCar(
	    PersonForSetup person
	) {

		Car.Segment segment = segmentModel.determineCarSegment(person);

		HouseholdForSetup household = person.household();

		CarPosition position = new CarPosition(household.homeZone(), household.homeLocation());

		return createCar(person, position, segment, false);
	}

	protected PrivateCarForSetup createPersonalCar(PersonForSetup person) {
		Car.Segment segment = segmentModel.determineCarSegment(person);
		HouseholdForSetup household = person.household();
		CarPosition position = new CarPosition(household.homeZone(), household.homeLocation());
		return createCar(person, position, segment, true);
	}

	abstract protected PrivateCarForSetup createCar(
	    PersonForSetup person, 
		CarPosition position,
		Car.Segment segment, 
		boolean personal
	);


}
