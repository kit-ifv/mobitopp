package edu.kit.ifv.mobitopp.data;


import java.time.Duration;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;

public class PatternActivity
	implements java.io.Serializable

{

	public static final int maximumDuration = Math.toIntExact(Duration.ofDays(28).toMinutes());

	private static final long serialVersionUID = 8483330614698184240L;
	
	protected final static byte UNDEFINED_BYTE     = Byte.MIN_VALUE;
  protected final static short UNDEFINED_SHORT   = Short.MIN_VALUE;

	public static final PatternActivity WHOLE_WEEK_AT_HOME = new PatternActivity(ActivityType.HOME,
			-1, Time.start, maximumDuration);


  private final ActivityType  activityType ;
  private final int duration             ;
  private final int observedTripDuration ;
	private final Time startTime;



  public PatternActivity(
			ActivityType activityType, int observedTripDuration, Time startTime, int duration) {
		super();
  	assert activityType != null;
		assert observedTripDuration >= -1;
		assert observedTripDuration <= maximumDuration;
		assert startTime != null;
		assert startTime.getMinute() <= 2 * maximumDuration;
		assert duration >= 1;
		assert duration <= maximumDuration;
		this.activityType = activityType;
		this.observedTripDuration = observedTripDuration;
		this.startTime = startTime;
    this.duration = duration;
  }
  
	public PatternActivity(
			ActivityType activityType, DayOfWeek weekDayType, int observedTripDuration, int startMinutes,
			int duration) {
		this(activityType, observedTripDuration,
				SimpleTime.ofDays(weekDayType.getTypeAsInt()).plusMinutes(startMinutes % 1440), duration);
	}

	public int getActivityTypeAsInt() {
    return this.activityType.getTypeAsInt();
  }

	public int getDuration() {
		assert this.duration != UNDEFINED_SHORT;

    return this.duration;
  }

	public int getObservedTripDuration() {
		assert this.observedTripDuration != UNDEFINED_SHORT;

    return this.observedTripDuration;
  }

	/**
	 * Returns the start time in minutes of the day. For the absolute start time use
	 * {@link PatternActivity#startTime()}.
	 */
	public int getStarttime() {
		assert this.startTime.getMinute() != UNDEFINED_SHORT;
		return this.startTime().getHour() * 60 + this.startTime.getMinute();
	}

	public Time startTime() {
		return startTime;
  }

	public int getWeekDayTypeAsInt() {
		return getWeekDayType().getTypeAsInt();
  }

	public ActivityType getActivityType() {
    return this.activityType;
  }

	public DayOfWeek getWeekDayType() {
		return startTime.weekDay();
  }
  

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activityType == null) ? 0 : activityType.hashCode());
		result = prime * result + duration;
		result = prime * result + observedTripDuration;
		result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
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
		PatternActivity other = (PatternActivity) obj;
		if (activityType != other.activityType)
			return false;
		if (duration != other.duration)
			return false;
		if (observedTripDuration != other.observedTripDuration)
			return false;
		if (startTime == null) {
			if (other.startTime != null)
			return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PatternActivity [activityType=" + activityType + ", duration=" + duration
				+ ", observedTripDuration=" + observedTripDuration + ", startTime=" + startTime + "]";
	}
  
}
