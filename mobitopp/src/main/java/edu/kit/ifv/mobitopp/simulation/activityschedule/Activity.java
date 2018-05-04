package edu.kit.ifv.mobitopp.simulation.activityschedule;


import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;


public class Activity
	implements ActivityIfc
{

	protected final static int UNDEFINED_OID       = -1;

  protected final static byte UNDEFINED_BYTE     = Byte.MIN_VALUE;
  protected final static short UNDEFINED_SHORT   = Short.MIN_VALUE;

  private final int _oid;

  private final byte activityNrOfWeek;
  private final ActivityType activityType;
  private Time startDate;

  private Mode mode = null;

  private boolean isRunning = false;

  private short duration             = UNDEFINED_SHORT; // [in min]
  private short observedTripDuration = UNDEFINED_SHORT; // [in min]

	private ZoneAndLocation zoneAndLocation = null;
	
	private final float startFlexibility;
	private final float endFlexibility;
	private final float durationFlexibility;
	


  public Activity(
		int oid,
		byte activityNrOfWeek,
		ActivityType activityType,
		Time startDate,
		short duration,
		short observedTripDuration,
		float startFlexibility,
		float endFlexibility,
		float durationFlexibility
	)
  {
    this._oid = oid;
		this.activityNrOfWeek=activityNrOfWeek;
		this.activityType=activityType;
		this.startDate=startDate;

		assert duration >= 1;
		assert duration <= 2*10080 : duration;
		this.duration=duration;

		assert observedTripDuration <= 10080;
		this.observedTripDuration=observedTripDuration;
		
		this.startFlexibility = startFlexibility;
		this.endFlexibility = endFlexibility;
		this.durationFlexibility = durationFlexibility;
  }
  
  public Activity(
  		int oid,
  		byte activityNrOfWeek,
  		ActivityType activityType,
  		Time startDate,
  		short duration,
  		short observedTripDuration
  	) {
  	this(oid, activityNrOfWeek, activityType, startDate, duration, observedTripDuration, 1.0f, 0.0f, 1.0f);
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
		assert this.duration != UNDEFINED_SHORT;

    return this.duration;
  }

  public int observedTripDuration()
  {
		assert this.observedTripDuration != UNDEFINED_SHORT;

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
  }

  public void changeDuration(int duration)
  {
		assert duration >= 1 : duration;
		assert duration <= 10080 : duration;

    this.duration = (short) duration;
  }

  public ActivityType activityType()
  {
    return this.activityType;
  }

  public Time calculatePlannedEndDate()
  {
    Time date = startDate().plusMinutes(duration());
    
    return date;
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

  public int getActivityNrOfWeek() {
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
						+ (isLocationSet() ? zoneAndLocation.zone.getId() : "-") + ";"	
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

