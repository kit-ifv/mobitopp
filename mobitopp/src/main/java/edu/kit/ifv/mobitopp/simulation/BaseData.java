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

  private final int tripId;
  private final int legId;

	private final ActivityIfc previousActivity;
	private final ActivityIfc nextActivity;

	public BaseData(
			final int tripId,
			final int legId,
			final ActivityIfc previousActivity,
			final ActivityIfc nextActivity,
			final ZoneAndLocation origin,
			final ZoneAndLocation destination,
			final Mode modeType,
			final Time start,
			final short plannedDuration
	)
	{
		super();
    this.tripId = tripId;
    this.legId = legId;
		this.previousActivity = previousActivity;
		this.nextActivity = nextActivity;
    this.origin = origin;
    this.destination = destination;
    this.modeType = modeType;
    this.startDate = start;
    this.plannedDuration = plannedDuration;
	}
	
	public BaseData(
		final int tripId,
		final int legId,
		final ActivityIfc previousActivity,
		final ActivityIfc nextActivity,
		final Mode modeType,
		final Time start,
		final short plannedDuration
	)
  {
		this(tripId, legId, previousActivity, nextActivity, previousActivity.zoneAndLocation(),
				nextActivity.zoneAndLocation(), modeType, start, plannedDuration);
  }
  
  public BaseData(
  	final int tripId,
		final int legId, 
  	final Mode mode, 
  	final ActivityIfc prev, 
  	final ActivityIfc next, 
  	final short duration) {
  	this(tripId, legId, prev, next, mode, prev.calculatePlannedEndDate(), duration);
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
    return this.tripId;
  }
  
  @Override
  public int getLegId() {
  	return this.legId;
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
						+ origin.zone.getId() + "; "	
						+ destination.zone.getId() + "; "	
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

