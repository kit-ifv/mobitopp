package edu.kit.ifv.mobitopp.simulation;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.time.Time;

public class TripTest {

	private Trip trip;
	private Time startOfTrip;

	@Before
	public void initialise() {
		startOfTrip = Data.someTime().plusMinutes(1);
		int id = 0;
		Mode mode = Mode.CAR;
		short duration = 0;
		ActivityIfc previousActivity = mock(ActivityIfc.class);
		ActivityIfc nextActivity = mock(ActivityIfc.class);
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

}
