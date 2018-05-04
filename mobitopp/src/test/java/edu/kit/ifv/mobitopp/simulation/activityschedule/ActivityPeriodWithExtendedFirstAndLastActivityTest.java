package edu.kit.ifv.mobitopp.simulation.activityschedule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.activityschedule.randomizer.ActivityDurationNoRandomizer;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;

public class ActivityPeriodWithExtendedFirstAndLastActivityTest {

	protected PatternActivityWeek pattern;
	
	List<Time> dates;
	

	protected ActivityPeriod week;



	@Before
	public void setUp() {
		
		pattern = new PatternActivityWeek();
		pattern.addPatternActivity(new PatternActivity(ActivityType.HOME,DayOfWeek.MONDAY,	30,	0,6*60));
		pattern.addPatternActivity(new PatternActivity(ActivityType.WORK,DayOfWeek.MONDAY,	30,	7*60,8*60));
		pattern.addPatternActivity(new PatternActivity(ActivityType.HOME,DayOfWeek.MONDAY,	30,15*60,15*60));
		pattern.addPatternActivity(new PatternActivity(ActivityType.WORK,DayOfWeek.TUESDAY,	30,7*60,8*60));
		pattern.addPatternActivity(new PatternActivity(ActivityType.HOME,DayOfWeek.TUESDAY,	30,15*60,15*60));
		pattern.addPatternActivity(new PatternActivity(ActivityType.WORK,DayOfWeek.WEDNESDAY,30,7*60,8*60));
		pattern.addPatternActivity(new PatternActivity(ActivityType.HOME,DayOfWeek.WEDNESDAY,30,15*60,15*60));
		pattern.addPatternActivity(new PatternActivity(ActivityType.WORK,DayOfWeek.THURSDAY,30,7*60,8*60));
		pattern.addPatternActivity(new PatternActivity(ActivityType.HOME,DayOfWeek.THURSDAY,30,15*60,15*60));
		pattern.addPatternActivity(new PatternActivity(ActivityType.WORK,DayOfWeek.FRIDAY,30,7*60,8*60));
		pattern.addPatternActivity(new PatternActivity(ActivityType.HOME,DayOfWeek.FRIDAY,30,15*60,15*60));
		pattern.addPatternActivity(new PatternActivity(ActivityType.SHOPPING,DayOfWeek.SATURDAY,30,10*60,3*60));
		pattern.addPatternActivity(new PatternActivity(ActivityType.HOME,DayOfWeek.SATURDAY,30,13*60,15*60));
		pattern.addPatternActivity(new PatternActivity(ActivityType.LEISURE,DayOfWeek.SUNDAY,30,14*60,3*60));
		pattern.addPatternActivity(new PatternActivity(ActivityType.HOME,DayOfWeek.SUNDAY,30,17*60,12*60));
		
		dates = new ArrayList<>();
		dates.add(SimpleTime.of(0,0,0,0));
		dates.add(SimpleTime.of(1,0,0,0));
		dates.add(SimpleTime.of(2,0,0,0));
		dates.add(SimpleTime.of(3,0,0,0));
		dates.add(SimpleTime.of(4,0,0,0));
		dates.add(SimpleTime.of(5,0,0,0));
		dates.add(SimpleTime.of(6,0,0,0));

		
		week = new ActivityPeriodWithExtendedFirstAndLastActivity(new ActivityDurationNoRandomizer(), pattern, dates);
	}



	@Test
	public void testIsEmpty() {
		
		assertFalse(week.isEmpty());

	}
	
	@Test
	public void testFirst() {
		
		ActivityIfc first = week.firstActivity();
		
		assertEquals(ActivityType.HOME, first.activityType());
		assertEquals(18*60, first.duration());
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
		
		assertFalse(week.hasPrevActivity(first));
		
	}
	
	@Test
	public void testNextLast() {
		
		ActivityIfc last = week.lastActivity();
		
		assertFalse(week.hasNextActivity(last));		
	}
	
	
	
	
	@Test
	public void testInBetween() {
		
		ActivityIfc first = week.firstActivity();
		ActivityIfc last = week.lastActivity();
		
		ActivityIfc start = week.nextActivity(first);
		ActivityIfc end   = week.prevActivity(last);
		
		List<ActivityIfc> between = week.activitiesBetween(first,last);
		
		assertEquals(13, between.size());
		assertEquals(start, between.get(0));
		assertEquals(end, between.get(12));
	}
	

	
	@Test
	public void testNextHomeActivity() {
		
		ActivityIfc first = week.firstActivity();
		ActivityIfc home1 = week.nextHomeActivity(first);
		
		ActivityIfc next = week.nextActivity(first);
		ActivityIfc next2 = week.nextActivity(next);
		ActivityIfc home2 = week.nextHomeActivity(next);
		ActivityIfc home3 = week.nextHomeActivity(next2);
		
		assertTrue(home1.activityType().isHomeActivity());
		assertTrue(home2.activityType().isHomeActivity());
		assertEquals(next2, home1);
		assertEquals(home1, home2);
		assertNotEquals(home1, home3);
	}
	



}
