package edu.kit.ifv.mobitopp.simulation.activityschedule;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

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
			int oid,
			float startFlexibility,
			float endFlexibility,
			float durationFlexibility
		) {
		
		Time startDate = SimpleTime.of(3,0,0,0);
		
			int activityNr = 1;
		
			int duration = 2*60;
			int tripDuration = 30;

			Activity act = 	new ActivityAsLinkedListElement(
												oid,
												(byte)activityNr,
												ActivityType.LEISURE,
												startDate,
												(short)duration,
												(short)tripDuration,
												startFlexibility,
												endFlexibility,
												durationFlexibility
											);

			return act;
		}
	
	@Before
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
	
}
