package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static java.util.stream.Collectors.groupingBy;

import java.io.IOException;
import java.util.List;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.Population;
import edu.kit.ifv.mobitopp.populationsynthesis.PrivateCarForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.opportunities.OpportunityLocationSelector;
import edu.kit.ifv.mobitopp.simulation.opportunities.Opportunity;

class DefaultDemandDataDeserialiser implements DemandDataDeserialiser {

  private final Deserialiser<HouseholdForSetup> households;
  private final Deserialiser<PersonPatternActivity> activities;
  private final Deserialiser<PersonFixedDestination> fixedDestinationDeserialiser;
  private final ForeignKeyDeserialiser<PersonBuilder> personDeserialiser;
  private final ForeignKeyDeserialiser<PrivateCarForSetup> carDeserialiser;
  private final Deserialiser<Opportunity> opportunityDeserialiser;

  DefaultDemandDataDeserialiser(
      Deserialiser<HouseholdForSetup> households, ForeignKeyDeserialiser<PersonBuilder> personDeserialiser,
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
    Population population = new Population();
    loadHouseholds(population);
    loadPatterns(population);
    loadFixedDestinations(population);
    loadPersons(population);
    loadCars(population);
    population.cleanCache();
    return population;
  }

  private void loadPatterns(Population population) throws IOException {
    System.out.println("Load patterns");
    for (PersonPatternActivity patternActivity : activities.deserialise()) {
      population.add(patternActivity.personOid(), patternActivity.pattern());
    }
  }

  private void loadHouseholds(Population population) throws IOException {
    System.out.println("Load households");
    for (HouseholdForSetup household : households.deserialise()) {
      population.add(household);
    }
  }

  private void loadPersons(Population population) throws IOException {
    System.out.println("Load persons");
    List<PersonBuilder> persons = personDeserialiser.deserialise(population);
    for (PersonBuilder person : persons) {
      population.add(person);
    }
  }

	private void loadCars(Population population) throws IOException {
		System.out.println("Load cars");
		List<PrivateCarForSetup> cars = carDeserialiser.deserialise(population);
		cars
				.stream()
				.collect(groupingBy(PrivateCarForSetup::getOwner))
				.forEach((h, c) -> population
						.getHouseholdForSetupByOid(h.getOid())
						.ifPresent(ho -> ho.ownCars(c)));
	}

  private void loadFixedDestinations(Population population) throws IOException {
    for(PersonFixedDestination destination : fixedDestinationDeserialiser.deserialise()) {
      population.add(destination);
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
