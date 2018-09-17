package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;

public class SimpleActivityTest {
	
	PatternActivity home_monday = createActivity(ActivityType.HOME, DayOfWeek.MONDAY, SimpleTime.ofHours(0), RelativeTime.ofHours(6));
	PatternActivity work_wednesday =	createActivity(ActivityType.WORK, DayOfWeek.WEDNESDAY, SimpleTime.ofHours(7), RelativeTime.ofHours(8));
	
	
	private static PatternActivity createActivity(
			ActivityType activityType,
      DayOfWeek day,
      Time start,
      RelativeTime duration
			) {
		return new PatternActivity(activityType, day, 15, (int) start.toMinutes(), (int) duration.toMinutes());
	}

	@Test
	public void fromPatternActivity() {
		
		SimpleActivity simple_home_monday = SimpleActivity.fromPatternActivity(home_monday);
		SimpleActivity simple_work_wednesday = SimpleActivity.fromPatternActivity(work_wednesday);
		
		assertEquals(home_monday.getActivityType(),simple_home_monday.activityType());
		assertEquals(work_wednesday.getActivityType(),simple_work_wednesday.activityType());
		
		assertEquals(home_monday.getWeekDayType(),simple_home_monday.plannedStart().weekDay());
		assertEquals(work_wednesday.getWeekDayType(),simple_work_wednesday.plannedStart().weekDay());
	}
}
