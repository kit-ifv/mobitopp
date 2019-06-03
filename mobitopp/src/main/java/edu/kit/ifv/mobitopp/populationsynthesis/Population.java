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

  private final Map<Integer, Household> households;
  private final Map<Integer, Person> persons;
  private final Map<Integer, List<ExtendedPatternActivity>> activityPatterns;
  private final List<PersonFixedDestination> destinations;
  private Map<PersonId, List<FixedDestination>> destinationsCache;

  public Population() {
    super();
    households = new LinkedHashMap<>();
    persons = new LinkedHashMap<>();
    activityPatterns = new LinkedHashMap<>();
    destinations = new ArrayList<>();
  }

  public static Population empty() {
    return new Population();
  }

  public Collection<Integer> householdOids() {
    return Collections.unmodifiableCollection(households.keySet());
  }

  public void add(Household household) {
    households.put(household.getOid(), household);
    for (Person person : household.getPersons()) {
      add(person);
    }
  }

  public void add(Person person) {
    persons.put(person.getOid(), person);
  }

  @Override
  public Optional<Household> getHouseholdByOid(int householdOid) {
    return Optional.ofNullable(households.get(householdOid));
  }

  public Stream<Household> households() {
    return households.values().stream();
  }

  public void removeHousehold(int oid) {
    Household household = households.remove(oid);
    household.getPersons().forEach(person -> persons.remove(person.getOid()));
  }

  public Collection<Integer> getPersonOids() {
    return Collections.unmodifiableCollection(persons.keySet());
  }

  @Override
  public Optional<Person> getPersonByOid(int id) {
    return Optional.ofNullable(persons.get(id));
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

  public Person getPerson(PersonId id) {
    return persons.get(id.getOid());
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
