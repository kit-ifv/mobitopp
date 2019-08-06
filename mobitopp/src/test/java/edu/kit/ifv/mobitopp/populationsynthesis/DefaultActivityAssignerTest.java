package edu.kit.ifv.mobitopp.populationsynthesis;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.HomeActivity;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.SimpleActivity;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPattern;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelDataId;

public class DefaultActivityAssignerTest {

  @Test
  public void createsActivityProgramForAllPeopleInHousehold() {
    int householdOid = 1;
    short year = 2000;
    long householdNumber = 1;
    int personNumber = 1;
    int personOid = 1;
    PanelDataRepository panelRepository = mock(PanelDataRepository.class);
    PersonOfPanelData panelAlice = mock(PersonOfPanelData.class);
    HouseholdOfPanelDataId householdPanelId = new HouseholdOfPanelDataId(year, householdNumber);
    TourBasedActivityPattern alicesPattern = new TourBasedActivityPattern(asList(new HomeActivity(
        DayOfWeek.MONDAY, SimpleActivity.fromPatternActivity(PatternActivity.WHOLE_WEEK_AT_HOME))));
    PersonBuilder alice = mock(PersonBuilder.class);
    HouseholdForSetup household = mock(HouseholdForSetup.class);
    HouseholdId householdId = new HouseholdId(householdOid, year, householdNumber);
    ActivityScheduleCreator scheduleCreator = mock(ActivityScheduleCreator.class);

    when(panelAlice.getId()).thenReturn(new PersonOfPanelDataId(householdPanelId, personNumber));
    when(panelRepository.getPerson(panelAlice.getId())).thenReturn(panelAlice);
    when(alice.getId()).thenReturn(new PersonId(personOid, householdId, personNumber));
    when(household.getId()).thenReturn(householdId);
    when(household.getPersons()).thenReturn(asList(alice));
    when(scheduleCreator.createActivitySchedule(any(), any(), any()))
        .thenReturn(PatternActivityWeek.WHOLE_WEEK_AT_HOME);

    DefaultActivityAssigner assigner = new DefaultActivityAssigner(panelRepository,
        scheduleCreator);
    assigner.assignActivitySchedule(household);

    verify(alice).setPatternActivityWeek(alicesPattern);
  }
}
