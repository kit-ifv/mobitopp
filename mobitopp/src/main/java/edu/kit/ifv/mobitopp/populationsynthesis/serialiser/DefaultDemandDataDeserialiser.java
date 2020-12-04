package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static java.util.stream.Collectors.groupingBy;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.Population;
import edu.kit.ifv.mobitopp.populationsynthesis.PrivateCarForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.opportunities.OpportunityLocationSelector;
import edu.kit.ifv.mobitopp.simulation.opportunities.Opportunity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class DefaultDemandDataDeserialiser implements DemandDataDeserialiser {

	private final Deserialiser<HouseholdForSetup> households;
	private final Deserialiser<PersonPatternActivity> activities;
	private final Deserialiser<PersonFixedDestination> fixedDestinationDeserialiser;
	private final ForeignKeyDeserialiser<PersonBuilder> personDeserialiser;
	private final ForeignKeyDeserialiser<PrivateCarForSetup> carDeserialiser;
	private final Deserialiser<Opportunity> opportunityDeserialiser;

	DefaultDemandDataDeserialiser(
			Deserialiser<HouseholdForSetup> households,
			ForeignKeyDeserialiser<PersonBuilder> personDeserialiser,
			Deserialiser<PersonPatternActivity> activities,
			ForeignKeyDeserialiser<PrivateCarForSetup> carDeserialiser,
			Deserialiser<PersonFixedDestination> fixedDestinations,
			Deserialiser<Opportunity> opportunityDeserialiser) {
		super();
		this.households = households;
		this.personDeserialiser = personDeserialiser;
		this.activities = activities;
		this.carDeserialiser = carDeserialiser;
		this.fixedDestinationDeserialiser = fixedDestinations;
		this.opportunityDeserialiser = opportunityDeserialiser;
	}

	@Override
	public Population loadPopulation() throws IOException {
		return loadPopulation(DemandDataDeserialiser.acceptAll());
	}

	@Override
	public Population loadPopulation(Predicate<HouseholdForSetup> householdFilter)
			throws IOException {
		Population population = new Population();
		loadHouseholds(population, householdFilter);
		loadPersons(population);
		loadPatterns(population);
		loadFixedDestinations(population);
		loadCars(population);
		population.cleanCache();
		return population;
	}

	private void loadPatterns(Population population) throws IOException {
		log.info("Load patterns");
		for (PersonPatternActivity patternActivity : activities.deserialise()) {
			population
					.getPersonBuilderByOid(patternActivity.personOid())
					.ifPresent(p -> p
							.addPatternActivity(patternActivity.pattern()));
		}
	}
	
	private void loadHouseholds(Population population, Predicate<HouseholdForSetup> householdFilter)
			throws IOException {
		log.info("Load households");
		households.deserialise().stream().filter(householdFilter).forEach(population::add);
	}

	private void loadPersons(Population population) throws IOException {
		log.info("Load persons");
		List<PersonBuilder> persons = personDeserialiser.deserialise(population);
		for (PersonBuilder person : persons) {
			population.add(person);
		}
	}

	private void loadCars(Population population) throws IOException {
		log.info("Load cars");
		List<PrivateCarForSetup> cars = carDeserialiser.deserialise(population);
		cars
				.stream()
				.collect(groupingBy(PrivateCarForSetup::getOwner))
				.forEach((h, c) -> population
						.getHouseholdForSetupByOid(h.getOid())
						.ifPresent(ho -> ho.ownCars(c)));
	}

	private void loadFixedDestinations(Population population) throws IOException {
		for (PersonFixedDestination destination : fixedDestinationDeserialiser.deserialise()) {
			population
					.getPersonBuilderByOid(destination.person().getOid())
					.ifPresent(p -> p.addFixedDestination(destination.fixedDestination()));
		}
	}

	@Override
	public void addOpportunitiesTo(ZoneRepository zoneRepository) throws IOException {
		List<Opportunity> opportunities = opportunityDeserialiser.deserialise();
		OpportunityLocationSelector fromFile = locationSelectorFrom(opportunities);
		for (Zone zone : zoneRepository.getZones()) {
			zone.getDemandData().opportunities().createLocations(fromFile);
		}
	}

	OpportunityLocationSelector locationSelectorFrom(List<Opportunity> opportunities) {
		return LocationSelector.from(opportunities);
	}

	@Override
	public void close() throws IOException {
		households.close();
		personDeserialiser.close();
		activities.close();
		carDeserialiser.close();
		fixedDestinationDeserialiser.close();
		opportunityDeserialiser.close();
	}
}
