package edu.kit.ifv.mobitopp.data;

import static java.util.Collections.emptyList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.DayOfWeek;

public class PatternActivityWeek implements Serializable {


	private final List<PatternActivity> patternActivities;
	
	private static final long serialVersionUID = -6351800754350553869L;

	public static final PatternActivityWeek WHOLE_WEEK_AT_HOME = new PatternActivityWeek(Arrays.asList(
																																	PatternActivity.WHOLE_WEEK_AT_HOME
																																));

	public PatternActivityWeek() {
		this(emptyList());
	}
  


	public PatternActivityWeek(List<PatternActivity> activities) {
		super();
		patternActivities = new ArrayList<>(activities);
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

	public List<PatternActivity> getPatternActivities(DayOfWeek dayOfWeek) {
		assert dayOfWeek != null;

		List<PatternActivity> activitiesAtDaytype = new ArrayList<>();
		for (PatternActivity patternActivity : patternActivities) {
			if (patternActivity.getWeekDayTypeAsInt() == dayOfWeek.getTypeAsInt()) {
				activitiesAtDaytype.add(patternActivity);
			}

			// We can skip here because we already found the patternactivities of the weekday
			if (patternActivity.getWeekDayTypeAsInt() > dayOfWeek.getTypeAsInt()) {
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
	
	public PatternActivity firstActivityOfDayOrNext(final DayOfWeek dayOfWeek) {
		
		DayOfWeek day = dayOfWeek;
		
		assert !this.patternActivities.isEmpty();
		
		List<PatternActivity> activities = getPatternActivities(day);
		
		while (activities.isEmpty()) {
			day = day.next();
			activities = getPatternActivities(day);
		}
		
		assert !activities.isEmpty();
		
		return activities.get(0);
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

}
