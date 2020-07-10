package edu.kit.ifv.mobitopp.simulation.activityschedule;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;

public class Activity implements ActivityIfc {

	private static final int maximumDuration = PatternActivity.maximumDuration;

  private final int _oid;
  private final byte activityNrOfWeek;
  private final ActivityType activityType;
  private Time startDate;
  private Mode mode = null;
  private boolean isRunning = false;
  private int duration; // [in min]
  private final int observedTripDuration; // [in min]
	private ZoneAndLocation zoneAndLocation = null;
	private Time currentEndDate;
	
	private final float startFlexibility;
	private final float endFlexibility;
	private final float durationFlexibility;

  public Activity(
		int oid,
		byte activityNrOfWeek,
		ActivityType activityType,
		Time startDate,
		int duration,
		int observedTripDuration,
		float startFlexibility,
		float endFlexibility,
		float durationFlexibility
	)
  {
    this._oid = oid;
		this.activityNrOfWeek=activityNrOfWeek;
		this.activityType=activityType;
		this.startDate=startDate;

		verifyDuration(duration);
		this.duration=duration;

		assert observedTripDuration <= maximumDuration;
		this.observedTripDuration=observedTripDuration;
		
		this.startFlexibility = startFlexibility;
		this.endFlexibility = endFlexibility;
		this.durationFlexibility = durationFlexibility;
		calculateCurrentEndDate();
  }
  
  public Activity(
  		int oid,
  		byte activityNrOfWeek,
  		ActivityType activityType,
  		Time startDate,
  		int duration,
  		int observedTripDuration
  	) {
  	this(oid, activityNrOfWeek, activityType, startDate, duration, observedTripDuration, 1.0f, 0.0f, 1.0f);
  }
  
  private void verifyDuration(int duration) {
    assert duration >= 1 : duration;
    assert duration <= 2 * maximumDuration : duration;
  }
  
  private void calculateCurrentEndDate() {
    currentEndDate = startDate().plusMinutes(duration());
  }

  public int getOid()
  {
    return this._oid;
  }

  public boolean isRunning()
  {
    return this.isRunning;
  }

  public Time startDate()
  {
		assert this.startDate!=null;

    return this.startDate; 
  }

  public int duration()
  {
    return this.duration;
  }

  public int observedTripDuration()
  {
    return this.observedTripDuration;
  }

  public void setMode(Mode mode)
  {
		assert mode != null;

    this.mode = mode;
  }

  public void setRunning(boolean running_)
  {
    this.isRunning = running_;
  }

  public void setStartDate(Time date_)
  {
		assert date_ != null;

    this.startDate = date_;
    calculateCurrentEndDate();
  }

  public void changeDuration(int duration)
  {
		verifyDuration(duration);

    this.duration = duration;
    calculateCurrentEndDate();
  }

  public ActivityType activityType()
  {
    return this.activityType;
  }

  public Time calculatePlannedEndDate()
  {
    return currentEndDate;
  }

  public boolean isLocationSet()
  {
    return this.zoneAndLocation != null;
  }
  
  public boolean isModeSet()
  {
    return this.mode != null;
  }

  public Mode mode()
  {
    return this.mode;
  }

  public byte getActivityNrOfWeek() {
  	return activityNrOfWeek;
  }


	public String toString() {
					String s =
						"Activity("
						+ getOid() + ";"	
						+ getActivityNrOfWeek() + ";"	
						+	activityType() + ";"	
						+ duration() + ";"	
						+ observedTripDuration() + ";"	
						+ (isLocationSet() ? zoneAndLocation.zone.getId().getExternalId() : "-") + ";"	
						+ (isModeSet() ? mode() : "-") + ";"	
						+ startDate().getDay() + ";"	
						+ String.format("%1$02d:%2$02d", startDate().getHour(), startDate().getMinute()) + ";"
						+ String.format("%1$02d:%2$02d", calculatePlannedEndDate().getHour(), 
																						calculatePlannedEndDate().getMinute()) + ")";

		return s;
	}

	public void setLocation(ZoneAndLocation zoneAndLocation) {
		assert zoneAndLocation != null;
		assert this.zoneAndLocation == null;

		this.zoneAndLocation = zoneAndLocation;
	}

	public Zone zone() {
		assert this.zoneAndLocation != null : ("zone is null for activity of type " + activityType() + "\n" + this);

		return this.zoneAndLocation.zone;
	}

	public Location location() {
		assert this.zoneAndLocation != null;

		return this.zoneAndLocation.location;
	}
	
	public ZoneAndLocation zoneAndLocation() {
		assert this.zoneAndLocation != null;

		return this.zoneAndLocation;
	}



	@Override
	public boolean hasFixedDuration() {
		
		return durationFlexibility < 0.5f;
	}



	@Override
	public boolean hasFixedStart() {
		
		return startFlexibility < 0.5f;
	}



	@Override
	public boolean hasFixedEnd() {
		
		return endFlexibility < 0.5f;
	}



	@Override
	public float durationFlexibility() {
		
		return durationFlexibility;
	}



	@Override
	public float startFlexibility() {
		
		return startFlexibility;
	}



	@Override
	public float endFlexibility() {

		return endFlexibility ;
	}

}

