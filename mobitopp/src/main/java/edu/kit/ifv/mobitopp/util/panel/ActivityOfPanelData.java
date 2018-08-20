package edu.kit.ifv.mobitopp.util.panel;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import edu.kit.ifv.mobitopp.simulation.ActivityType;

public class ActivityOfPanelData
{

  private final byte activityType;

  private final short duration;
  private final short observedTripDuration;
  private final short startTime;


  public ActivityOfPanelData(
      int observerTripDuration,
      ActivityType type,
      int duration,
      int startTime
	)
  {
		assert duration >= 0 && duration <= 2*10080 : duration;
		assert observerTripDuration >= -1 && observerTripDuration <= 10080 : observerTripDuration;
		assert startTime >= -10080 && startTime <= 2*10080 : startTime;

		this.observedTripDuration = (short) observerTripDuration;
    this.startTime = (short) startTime;

    this.activityType = (byte) type.getTypeAsInt();

    if (duration <=1 )
    {
    	this.duration = (short) 3;
    }
    else
    {
    	this.duration = (short) duration;
    }
  }


  public int getActivityTypeAsInt()
  {
    return this.activityType;
  }

  public int getDuration()
  {
    return this.duration;
  }

  public int getObservedTripDuration()
  {
    return this.observedTripDuration;
  }

  public int getStarttime()
  {
    return this.startTime;
  }

  public ActivityType getActivityType()
  {
    return ActivityType.getTypeFromInt(getActivityTypeAsInt());
  }

  @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + activityType;
		result = prime * result + duration;
		result = prime * result + observedTripDuration;
		result = prime * result + startTime;
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
		ActivityOfPanelData other = (ActivityOfPanelData) obj;
		if (activityType != other.activityType)
			return false;
		if (duration != other.duration)
			return false;
		if (observedTripDuration != other.observedTripDuration)
			return false;
		if (startTime != other.startTime)
			return false;
		return true;
	}

	public String asCSV() {

		return observedTripDuration + ";" + activityType + ";" + duration + ";" + startTime;
	}

	public String toString() {

		return "ActivityOfPanelData[" + observedTripDuration + "," 
									+ activityType + "," + duration + "," + startTime + "]";
	}


	public static List<ActivityOfPanelData> parseActivities(String pattern) {
		assert pattern != null;
		
		if (pattern.isEmpty()) {
			return new ArrayList<ActivityOfPanelData>();
		}
	
		StringTokenizer tokenizer = new StringTokenizer(pattern, ";");
	
		if ((tokenizer.countTokens() % 4) != 0) {
			throw new AssertionError("the amount elements of the activity pattern must dividable by 4");
		}
	
		List<ActivityOfPanelData> activities = new ArrayList<ActivityOfPanelData>();
	
		while (tokenizer.hasMoreTokens()) {
			activities.add(
						new ActivityOfPanelData(
								Integer.parseInt(tokenizer.nextToken()), 
								ActivityType.getTypeFromInt(Integer.parseInt(tokenizer.nextToken())), 
								Integer.parseInt(tokenizer.nextToken()), 
								Integer.parseInt(tokenizer.nextToken())
						)
			);
		}
	
		return activities;
	}
}
