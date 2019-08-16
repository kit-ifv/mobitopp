package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.ExtendedPatternActivity;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPattern;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.OpportunityLocations;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.PrivateCarForSetup;
import edu.kit.ifv.mobitopp.simulation.opportunities.Opportunity;

class DefaultDemandDataSerialiser implements DemandDataSerialiser {

	private final Serialiser<HouseholdForSetup> householdSerialiser;
	private final ForeignKeySerialiser<PersonBuilder> personSerialiser;
	private final Serialiser<PersonPatternActivity> activitySerialiser;
	private final ForeignKeySerialiser<PrivateCarForSetup> carSerialiser;
	private final Serialiser<PersonFixedDestination> fixedDestinationSerialiser;
	private final Serialiser<Opportunity> opportunitySerialiser;

	DefaultDemandDataSerialiser(
			Serialiser<HouseholdForSetup> householdSerialiser,
			ForeignKeySerialiser<PersonBuilder> personSerialiser,
			Serialiser<PersonPatternActivity> activitySerialiser,
			ForeignKeySerialiser<PrivateCarForSetup> carSerialiser,
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
	public void serialise(Collection<HouseholdForSetup> households) {
		for (HouseholdForSetup household : households) {
			serialise(household);
		}
	}

	@Override
	public void serialise(HouseholdForSetup household) {
		write(household);
		writePersonsOf(household);
		writeCarsOf(household);
	}

	private void write(HouseholdForSetup household) {
		householdSerialiser.write(household);
	}

	private void writeCarsOf(HouseholdForSetup household) {
		household.ownedCars().forEach(carSerialiser::write);
	}

	private void writePersonsOf(HouseholdForSetup household) {
		for (PersonBuilder person : household.getPersons()) {
			write(person);
			writeTourbasedActivityPattern(person);
			writeFixedDestinationsOf(person);
		}
	}

	private void write(PersonBuilder person) {
		personSerialiser.write(person);
	}

	private void writeTourbasedActivityPattern(PersonBuilder person) {
    writeActivities(person, person.getActivityPattern());
	}

  private void writeActivities(PersonBuilder person, TourBasedActivityPattern patternActivityWeek) {
    List<ExtendedPatternActivity> patternActivities = patternActivityWeek.asPatternActivities();
		for (ExtendedPatternActivity patternActivity : patternActivities) {
			write(person, patternActivity);
		}
  }

	private void write(PersonBuilder person, ExtendedPatternActivity activity) {
		PersonPatternActivity personActivity = new PersonPatternActivity(person.getId().getOid(),
				activity);
		activitySerialiser.write(personActivity);
	}

  private void writeFixedDestinationsOf(PersonBuilder person) {
    person
        .fixedDestinations()
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
