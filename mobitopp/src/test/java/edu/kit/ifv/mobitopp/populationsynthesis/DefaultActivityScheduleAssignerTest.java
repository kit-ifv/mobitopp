package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

import edu.kit.ifv.mobitopp.data.ExampleZones;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPattern;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPatternCreator;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

public class DefaultActivityScheduleAssignerTest {

  @Test
  public void assignProgramById() {
    Zone zone = ExampleZones.create().someZone();
    HouseholdForSetup household = ExampleHousehold.createHousehold(zone);
    PersonBuilder alice = mock(PersonBuilder.class);
    PersonBuilder bob = mock(PersonBuilder.class);
    when(alice.getId()).thenReturn(new PersonId(1, household.getId(), 1));
    when(bob.getId()).thenReturn(new PersonId(2, household.getId(), 1));
    household.addPerson(alice);
    household.addPerson(bob);

    ActivityScheduleCreator scheduleCreator = mock(ActivityScheduleCreator.class);
    PanelDataRepository panelDataRepository = mock(PanelDataRepository.class);
    HouseholdOfPanelData panelHousehold = mock(HouseholdOfPanelData.class);
    when(panelDataRepository.getHousehold(any())).thenReturn(panelHousehold);
    PersonOfPanelData panelAlice = mock(PersonOfPanelData.class);
    PersonOfPanelData panelBob = mock(PersonOfPanelData.class);
    PatternActivityWeek patternActivityWeek = PatternActivityWeek.WHOLE_WEEK_AT_HOME;
    when(scheduleCreator.createActivitySchedule(any(), any(), any()))
        .thenReturn(patternActivityWeek);
    when(panelDataRepository.getPerson(any())).thenReturn(panelAlice, panelBob);

    ActivityScheduleAssigner assigner = new DefaultActivityScheduleAssigner(scheduleCreator,
        panelDataRepository);

    assigner.assignActivitySchedule(household);

    TourBasedActivityPattern expectedPattern = TourBasedActivityPatternCreator
        .fromPatternActivityWeek(patternActivityWeek);
    verify(alice).setPatternActivityWeek(expectedPattern);
    verify(bob).setPatternActivityWeek(expectedPattern);
  }
}
