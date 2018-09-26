package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;

public class SplitActivityTest {
	
	final ActivityType activityType = ActivityType.WORK;
	
	PatternActivity work1 =	createActivity(activityType, DayOfWeek.WEDNESDAY, SimpleTime.ofHours(2*24+7), RelativeTime.ofHours(4));
	PatternActivity work2 =	createActivity(activityType, DayOfWeek.WEDNESDAY, SimpleTime.ofHours(2*24+12), RelativeTime.ofHours(2));
	PatternActivity work3 =	createActivity(activityType, DayOfWeek.WEDNESDAY, SimpleTime.ofHours(2*24+15), RelativeTime.ofHours(2));
	
	
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
		
		SplitActivity splitActivity = SplitActivity.fromPatternActivities(work1.getActivityType(), Arrays.asList(work1, work2));
		
		assertEquals(work1.getActivityType(),splitActivity.activityType());
		assertEquals(work2.getActivityType(),splitActivity.activityType());
		assertEquals(work3.getActivityType(),splitActivity.activityType());
		
		assertEquals(work1.getWeekDayType(),splitActivity.plannedStart().weekDay());
		assertEquals(work2.getWeekDayType(),splitActivity.plannedStart().weekDay());
		assertEquals(work3.getWeekDayType(),splitActivity.plannedStart().weekDay());
	}
}
