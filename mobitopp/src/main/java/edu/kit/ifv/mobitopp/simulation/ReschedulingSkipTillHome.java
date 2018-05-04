package edu.kit.ifv.mobitopp.simulation;

import java.util.List;

import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivitySchedule;
import edu.kit.ifv.mobitopp.time.Time;

class ReschedulingSkipTillHome
 implements ReschedulingStrategy 
{

	protected final SimulationDays simulationDays;

	public ReschedulingSkipTillHome(SimulationDays simulationDays) {
		this.simulationDays = simulationDays;
	}

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
		Time expectedEndTime = beginningActivity.calculatePlannedEndDate();

		if (plannedDay.isAfter(currentDay)) {
			adjustDurationOfActivityToMatchStartOfNextActivity(schedule, beginningActivity);
			return;
		}

		if (plannedDay.equals(currentDay)) {
			Time endOfDay = currentTime.nextDay();
			if(expectedEndTime.isAfter(endOfDay))
			{ 
				removeConflictingActivitiesTillNextHomeActivity(schedule, beginningActivity);
				adjustDurationOfActivityToMatchStartOfNextActivity(schedule, beginningActivity);
			} 
		}

    if (plannedDay.isBefore(currentDay)) {
			removeConflictingActivitiesTillNextHomeActivity(schedule, beginningActivity);
			adjustDurationOfActivityToMatchStartOfNextActivity(schedule, beginningActivity);
		}
  }


  protected void removeConflictingActivitiesTillNextHomeActivity(
		ModifiableActivitySchedule schedule,
		ActivityIfc activity
	) {
		ActivityIfc home = activity.activityType().isHomeActivity() ? activity
																																		: schedule.nextHomeActivity(activity);
		List<ActivityIfc> conflicts = schedule.getSucceedingActivitiesUntilDate(activity, home.startDate());

		assert !conflicts.contains(home);

		for (ActivityIfc conflict : conflicts) {
			schedule.removeActivity(conflict);
		}
	}


	protected void adjustDurationOfActivityToMatchStartOfNextActivity(
		ActivitySchedule schedule,
		ActivityIfc beginningActivity
	) {

		if (schedule.hasNextActivity(beginningActivity)) {

    	ActivityIfc nextActivity = schedule.nextActivity(beginningActivity);

			assert nextActivity != null;

			adjustDurationOfActivityToMatchStartOfNextActivity(beginningActivity, nextActivity);	

		} else {
			adjustDurationOfActivityUntil(beginningActivity,
																		simulationDays.endDate().plusHours(6));
		}

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

		setDuration(currentActivity, newDuration);
  }

	protected void adjustDurationOfActivityUntil(
		ActivityIfc activity,
		Time end
	) {

		int duration = Math.toIntExact(end.differenceTo(activity.startDate()).toMinutes());

		setDuration(activity, duration);
	}

	protected void setDuration(
		ActivityIfc activity,
		int duration
	) {

		final int MIN_DURATION = 5;
		final int MAX_DURATION = 10080;

		activity.changeDuration(Math.min(MAX_DURATION,
														Math.max(MIN_DURATION, duration)
												)); 
	}




}
