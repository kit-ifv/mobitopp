package edu.kit.ifv.mobitopp.simulation.activityschedule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.ActivityDurationNoRandomizer;
import edu.kit.ifv.mobitopp.simulation.tour.DefaultTourFactory;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.Time;

public class ActivityPeriodWithPrecedingAndSucceedingTest {

	private ActivityPeriod week;

	@Before
	public void setUp() {
		PatternActivityWeek pattern = new ExamplePattern().startingAtWork();
		List<Time> dates = new ExamplePattern().dates();
		week = new ActivityPeriodWithPrecedingAndSucceeding(new DefaultTourFactory(), new ActivityDurationNoRandomizer(), pattern, dates);
	}
	
	public static PatternActivity makeActivity(ActivityType actType, DayOfWeek day, int minutesSinceStartOfDay, int duration) {
			return new PatternActivity(actType,  day, 30, day.getTypeAsInt()*24*60+minutesSinceStartOfDay, duration);
	}



	@Test
	public void testIsEmpty() {
		
		assertFalse(week.isEmpty());

	}
	
	@Test
	public void testFirst() {
		
		ActivityIfc first = week.firstActivity();
		
		assertEquals(ActivityType.WORK, first.activityType());
		assertEquals(8*60, first.duration());
	}
	
	@Test
	public void testLast() {
		
		ActivityIfc last = week.lastActivity();
		
		assertEquals(ActivityType.HOME, last.activityType());
		assertEquals(12*60, last.duration());
	}
	
	@Test
	public void testPrevFirst() {
		
		ActivityIfc first = week.firstActivity();
		
		assertTrue(week.hasPrevActivity(first));
		
		ActivityIfc prevFirst = week.prevActivity(first);
		
		assertFalse(week.hasPrevActivity(prevFirst));
		assertTrue(week.hasNextActivity(prevFirst));
		
		assertEquals(first, week.nextActivity(prevFirst));
	}
	
	@Test
	public void testNextLast() {
		
		ActivityIfc last = week.lastActivity();
		
		assertTrue(week.hasNextActivity(last));
		
		ActivityIfc nextLast = week.nextActivity(last);
		
		assertFalse(week.hasNextActivity(nextLast));
		assertTrue(week.hasPrevActivity(nextLast));
		
		assertEquals(last, week.prevActivity(nextLast));
	}
	
	
	
	
	@Test
	public void testInBetween() {
		
		ActivityIfc first = week.firstActivity();
		ActivityIfc last = week.lastActivity();
		
		ActivityIfc start = week.nextActivity(first);
		ActivityIfc end   = week.prevActivity(last);
		
		List<ActivityIfc> between = week.activitiesBetween(first,last);
		
		assertEquals(12, between.size());
		assertEquals(start, between.get(0));
		assertEquals(end, between.get(11));
	}
	
	@Test
	public void testInBetween2() {
		
		ActivityIfc first = week.firstActivity();
		ActivityIfc last = week.lastActivity();
		
		ActivityIfc prevFirst = week.prevActivity(first);
		ActivityIfc nextLast   = week.nextActivity(last);
		
		List<ActivityIfc> between = week.activitiesBetween(prevFirst,nextLast);
		
		assertEquals(14, between.size());
		assertEquals(first, between.get(0));
		assertEquals(last, between.get(13));
	}
	
	@Test
	public void testNextHomeActivity() {
		
		ActivityIfc first = week.firstActivity();
		ActivityIfc home1 = week.nextHomeActivity(first);
		
		ActivityIfc next = week.nextActivity(first);
		ActivityIfc home2 = week.nextHomeActivity(next);
		
		assertTrue(home1.activityType().isHomeActivity());
		assertTrue(home2.activityType().isHomeActivity());
		assertEquals(next, home1);
		assertNotEquals(home1, home2);
	}
	



}
