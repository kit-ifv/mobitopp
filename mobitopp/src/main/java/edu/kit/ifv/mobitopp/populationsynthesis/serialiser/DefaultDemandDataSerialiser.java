package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import java.io.IOException;
import java.util.List;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.populationsynthesis.OpportunityLocations;
import edu.kit.ifv.mobitopp.populationsynthesis.Population;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;
import edu.kit.ifv.mobitopp.simulation.opportunities.Opportunity;

class DefaultDemandDataSerialiser implements DemandDataSerialiser {

	private final Serialiser<Household> householdSerialiser;
	private final ForeignKeySerialiser<Person> personSerialiser;
	private final Serialiser<PersonPatternActivity> activitySerialiser;
	private final ForeignKeySerialiser<PrivateCar> carSerialiser;
	private final ForeignKeySerialiser<PersonFixedDestination> fixedDestinationSerialiser;
	private final Serialiser<Opportunity> opportunitySerialiser;

	DefaultDemandDataSerialiser(
			Serialiser<Household> householdSerialiser, ForeignKeySerialiser<Person> personSerialiser,
			Serialiser<PersonPatternActivity> activitySerialiser,
			ForeignKeySerialiser<PrivateCar> carSerialiser,
			ForeignKeySerialiser<PersonFixedDestination> fixedDestinationSerialiser,
			Serialiser<Opportunity> opportunitySerialiser) {
		super();
		this.householdSerialiser = householdSerialiser;
		this.personSerialiser = personSerialiser;
		this.activitySerialiser = activitySerialiser;
		this.carSerialiser = carSerialiser;
		this.fixedDestinationSerialiser = fixedDestinationSerialiser;
		this.opportunitySerialiser = opportunitySerialiser;
	}

	@Override
	public void serialise(Population population) {
		writeHeader();
		writeData(population);
	}

	private void writeHeader() {
		householdSerialiser.writeHeader();
		personSerialiser.writeHeader();
		activitySerialiser.writeHeader();
		carSerialiser.writeHeader();
		fixedDestinationSerialiser.writeHeader();
	}

	private void writeData(Population population) {
		for (Household household : population.households()) {
			write(household);
			writePersonsOf(household, population);
			writeCarsOf(household, population);
		}
	}

	private void write(Household household) {
		householdSerialiser.write(household);
	}

	private void writeCarsOf(Household household, PopulationContext context) {
		for (PrivateCar car : household.whichCars()) {
			carSerialiser.write(car, context);
		}
	}

	private void writePersonsOf(Household household, PopulationContext context) {
		for (Person person : household.getPersons()) {
			write(person, context);
			writePatternActivityWeekOf(person);
			writeFixedDestinationsOf(person, context);
		}
	}

	private void write(Person person, PopulationContext context) {
		personSerialiser.write(person, context);
	}

	private void writePatternActivityWeekOf(Person person) {
		PatternActivityWeek patternActivityWeek = person.getPatternActivityWeek();
		List<PatternActivity> patternActivities = patternActivityWeek.getPatternActivities();
		for (PatternActivity patternActivity : patternActivities) {
			write(person, patternActivity);
		}
	}

	private void write(Person person, PatternActivity activity) {
		PersonPatternActivity personActivity = new PersonPatternActivity(person.getOid(), activity);
		activitySerialiser.write(personActivity);
	}

	private void writeFixedDestinationsOf(Person person, PopulationContext context) {
		for (FixedDestination destination : person.getFixedDestinations()) {
			PersonFixedDestination personDestination = new PersonFixedDestination(person, destination);
			fixedDestinationSerialiser.write(personDestination, context);
		}
	}
	
	@Override
	public void serialise(OpportunityLocations opportunities) {
		opportunitySerialiser.writeHeader();
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
