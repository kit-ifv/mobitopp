package edu.kit.ifv.mobitopp.simulation.activityschedule;

import java.util.List;
import java.util.function.IntSupplier;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.time.Time;

/**
 * The Interface ActivityPeriodFixer. Can fix certain {@link ActivityIfc
 * activities} of an {@link ActivityPeriod} if required.
 */
public interface ActivityPeriodFixer {

	/**
	 * Checks whether the given {@link PatternActivity} requires fixing before
	 * adding it to an {@link ActivityPeriod}.
	 * @param patternActivity   the pattern activity to be checked
	 * @param patternActivities the other pattern activities
	 * @param currentDay        the current day
	 *
	 * @return true, if if the given {@link PatternActivity} requires fixing
	 */
	public boolean requiresFixing(PatternActivity patternActivity, List<PatternActivity> patternActivities,
			Time currentDay);

	/**
	 * Creates the and adds the fixed activity to the given {@link ActivityPeriod}.
	 *
	 * @param activityPeriod    the activity period, the activity will be added to
	 * @param patternActivity   the pattern activity to be added
	 * @param patternActivities the other pattern activities
	 * @param currentDay        the current day
	 * @param idSupplier        the id supplier, providing ids for created activities 
	 */
	public void createAndAddFixedActivity(ActivityPeriod activityPeriod, PatternActivity patternActivity,
			List<PatternActivity> patternActivities, Time currentDay, IntSupplier idSupplier);

}
