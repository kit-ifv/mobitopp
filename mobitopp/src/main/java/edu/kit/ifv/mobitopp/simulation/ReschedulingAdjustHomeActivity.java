package edu.kit.ifv.mobitopp.simulation;


import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.Time;

class ReschedulingAdjustHomeActivity
 implements ReschedulingStrategy 
{

	protected final SimulationContext context;

	public ReschedulingAdjustHomeActivity(SimulationContext context) {
		this.context = context;
	}

	public void adjustSchedule(
		ModifiableActivitySchedule schedule, 
		ActivityIfc beginningActivity, 
		Time plannedStartTime,
		Time currentTime
	) {

		assert beginningActivity != null;
		assert currentTime != null;

		// Use home activities to balance discrepancies in activity program
		final int MINIMUM_HOME_ACTIVITY_DURATION = 15;

		if (beginningActivity.activityType().isHomeActivity()) {
			
			int deviation = Math.toIntExact(plannedStartTime.differenceTo(currentTime).toMinutes());
			
			boolean isLate = deviation < 0;
			
			if (isLate) {
				assert isLate : (isLate + " " + deviation + "\n" + plannedStartTime + "\n" + currentTime);

				int absoluteDeviation = - deviation;
				assert absoluteDeviation > 0;
				
				int activityDuration = beginningActivity.duration();
				
				if (activityDuration > absoluteDeviation + MINIMUM_HOME_ACTIVITY_DURATION) {
					int changedDuration = beginningActivity.duration() - absoluteDeviation;
					beginningActivity.changeDuration(changedDuration);
				} else {
					beginningActivity.changeDuration(MINIMUM_HOME_ACTIVITY_DURATION);
				}

			} else { 
				assert !isLate : (isLate + " " + deviation + "\n" + plannedStartTime + "\n" + currentTime);
				assert deviation >= 0 : (isLate + " " + deviation + "\n" + plannedStartTime + "\n" + currentTime);
				
				int changedDuration = Math.min(beginningActivity.duration() + deviation,10080);
				beginningActivity.changeDuration(changedDuration);

			}
		}
  }
}
