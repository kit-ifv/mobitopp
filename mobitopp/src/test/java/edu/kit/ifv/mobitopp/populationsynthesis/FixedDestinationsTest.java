package edu.kit.ifv.mobitopp.populationsynthesis;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.ExampleZones;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.Location;

public class FixedDestinationsTest {

  private FixedDestinations fixedDestinations;

  @Before
  public void initialise() {
    fixedDestinations = new FixedDestinations();
  }

  @Test
  public void getForActivityType() {
    ActivityType forWork = ActivityType.WORK;
    ActivityType forEducation = ActivityType.EDUCATION;
    FixedDestination workDestination = createDestination(forWork);
    FixedDestination educationDestination = createDestination(forEducation);
    fixedDestinations.add(workDestination);
    fixedDestinations.add(educationDestination);
    FixedDestination returnedWork = fixedDestinations.getDestination(forWork);
    FixedDestination returnedEducation = fixedDestinations.getDestination(forEducation);

    assertThat(returnedWork, is(equalTo(workDestination)));
    assertThat(returnedEducation, is(equalTo(educationDestination)));
  }

  private FixedDestination createDestination(ActivityType forEducation) {
    Zone educationZone = ExampleZones.create().someZone();
    Location educationLocation = educationZone.centroidLocation();
    FixedDestination educationDestination = new FixedDestination(forEducation, educationZone,
        educationLocation);
    return educationDestination;
  }

  @Test(expected = NoSuchElementException.class)
  public void failsForMissingDestination() {
    FixedDestinations fixedDestinations = new FixedDestinations();
    fixedDestinations.getDestination(ActivityType.WORK);
  }

  @Test
  public void hasFixedWorkDestination() {
    FixedDestination destination = createDestination(ActivityType.WORK);
    fixedDestinations.add(destination);

    assertTrue(fixedDestinations.hasFixedDestination());
    assertThat(fixedDestinations.getFixedDestination(), hasValue(destination));
  }

  @Test
  public void hasFixedEducationDestination() {
    FixedDestination destination = createDestination(ActivityType.EDUCATION);
    fixedDestinations.add(destination);

    assertTrue(fixedDestinations.hasFixedDestination());
    assertThat(fixedDestinations.getFixedDestination(), hasValue(destination));
  }

  @Test
  public void doesNotHaveFixedDestination() {
    assertFalse(fixedDestinations.hasFixedDestination());
    assertThat(fixedDestinations.getFixedDestination(), isEmpty());
  }
}
