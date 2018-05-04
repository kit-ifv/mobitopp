package edu.kit.ifv.mobitopp.simulation;

import java.util.List;

import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivitySchedule;
import edu.kit.ifv.mobitopp.time.Time;

class ReschedulingTruncate
 implements ReschedulingStrategy 
{

	public void adjustSchedule(
		ModifiableActivitySchedule schedule, 
		ActivityIfc beginningActivity, 
		Time plannedStartTime,
		Time currentTime
	) {

		assert beginningActivity != null;
		assert currentTime != null;

		Time currentDay = currentTime.startOfDay();
		Time plannedDay = plannedStartTime.startOfDay();
		Time expectedEndDay = beginningActivity.calculatePlannedEndDate().startOfDay();

		if (plannedDay.isAfter(currentDay)) {
			extendDurationUntilStartofNextActivity(schedule, beginningActivity);
			return;
		}

		if (plannedDay.equals(currentDay)) {
			if(expectedEndDay.isAfter(currentDay))
			{ 
				removeConflictingActivities(schedule, beginningActivity);
			} 
		}

    if (plannedDay.isBefore(currentDay)) {
			removeConflictingActivities(schedule, beginningActivity);
		}

		adjustDurationOfActivityIfFollwingActivityIsAtNextDay(schedule, beginningActivity);
  }


  protected void removeConflictingActivities(
		ModifiableActivitySchedule schedule,
		ActivityIfc activity
	) {

		Time endOfActivity = activity.calculatePlannedEndDate();

		List<ActivityIfc> conflicts = schedule.getSucceedingActivitiesUntilDate(activity, endOfActivity);

		for (ActivityIfc conflict : conflicts) {
			schedule.removeActivity(conflict);
		}
	}


	protected void adjustDurationOfActivityIfFollwingActivityIsAtNextDay(
		ActivitySchedule schedule,
		ActivityIfc beginningActivity
	)
	{
    ActivityIfc nextActivity = schedule.nextActivity(beginningActivity);

		if (nextActivity == null) { 
			extendDurationOfActivityUntilEndOfDay(beginningActivity);	
			return; 
		}

    if (nextActivity.startDate().startOfDay().isAfter(beginningActivity.startDate().startOfDay())) { 
			adjustDurationOfActivityToMatchStartOfNextActivity(beginningActivity, nextActivity);	
		}
	}

	protected void extendDurationUntilStartofNextActivity(
		ActivitySchedule schedule,
		ActivityIfc beginningActivity
	) {

    ActivityIfc nextActivity = schedule.nextActivity(beginningActivity);

		assert nextActivity != null;

		adjustDurationOfActivityToMatchStartOfNextActivity(beginningActivity, nextActivity);	

	}


  private void adjustDurationOfActivityToMatchStartOfNextActivity(
      ActivityIfc currentActivity,
      ActivityIfc nextActivity)
  {
		assert currentActivity != null;
		assert nextActivity != null;


    Time currentDate = currentActivity.startDate();

		int startToCurrent = Math.toIntExact(nextActivity.startDate().differenceTo(currentDate).toMinutes());
		int tripDuration = nextActivity.observedTripDuration();
		int newDuration = startToCurrent - tripDuration;

		final int MIN_DURATION = 5;

		currentActivity.changeDuration(java.lang.Math.max(newDuration,MIN_DURATION)); 
  }

  private void extendDurationOfActivityUntilEndOfDay(
      ActivityIfc activity
  )
  {
		assert activity != null;


    Time currentDate = activity.startDate();
		int oldDuration = activity.duration();

		int newDuration =  Math.toIntExact(activity.startDate().nextDay().differenceTo(currentDate).toMinutes());

		if (oldDuration < newDuration) { 
		
			activity.changeDuration(newDuration); 
		}
  }

}
