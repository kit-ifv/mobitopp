package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static java.util.stream.Collectors.groupingBy;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.Population;
import edu.kit.ifv.mobitopp.populationsynthesis.opportunities.OpportunityLocationSelector;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;
import edu.kit.ifv.mobitopp.simulation.opportunities.Opportunity;

class DefaultDemandDataDeserialiser implements DemandDataDeserialiser {

  private final Deserialiser<Household> households;
  private final Deserialiser<PersonPatternActivity> activities;
  private final Deserialiser<PersonFixedDestination> fixedDestinationDeserialiser;
  private final ForeignKeyDeserialiser<Person> personDeserialiser;
  private final ForeignKeyDeserialiser<PrivateCar> carDeserialiser;
  private final Deserialiser<Opportunity> opportunityDeserialiser;

  DefaultDemandDataDeserialiser(
      Deserialiser<Household> households, ForeignKeyDeserialiser<Person> personDeserialiser,
      Deserialiser<PersonPatternActivity> activities,
      ForeignKeyDeserialiser<PrivateCar> carDeserialiser,
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
    loadPatterns(population);
    loadHouseholds(population);
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
    for (Household household : households.deserialise()) {
      population.add(household);
    }
  }

  private void loadPersons(Population population) throws IOException {
    System.out.println("Load persons");
    List<Person> persons = personDeserialiser.deserialise(population);
    for (Person person : persons) {
      population.add(person);
    }
  }

  private void loadCars(Population population) throws IOException {
    System.out.println("Load cars");
    List<PrivateCar> cars = carDeserialiser.deserialise(population);
    Consumer<Entry<Household, List<PrivateCar>>> assignCars = entry -> assignCars(entry);
    cars.stream().collect(groupingBy(PrivateCar::owner)).entrySet().stream().forEach(assignCars);
    cars
        .stream()
        .filter(PrivateCar::isPersonal)
        .forEach(car -> assignPersonalCar(car, population));
  }

  private void assignPersonalCar(PrivateCar car, Population population) {
    population.getPerson(car.personalUser()).assignPersonalCar(car);
  }

  private void assignCars(Entry<Household, List<PrivateCar>> entry) {
    entry.getKey().ownCars(entry.getValue());
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
