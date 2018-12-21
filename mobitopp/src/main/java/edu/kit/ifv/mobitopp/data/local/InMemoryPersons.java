package edu.kit.ifv.mobitopp.data.local;

import static edu.kit.ifv.mobitopp.util.collections.CollectionsHelper.mergeLists;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelDataId;

public class InMemoryPersons {

  private final Map<HouseholdOfPanelDataId, List<PersonOfPanelData>> households;
  private final Map<PersonOfPanelDataId, PersonOfPanelData> persons;

  private InMemoryPersons(
      Map<HouseholdOfPanelDataId, List<PersonOfPanelData>> households,
      Map<PersonOfPanelDataId, PersonOfPanelData> persons) {
    super();
    this.households = households;
    this.persons = persons;
  }

  public static InMemoryPersons createFrom(List<PersonOfPanelData> persons) {
    Map<PersonOfPanelDataId, PersonOfPanelData> personMapping = mapIdsOf(persons);
    Map<HouseholdOfPanelDataId, List<PersonOfPanelData>> households = mapHouseholdsOf(persons);
    return new InMemoryPersons(households, personMapping);
  }

  private static Map<PersonOfPanelDataId, PersonOfPanelData> mapIdsOf(
      List<PersonOfPanelData> persons) {
    return persons.stream().collect(toMap(PersonOfPanelData::getId, Function.identity()));
  }

  private static LinkedHashMap<HouseholdOfPanelDataId, List<PersonOfPanelData>> mapHouseholdsOf(
      List<PersonOfPanelData> persons) {
    LinkedHashMap<HouseholdOfPanelDataId, List<PersonOfPanelData>> assigned = new LinkedHashMap<>();
    for (PersonOfPanelData person : persons) {
      HouseholdOfPanelDataId householdId = person.getId().getHouseholdId();
      assigned.computeIfAbsent(householdId, k -> new ArrayList<>());
      assigned.merge(householdId, asList(person), mergeLists());
    }
    return assigned;
  }

  public List<PersonOfPanelData> getPersonsOfHousehold(HouseholdOfPanelDataId id) {
    List<PersonOfPanelData> persons = this.households.get(id);
    Collections.sort(persons);
    return persons;
  }

  public PersonOfPanelData getPerson(PersonOfPanelDataId id) {
    return persons.get(id);
  }

}
