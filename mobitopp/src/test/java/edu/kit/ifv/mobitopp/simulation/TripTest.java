package edu.kit.ifv.mobitopp.simulation;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.time.Time;

public class TripTest {

	private Trip trip;
	private Time startOfTrip;
  private ActivityIfc previousActivity;
  private ActivityIfc nextActivity;

	@BeforeEach
	public void initialise() {
		startOfTrip = Data.someTime().plusMinutes(1);
		int id = 0;
		Mode mode = Mode.CAR;
		short duration = 0;
		previousActivity = mock(ActivityIfc.class);
		nextActivity = mock(ActivityIfc.class);
		Zone zone = mock(Zone.class);
		when(previousActivity.zone()).thenReturn(zone);
		when(previousActivity.calculatePlannedEndDate()).thenReturn(startOfTrip);
		when(nextActivity.zone()).thenReturn(zone);
		trip = new Trip(id, mode, previousActivity, nextActivity, duration);
	}

	@Test
	public void nextEvent() {
		assertThat(trip.timeOfNextChange(), isEmpty());
	}

	@Test
	public void setsEndDateOfTrip() {
		FinishedTrip endTrip = trip.finish(startOfTrip);

		assertThat(endTrip.endDate(), is(equalTo(startOfTrip)));
	}
	
	@Test
  void doesNothingToStartATrip() throws Exception {
    ImpedanceIfc impedance = mock(ImpedanceIfc.class);
    trip.allocateVehicle(impedance, startOfTrip);
    
    verifyZeroInteractions(impedance);
    verify(previousActivity).calculatePlannedEndDate();
    verify(previousActivity).zoneAndLocation();
    verify(nextActivity).zoneAndLocation();
    verifyNoMoreInteractions(previousActivity, nextActivity);
  }

}
