package edu.kit.ifv.mobitopp.simulation.modeChoice;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;

public class ModeAvailabilityModelAddingCarsharingTest {

  private ModeChoiceSetup setup;
  private ImpedanceIfc impedance;
  private Person person;
  private Zone origin;
  private Zone destination;
  private ActivityIfc previousActivity;
  private ActivityIfc nextActivity;
  private Collection<Mode> allModes;
  private ModeAvailabilityModelAddingCarsharing model;

  @BeforeEach
  public void initialise() {
    setup = ModeChoiceSetup.create();
    impedance = setup.impedance;

    person = setup.person;
    origin = setup.origin;
    destination = setup.destination;
    allModes = Set.of(StandardMode.PUBLICTRANSPORT, StandardMode.CARSHARING_FREE);
    when(origin.carSharing().isFreeFloatingCarSharingCarAvailable(any())).thenReturn(true);

    model = new ModeAvailabilityModelAddingCarsharing(impedance);
  }

  public void configureNextActivity(ActivityType home) {
    setup.configureNextActivity(home);
    nextActivity = person.nextActivity();
  }

  public void configureCurrentActivity(ActivityType work) {
    setup.configureCurrentActivity(work);
    previousActivity = person.currentActivity();
  }

  @Test
  void fixesModesToFreeFloating() throws Exception {
    configureCurrentActivity(ActivityType.WORK);
    configureNextActivity(ActivityType.HOME);
    when(person.hasParkedCar()).thenReturn(true);
    when(previousActivity.mode()).thenReturn(Mode.CARSHARING_FREE);

    Set<Mode> filteredModes = filterModes();

    assertThat(filteredModes, contains(Mode.CARSHARING_FREE));
  }

  @Test
  void fixesModesToFreeFloatingAtHome() throws Exception {
    configureCurrentActivity(ActivityType.HOME);
    configureNextActivity(ActivityType.WORK);
    when(person.hasParkedCar()).thenReturn(true);
    when(previousActivity.isModeSet()).thenReturn(true);
    when(previousActivity.mode()).thenReturn(Mode.CARSHARING_FREE);

    Set<Mode> filteredModes = filterModes();

    assertThat(filteredModes, contains(Mode.CARSHARING_FREE));
  }

  public Set<Mode> filterModes() {
    return model
        .filterAvailableModes(person, origin, destination, previousActivity, nextActivity,
            allModes);
  }
}
