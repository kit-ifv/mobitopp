package edu.kit.ifv.mobitopp.populationsynthesis;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.ExtendedPatternActivity;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPattern;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.PersonFixedDestination;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.PopulationContext;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Person;

public class Population implements PopulationContext, Serializable {

  private static final long serialVersionUID = 1L;

  private final Map<Integer, HouseholdForSetup> householdsForSetup;
  private final Map<Integer, Household> households;
  private final Map<Integer, PersonBuilder> personBuilders;
  private final Map<Integer, Person> persons;
  private final Map<Integer, List<ExtendedPatternActivity>> activityPatterns;
  private final List<PersonFixedDestination> destinations;
  private Map<PersonId, List<FixedDestination>> destinationsCache;

  public Population() {
    super();
    householdsForSetup = new LinkedHashMap<>();
    households = new LinkedHashMap<>();
    personBuilders = new LinkedHashMap<>();
    persons = new LinkedHashMap<>();
    activityPatterns = new LinkedHashMap<>();
    destinations = new ArrayList<>();
  }

  public static Population empty() {
    return new Population();
  }

  public Collection<Integer> householdOids() {
    return Collections.unmodifiableCollection(householdsForSetup.keySet());
  }

  public void add(HouseholdForSetup household) {
    householdsForSetup.put(household.getId().getOid(), household);
    for (PersonBuilder person : household.getPersons()) {
      add(person);
    }
  }

  public void add(PersonBuilder person) {
    personBuilders.put(person.getId().getOid(), person);
  }

  @Override
  public Optional<Household> getHouseholdByOid(int householdOid) {
  	if (households.containsKey(householdOid)) {
  		return Optional.of(households.get(householdOid));
  	}
    Optional<Household> household = getHouseholdForSetupByOid(householdOid).map(HouseholdForSetup::toHousehold);
    household.ifPresent(h -> households.put(h.getId().getOid(), h));
    household.map(h -> h.getPersons()).ifPresent(ps -> ps.forEach(p -> persons.put(p.getId().getOid(), p)));
		return household;
  }

	@Override
	public Optional<HouseholdForSetup> getHouseholdForSetupByOid(int householdOid) {
		return Optional.ofNullable(householdsForSetup.get(householdOid));
	}

  public Stream<HouseholdForSetup> households() {
    return householdsForSetup.values().stream();
  }

  public void removeHousehold(int oid) {
    HouseholdForSetup household = householdsForSetup.remove(oid);
    household.getPersons().forEach(person -> personBuilders.remove(person.getId().getOid()));
  }

  public Collection<Integer> getPersonOids() {
    return Collections.unmodifiableCollection(personBuilders.keySet());
  }

  @Override
  public Optional<Person> getPersonByOid(int id) {
  	if (persons.containsKey(id)) {
  		return Optional.of(persons.get(id));
  	}
  	Optional<PersonBuilder> builder = getPersonBuilderByOid(id);
  	if (builder.isPresent()) {
  		HouseholdId householdId = builder.get().household().getId();
  		Optional<Household> household = getHouseholdByOid(householdId.getOid());
  		return household.map(h -> h.getPerson(builder.get().getId()));
  	}
		return Optional.empty();
  }
  
  @Override
  public Optional<PersonBuilder> getPersonBuilderByOid(int id) {
  	return Optional.ofNullable(personBuilders.get(id));
  }

  @Override
  public TourBasedActivityPattern activityScheduleFor(int oid) {
    return activityScheduleOf(oid);
  }

  private TourBasedActivityPattern activityScheduleOf(int oid) {
    if (activityPatterns.containsKey(oid)) {
      List<ExtendedPatternActivity> activities = activityPatterns.remove(oid);
      // return new PatternActivityWeek(activities);
      return TourBasedActivityPattern.fromExtendedPatternActivities(activities);
    }
    throw new IllegalArgumentException(
        "Could not find PatternActivityWeek for person with oid " + oid);
  }

  public void add(int personOid, ExtendedPatternActivity pattern) {
    if (!activityPatterns.containsKey(personOid)) {
      activityPatterns.put(personOid, new LinkedList<>());
    }
    List<ExtendedPatternActivity> patternWeek = activityPatterns.get(personOid);
    patternWeek.add(pattern);
    activityPatterns.put(personOid, patternWeek);
  }

  public PersonBuilder getPerson(PersonId id) {
    return personBuilders.get(id.getOid());
  }

  public void add(PersonFixedDestination destination) {
    destinations.add(destination);
  }

  @Override
  public Stream<FixedDestination> destinations(PersonId person) {
    if (isCacheMissing()) {
      createDestinationsCache();
    }
    return destinationsCache.getOrDefault(person, emptyList()).stream();
  }

  private boolean isCacheMissing() {
    return destinationsCache == null;
  }

  private void createDestinationsCache() {
    destinationsCache = destinations
        .stream()
        .collect(groupingBy(PersonFixedDestination::person,
            mapping(PersonFixedDestination::fixedDestination, toList())));
  }

  public void cleanCache() {
    if (isCacheMissing()) {
      return;
    }
    destinationsCache.clear();
    destinationsCache = null;
  }
}
