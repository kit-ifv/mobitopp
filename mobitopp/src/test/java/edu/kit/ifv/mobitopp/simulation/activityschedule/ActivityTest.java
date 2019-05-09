package edu.kit.ifv.mobitopp.simulation.activityschedule;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assume;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.activityschedule.linkedlist.ActivityAsLinkedListElement;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;

public class ActivityTest {
	
	ActivityIfc fixedActivity;
	ActivityIfc flexibleActivity;
	
	ActivityIfc startFixedActivity;
	ActivityIfc endFixedActivity;
	ActivityIfc durationFixedActivity;
	
	ActivityIfc standardActivity;
	
  private Activity makeActivity(
      int oid, float startFlexibility, float endFlexibility, float durationFlexibility) {
    Time startDate = SimpleTime.of(3, 0, 0, 0);
    int activityNr = 1;
    int tripDuration = 30;
    int duration = 2 * 60;
		Activity act = 	new ActivityAsLinkedListElement(
											oid,
											(byte)activityNr,
											ActivityType.LEISURE,
											startDate,
											duration,
											tripDuration,
											startFlexibility,
											endFlexibility,
											durationFlexibility
										);
    return act;
  }

	@BeforeEach
	public void setUp() {
		
		fixedActivity = makeActivity(1, 0.1f, 0.3f, 0.4f);
		flexibleActivity = makeActivity(2, 0.6f, 0.7f, 0.9f);

		startFixedActivity = makeActivity(1, 0.1f, 0.9f, 0.9f);;
		endFixedActivity = makeActivity(1, 0.9f, 0.1f, 0.9f);;
		durationFixedActivity = makeActivity(1, 0.9f, 0.9f, 0.1f);;

	}


	@Test
	public void testHasFixedStart() {
		
		assertTrue(fixedActivity.hasFixedStart());
		assertFalse(flexibleActivity.hasFixedStart());
		assertTrue(startFixedActivity.hasFixedStart());
		assertFalse(endFixedActivity.hasFixedStart());
		assertFalse(durationFixedActivity.hasFixedStart());	
	}
	
	@Test
	public void testHasFixedEnd() {
		
		assertTrue(fixedActivity.hasFixedEnd());
		assertFalse(flexibleActivity.hasFixedEnd());
		assertFalse(startFixedActivity.hasFixedEnd());
		assertTrue(endFixedActivity.hasFixedEnd());
		assertFalse(durationFixedActivity.hasFixedEnd());
		
	}
	
	@Test
	public void testHasFixedDuration() {
		
		assertTrue(fixedActivity.hasFixedDuration());
		assertFalse(flexibleActivity.hasFixedDuration());
		assertFalse(startFixedActivity.hasFixedDuration());
		assertFalse(endFixedActivity.hasFixedDuration());
		assertTrue(durationFixedActivity.hasFixedDuration());
		
	}
	
	@Test
  void calculatesOriginalEndDate() throws Exception {
    Time originalStart = Time.start;
    int duration = 5;
    Time originalEnd = originalStart.plusMinutes(duration);
    Time someStart = originalStart.plusMinutes(duration);
    Activity activity = makeActivity(originalStart, duration);
    assertThat(activity.calculatePlannedEndDate(), is(equalTo(originalEnd)));
    
    activity.setStartDate(someStart);
  }
	
	@Test
	void calculatesEndDateAfterStartDateChanged() throws Exception {
	  Time originalStart = Time.start;
	  int duration = 5;
	  Time originalEnd = originalStart.plusMinutes(duration);
	  Time someStart = originalStart.plusMinutes(duration);
	  Time someEnd = someStart.plusMinutes(duration);
	  Activity activity = makeActivity(originalStart, duration);
	  Assume.assumeThat(activity.calculatePlannedEndDate(), is(equalTo(originalEnd)));
	  
	  activity.setStartDate(someStart);
	  
	  assertThat(activity.calculatePlannedEndDate(), is(equalTo(someEnd)));
	}
	
	@Test
	void calculatesEndDateAfterDurationChange() throws Exception {
	  Time originalStart = Time.start;
	  int duration = 5;
	  int newDuration = 10;
	  Time originalEnd = originalStart.plusMinutes(duration);
	  Time someEnd = originalStart.plusMinutes(newDuration);
	  Activity activity = makeActivity(originalStart, duration);
	  Assume.assumeThat(activity.calculatePlannedEndDate(), is(equalTo(originalEnd)));
	  
	  activity.changeDuration(newDuration);
	  assertThat(activity.calculatePlannedEndDate(), is(equalTo(someEnd)));
	}

  private Activity makeActivity(Time startDate, int duration) {
    int oid = 1;
    byte activityNr = 1;
    int tripDuration = -1;
    float startFlexibility = 0.0f;
    float endFlexibility = 0.0f;
    float durationFlexibility = 0.0f;
    return new ActivityAsLinkedListElement(oid, activityNr, ActivityType.LEISURE, startDate,
        duration, tripDuration, startFlexibility, endFlexibility, durationFlexibility);
  }
	
}
