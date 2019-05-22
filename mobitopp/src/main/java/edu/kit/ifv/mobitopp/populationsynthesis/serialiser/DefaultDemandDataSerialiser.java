package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.ExtendedPatternActivity;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPattern;
import edu.kit.ifv.mobitopp.populationsynthesis.OpportunityLocations;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;
import edu.kit.ifv.mobitopp.simulation.opportunities.Opportunity;

class DefaultDemandDataSerialiser implements DemandDataSerialiser {

	private final Serialiser<Household> householdSerialiser;
	private final ForeignKeySerialiser<Person> personSerialiser;
	private final Serialiser<PersonPatternActivity> activitySerialiser;
	private final ForeignKeySerialiser<PrivateCar> carSerialiser;
	private final Serialiser<PersonFixedDestination> fixedDestinationSerialiser;
	private final Serialiser<Opportunity> opportunitySerialiser;

	DefaultDemandDataSerialiser(
			Serialiser<Household> householdSerialiser, ForeignKeySerialiser<Person> personSerialiser,
			Serialiser<PersonPatternActivity> activitySerialiser,
			ForeignKeySerialiser<PrivateCar> carSerialiser,
			Serialiser<PersonFixedDestination> fixedDestinationSerialiser,
			Serialiser<Opportunity> opportunitySerialiser) {
		super();
		this.householdSerialiser = householdSerialiser;
		this.personSerialiser = personSerialiser;
		this.activitySerialiser = activitySerialiser;
		this.carSerialiser = carSerialiser;
		this.fixedDestinationSerialiser = fixedDestinationSerialiser;
		this.opportunitySerialiser = opportunitySerialiser;
	}

	void writeHeader() {
		householdSerialiser.writeHeader();
		personSerialiser.writeHeader();
		activitySerialiser.writeHeader();
		carSerialiser.writeHeader();
		fixedDestinationSerialiser.writeHeader();
		opportunitySerialiser.writeHeader();
	}

	@Override
	public void serialise(Collection<Household> households) {
		for (Household household : households) {
			serialise(household);
		}
	}

	@Override
	public void serialise(Household household) {
		write(household);
		writePersonsOf(household);
		writeCarsOf(household);
	}

	private void write(Household household) {
		householdSerialiser.write(household);
	}

	private void writeCarsOf(Household household) {
		for (PrivateCar car : household.whichCars()) {
			carSerialiser.write(car);
		}
	}

	private void writePersonsOf(Household household) {
		for (Person person : household.getPersons()) {
			write(person);
			writeTourbasedActivityPattern(person);
			writeFixedDestinationsOf(person);
		}
	}

	private void write(Person person) {
		personSerialiser.write(person);
	}

	private void writeTourbasedActivityPattern(Person person) {
    person.tourBasedActivityPattern().ifPresent(pattern -> writeActivities(person, pattern));
	}

  private void writeActivities(Person person, TourBasedActivityPattern patternActivityWeek) {
    List<ExtendedPatternActivity> patternActivities = patternActivityWeek.asPatternActivities();
		for (ExtendedPatternActivity patternActivity : patternActivities) {
			write(person, patternActivity);
		}
  }

	private void write(Person person, ExtendedPatternActivity activity) {
		PersonPatternActivity personActivity = new PersonPatternActivity(person.getOid(), activity);
		activitySerialiser.write(personActivity);
	}

  private void writeFixedDestinationsOf(Person person) {
    person
        .getFixedDestinations()
        .map(d -> new PersonFixedDestination(person.getId(), d))
        .forEach(fixedDestinationSerialiser::write);
  }

	public void serialise(OpportunityLocations opportunities) {
		opportunities.forEach(opportunitySerialiser::write);
	}

	@Override
	public void close() throws IOException {
		householdSerialiser.close();
		activitySerialiser.close();
		carSerialiser.close();
		personSerialiser.close();
		fixedDestinationSerialiser.close();
		opportunitySerialiser.close();
	}

}
