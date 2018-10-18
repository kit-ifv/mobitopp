package edu.kit.ifv.mobitopp.data;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.util.panel.ActivityOfPanelData;

public class PatternActivityWeek implements Serializable {

	private static final Time defaultStartTime = Time.start;
	private static final long serialVersionUID = -6351800754350553869L;

	public static final PatternActivityWeek WHOLE_WEEK_AT_HOME = new PatternActivityWeek(
			asList(PatternActivity.WHOLE_WEEK_AT_HOME));

	private final List<PatternActivity> patternActivities;
	
	public PatternActivityWeek() {
		this(emptyList());
	}

	public PatternActivityWeek(List<? extends PatternActivity> activities) {
		super();
		this.patternActivities = new ArrayList<>(activities);
	}

	public boolean existsPatternActivity(ActivityType type) {
		assert type != null;

		for (PatternActivity patternActivity : patternActivities) {
			if (patternActivity.getActivityType() == type) {
				return true;
			}
		}

		return false;
	}

	public void addPatternActivity(PatternActivity activity_) {
		assert activity_ != null;

		this.patternActivities.add(activity_);
	}

	public List<PatternActivity> getPatternActivities(Time time) {
		Time currentDay = time.startOfDay();
		Time nextDay = currentDay.nextDay();

		List<PatternActivity> activitiesAtDaytype = new ArrayList<>();
		for (PatternActivity patternActivity : patternActivities) {
			if (currentDay.equals(patternActivity.startTime().startOfDay())) {
				activitiesAtDaytype.add(patternActivity);
			}

			// We can skip here because we already found the patternactivities of the weekday
			if (nextDay.equals(patternActivity.startTime().startOfDay())) {
				break;
			}
		}

		return activitiesAtDaytype;
	}

	public List<PatternActivity> getPatternActivities() {
		return new ArrayList<PatternActivity>(patternActivities);
	}
	
	public PatternActivity first() {
		return patternActivities.get(0);
	}
	
	public PatternActivity last() {
		return patternActivities.get(patternActivities.size()-1);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((patternActivities == null) ? 0 : patternActivities.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PatternActivityWeek other = (PatternActivityWeek) obj;
		if (patternActivities == null) {
			if (other.patternActivities != null)
				return false;
		} else if (!patternActivities.equals(other.patternActivities))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PatternActivityWeek [patternActivities=" + patternActivities + "]";
	}

	public static PatternActivityWeek fromActivityOfPanelData(
			Collection<ActivityOfPanelData> activityOfPanelData) {
		PatternActivityWeek patternActivityWeek = new PatternActivityWeek();
		if (activityOfPanelData.isEmpty()) {
			return WHOLE_WEEK_AT_HOME;
		}
	
		Time previousEnd = defaultStartTime;
		for (ActivityOfPanelData activity : activityOfPanelData) {
			Time startTime = defaultStartTime;
			if (activity.getStarttime() != -1) {
				int startTimeOfActivity = activity.getStarttime();
				startTime = SimpleTime.ofMinutes(startTimeOfActivity);
			} else {
				if (patternActivityWeek.getPatternActivities().isEmpty()) {
					startTime = defaultStartTime;
				} else {
					int tripDuration = activity.getObservedTripDuration() > -1 ?
															activity.getObservedTripDuration() : 15;
					startTime = previousEnd.plusMinutes(tripDuration);
				}
			}
	
			previousEnd = startTime.plusMinutes(activity.getDuration());
			PatternActivity patternActivity 
				= new PatternActivity(
						ActivityType.getTypeFromInt(activity.getActivityTypeAsInt()),
								activity.getObservedTripDuration(),
								startTime, 
								activity.getDuration()
					);
			patternActivityWeek.addPatternActivity(patternActivity);
		}
	
		return patternActivityWeek;
	}
	
}
