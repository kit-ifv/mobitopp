package edu.kit.ifv.mobitopp.simulation;

import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.BeamedTrip;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.time.Time;

public class Trip
	implements TripIfc
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


  public Trip(
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
  
  public Trip(
  	int tripId, 
  	Mode mode, 
  	ActivityIfc prev, 
  	ActivityIfc next, 
  	short duration) {
  	this(tripId, prev, next, mode, prev.calculatePlannedEndDate(), duration);
  }

  public ActivityIfc previousActivity() {
		return this.previousActivity;
	}

  public ActivityIfc nextActivity() {
		return this.nextActivity;
	}

  public Mode mode()
  {
    return this.modeType; 
  }

  public Time startDate()
  {
		assert this.startDate != null;

    return this.startDate; 
  }

  public int plannedDuration()
  {
    return this.plannedDuration; 
  }

  public int getOid()
  {
    return this.oid;
  }


  public Time calculatePlannedEndDate()
  {
		Time date = startDate().plusMinutes(plannedDuration());
    
    return date;
  }
  
  @Override
  public Optional<Time> timeOfNextChange() {
  	return Optional.empty();
  }
  
  @Override
  public void startTrip(ImpedanceIfc impedance, Time currentTime) {
  }

  @Override
	public FinishedTrip finish(Time currentDate) {
  	return new BeamedTrip(this, currentDate);
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

