package edu.kit.ifv.mobitopp.simulation;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.simulation.person.BeamedTrip;

public class StatisticResultsTest {

  private StatisticResults statistic;
  private Person person;
  private TripData trip;
  private BeamedTrip finishedTrip;
  
  @Before
  public void initialise() {
    person = mock(Person.class);
    trip = mock(TripData.class);
    finishedTrip = new BeamedTrip(trip, someTime());

    statistic = new StatisticResults();
  }

  @Test
  public void collectsTrips() {
    statistic.notifyEndTrip(person, finishedTrip);

    assertThat(statistic.trips(), contains(finishedTrip));
  }

}
