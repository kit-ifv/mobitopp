package edu.kit.ifv.mobitopp.data;


import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.DayOfWeek;

public class PatternActivity
	implements java.io.Serializable

{

	private static final long serialVersionUID = 8483330614698184240L;
	
	protected final static byte UNDEFINED_BYTE     = Byte.MIN_VALUE;
  protected final static short UNDEFINED_SHORT   = Short.MIN_VALUE;

	public static final PatternActivity WHOLE_WEEK_AT_HOME = new PatternActivity(ActivityType.HOME,DayOfWeek.MONDAY,-1,0,10080);


  private final ActivityType  activityType ;
  private final short duration             ;
  private final short observedTripDuration ;
  private final short starttime            ;
  private final DayOfWeek  weekDayType     ;



  public PatternActivity(
  		ActivityType activityType,
      DayOfWeek weekDayType,
      int observedTripDuration,
      int starttime,
      int duration)
  {
  	assert activityType != null;
		assert weekDayType != null;
		assert duration >= 1;
		assert duration <= 10080;
		assert observedTripDuration >= -1;
		assert observedTripDuration <= 10080;
		assert starttime >= -1;
		assert starttime <= 2*10080;

		this.activityType = activityType;
    this.duration = (short) duration;
    this.observedTripDuration = (short) observedTripDuration;
    this.starttime = (short) starttime;
    this.weekDayType = weekDayType;
  }
  

	public int getActivityTypeAsInt()
  {
    return this.activityType.getTypeAsInt();
  }


  public int getDuration()
  {
		assert this.duration != UNDEFINED_SHORT;

    return this.duration;
  }


  public int getObservedTripDuration()
  {
		assert this.observedTripDuration != UNDEFINED_SHORT;

    return this.observedTripDuration;
  }


  public int getStarttime()
  {
		assert this.starttime != UNDEFINED_SHORT;

    return this.starttime;
  }

  public int getWeekDayTypeAsInt()
  {

    return this.weekDayType.getTypeAsInt();
  }

 
  public ActivityType getActivityType()
  {
    return this.activityType;
  }

  public DayOfWeek getWeekDayType()
  {
		return this.weekDayType;
  }
  

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activityType == null) ? 0 : activityType.hashCode());
		result = prime * result + duration;
		result = prime * result + observedTripDuration;
		result = prime * result + starttime;
		result = prime * result + ((weekDayType == null) ? 0 : weekDayType.hashCode());
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
		if (starttime != other.starttime)
			return false;
		if (weekDayType != other.weekDayType)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PatternActivity [activityType=" + activityType + ", duration=" + duration
				+ ", observedTripDuration=" + observedTripDuration + ", starttime=" + starttime
				+ ", weekDayType=" + weekDayType + "]";
	}
  
}
