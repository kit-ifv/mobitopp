package edu.kit.ifv.mobitopp.simulation.activityschedule;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.activityschedule.linkedlist.ActivitySequenceAsLinkedList;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * The Class LeisureWalkActivityPeriodFixer is an {@link ActivityPeriodFixer}.
 * It fixes {@link ActivityType#LEISURE_WALK LEISURE_WALK} activities by
 * appending a {@link ActivityType#HOME HOME} activity. This is required, since
 * LEISURE_WALK is a walk from HOME to HOME but can also take place in other
 * zones the home zone.
 */
public class LeisureWalkActivityPeriodFixer implements ActivityPeriodFixer {

	/**
	 * Checks if the given {@link PatternActivity} is of type
	 * {@link ActivityType#LEISURE_WALK LEISURE_WALK}.
	 *
	 * @param patternActivity   the pattern activity to be checked
	 * @param patternActivities the other pattern activities
	 * @param currentDay        the current day
	 * @return true, if the given {@link PatternActivity} is of type
	 *         {@link ActivityType#LEISURE_WALK LEISURE_WALK}
	 */
	@Override
	public boolean requiresFixing(PatternActivity patternActivity, List<PatternActivity> patternActivities,
			Time currentDay) {
		return patternActivity.getActivityType() == ActivityType.LEISURE_WALK;
	}

	/**
	 * Creates a {@link ActivityType#LEISURE_WALK LEISURE_WALK} activity. If the
	 * following activity is not at {@link ActivityType#HOME HOME}, a new HOME
	 * activity is inserted.
	 *
	 * @param activityPeriod    the activity period, the activity will be added to
	 * @param patternActivity   the pattern activity to be added
	 * @param patternActivities the other pattern activities
	 * @param currentDay        the current day
	 * @param idSupplier        the id supplier, providing ids for created activities 
	 */
	@Override
	public void createAndAddFixedActivity(ActivityPeriod activityPeriod, PatternActivity patternActivity,
			List<PatternActivity> patternActivities, Time currentDay, IntSupplier idSupplier) {

		int index = patternActivities.indexOf(patternActivity);

		if (index == patternActivities.size() - 1) {
			createAndAddWalkActivityWithHomeActivity(activityPeriod, currentDay, index, patternActivity, idSupplier);

		} else {
			PatternActivity nextPatternActivity = patternActivities.get(index + 1);

			if (nextPatternActivity.getActivityType().isHomeActivity()) {
				activityPeriod.createAndAddActivity(currentDay, patternActivity, index);
			} else {
				createAndAddWalkActivityWithHomeActivity(activityPeriod, currentDay, index, patternActivity,
						idSupplier);
			}
		}

	}

	/**
	 * Creates the and adds a walk activity with a following home activity.
	 *
	 * @param activityPeriod  the activity period
	 * @param currentDay      the current day
	 * @param i               the index of the given {@link PatternActivity}
	 * @param patternActivity the pattern activity
	 * @param idSupplier      the id supplier
	 */
	protected void createAndAddWalkActivityWithHomeActivity(ActivityPeriod activityPeriod, Time currentDay, int i,
			PatternActivity patternActivity, IntSupplier idSupplier) {
		List<ActivityIfc> acts = createWalkActivityWithHomeActivity(currentDay, patternActivity, i + 1, idSupplier);
		assert acts.size() == 2;

		activityPeriod.addAsLastActivity(acts.get(0));
		activityPeriod.addAsLastActivity(acts.get(1));
	}

	/**
	 * Creates a walk activity with a following home activity.
	 *
	 * @param currentDay      the current day
	 * @param patternActivity the pattern activity
	 * @param activityNumber  the activity number
	 * @param idSupplier      the id supplier
	 * @return the list
	 */
	private List<ActivityIfc> createWalkActivityWithHomeActivity(Time currentDay, PatternActivity patternActivity,
			int activityNumber, IntSupplier idSupplier) {

		int hour = patternActivity.startTime().getHour();
		int minute = patternActivity.startTime().getMinute();

		Time startTimeWalk = currentDay.newTime(hour, minute, 0);

		assert currentDay.weekDay() == startTimeWalk.weekDay()
				: (currentDay.weekDay() + " : " + startTimeWalk.weekDay());

		int durationHome = patternActivity.getDuration();
		int durationWalk = patternActivity.getObservedTripDuration();

		Time startTimeHome = startTimeWalk.plusMinutes(durationWalk + 1);

		List<ActivityIfc> activities = new ArrayList<ActivityIfc>();

		ActivityIfc walk = ActivitySequenceAsLinkedList.newActivity(idSupplier.getAsInt(), activityNumber,
				startTimeWalk, ActivityType.LEISURE_WALK, durationWalk, 1);

		ActivityIfc home = ActivitySequenceAsLinkedList.newActivity(idSupplier.getAsInt(), activityNumber + 1,
				startTimeHome, ActivityType.HOME, durationHome, 1);

		activities.add(walk);
		activities.add(home);

		return activities;
	}

}
