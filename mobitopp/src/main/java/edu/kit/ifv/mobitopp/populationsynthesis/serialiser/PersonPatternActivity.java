package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.ExtendedPatternActivity;
import edu.kit.ifv.mobitopp.time.DayOfWeek;

public class PersonPatternActivity {

	private final int personOid;
	private final ExtendedPatternActivity patternActivity;

	public PersonPatternActivity(int personOid, ExtendedPatternActivity patternActivity) {
		super();
		this.personOid = personOid;
		this.patternActivity = patternActivity;
	}

	public int personOid() {
		return personOid;
	}

	public int activityTypeAsInt() {
		return patternActivity.getActivityTypeAsInt();
	}

	public int duration() {
		return patternActivity.getDuration();
	}

	public int observedTripDuration() {
		return patternActivity.getObservedTripDuration();
	}

	public int starttime() {
		return patternActivity.getStarttime();
	}

	public DayOfWeek weekDayType() {
		return patternActivity.getWeekDayType();
	}

	public ExtendedPatternActivity pattern() {
		return patternActivity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((patternActivity == null) ? 0 : patternActivity.hashCode());
		result = prime * result + personOid;
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
		PersonPatternActivity other = (PersonPatternActivity) obj;
		if (patternActivity == null) {
			if (other.patternActivity != null)
				return false;
		} else if (!patternActivity.equals(other.patternActivity))
			return false;
		if (personOid != other.personOid)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PersonPatternActivity [personOid=" + personOid + ", patternActivity=" + patternActivity
				+ "]";
	}

	public int tourNumber() {
		return patternActivity.tourNumber();
	}

	public boolean isMainActivity() {
		return patternActivity.isMainActivity();
	}

	public boolean isInSupertour() {
		return patternActivity.isInSupertour();
	}

}
