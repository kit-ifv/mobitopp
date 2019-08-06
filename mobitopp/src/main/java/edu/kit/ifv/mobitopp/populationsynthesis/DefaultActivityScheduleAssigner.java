package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPattern;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPatternCreator;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelDataId;

public class DefaultActivityScheduleAssigner implements ActivityScheduleAssigner {

  private final ActivityScheduleCreator scheduleCreator;
  private final Map<PersonOfPanelDataId, TourBasedActivityPattern> cachedActivityPattern = new LinkedHashMap<>();
  private final PanelDataRepository panelDataRepository;

  public DefaultActivityScheduleAssigner(ActivityScheduleCreator scheduleCreator, PanelDataRepository panelDataRepository) {
    super();
    this.scheduleCreator = scheduleCreator;
    this.panelDataRepository = panelDataRepository;
  }

  @Override
  public void assignActivitySchedule(HouseholdForSetup toHousehold) {
    for (PersonBuilder personInHH : toHousehold.getPersons()) {
      doAssign(toHousehold, personInHH);
    }
  }

  private void doAssign(HouseholdForSetup household, PersonBuilder person) {
    HouseholdOfPanelDataId householdId = createPanelId(household.getId());
    PersonOfPanelDataId personId = createPanelId(person.getId());
    HouseholdOfPanelData panelHousehold = panelDataRepository.getHousehold(householdId);
    PersonOfPanelData panelPerson = panelDataRepository.getPerson(personId);
    PatternActivityWeek activityPattern = this.scheduleCreator
        .createActivitySchedule(panelPerson, panelHousehold, household);

    TourBasedActivityPattern activitySchedule;
    if (cachedActivityPattern.containsKey(panelPerson.getId())) {
      activitySchedule = cachedActivityPattern.get(panelPerson.getId());
    } else {
      activitySchedule = TourBasedActivityPatternCreator.fromPatternActivityWeek(activityPattern);
      cachedActivityPattern.put(panelPerson.getId(), activitySchedule);
    }
    person.setPatternActivityWeek(activitySchedule);
  }

  private PersonOfPanelDataId createPanelId(PersonId id) {
    return new PersonOfPanelDataId(createPanelId(id.getHouseholdId()), id.getPersonNumber());
  }

  private HouseholdOfPanelDataId createPanelId(HouseholdId id) {
    return new HouseholdOfPanelDataId(id.getYear(), id.getHouseholdNumber());
  }

}
