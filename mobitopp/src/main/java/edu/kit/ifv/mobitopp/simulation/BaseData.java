package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.Time;

public class BaseData implements TripData
{

  private final ZoneAndLocation origin;
  private final ZoneAndLocation destination;
  
  private final Mode modeType;
  private final Time startDate;
	/** in minutes **/
	private final short plannedDuration;

  private final int oid;

	private final ActivityIfc previousActivity;
	private final ActivityIfc nextActivity;


  public BaseData(
		int oid,
		ActivityIfc previousActivity,
		ActivityIfc nextActivity,
		Mode modeType,
		Time start,
		short plannedDuration
	)
  {
    this.oid = oid;
		this.previousActivity = previousActivity;
		this.nextActivity = nextActivity;
    this.origin = previousActivity.zoneAndLocation();
    this.destination = nextActivity.zoneAndLocation();
    this.modeType = modeType;
    this.startDate = start;
    this.plannedDuration = plannedDuration;
  }
  
  public BaseData(
  	int tripId, 
  	Mode mode, 
  	ActivityIfc prev, 
  	ActivityIfc next, 
  	short duration) {
  	this(tripId, prev, next, mode, prev.calculatePlannedEndDate(), duration);
  }

  @Override
  public ActivityIfc previousActivity() {
		return this.previousActivity;
	}

  @Override
  public ActivityIfc nextActivity() {
		return this.nextActivity;
	}

  @Override
  public Mode mode()
  {
    return this.modeType; 
  }

  @Override
  public Time startDate()
  {
		assert this.startDate != null;

    return this.startDate; 
  }

  @Override
  public int plannedDuration()
  {
    return this.plannedDuration; 
  }

  @Override
  public int getOid()
  {
    return this.oid;
  }


  @Override
  public Time calculatePlannedEndDate()
  {
		Time date = startDate().plusMinutes(plannedDuration());
    
    return date;
  }
  
	public String toString() {

					String s =
						"TRIP: "
						+ origin.zone.getOid() + "; "	
						+ destination.zone.getOid() + "; "	
						+ mode() + "; "	
						+ startDate().getDay() + "; "	
						+ String.format("%1$02d", startDate().getHour()) + ":"	
						+ String.format("%1$02d", startDate().getMinute()) + "; "	
						+ plannedDuration() + "; "	;

		return s;
	}

	@Override
  public ZoneAndLocation origin() {
		return origin;
	}

	@Override
  public ZoneAndLocation destination() {
		return destination;
	}

}
