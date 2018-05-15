package edu.kit.ifv.mobitopp.simulation;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.time.Time;

public class TripTest {

	@Test
	public void setsEndDateOfTrip() {
		Time currentDate = Data.someTime().plusMinutes(1);
		int id = 0;
		Mode mode = Mode.CAR;
		ActivityIfc previousActivity = mock(ActivityIfc.class);
		ActivityIfc nextActivity = mock(ActivityIfc.class);
		Zone zone = mock(Zone.class);
		when(previousActivity.zone()).thenReturn(zone);
		when(nextActivity.zone()).thenReturn(zone);
		short duration = 0;
		Trip trip = new Trip(id, mode, previousActivity, nextActivity, duration);

		FinishedTrip endTrip = trip.finish(currentDate);

		assertThat(endTrip.endDate(), is(equalTo(currentDate)));
	}
}
