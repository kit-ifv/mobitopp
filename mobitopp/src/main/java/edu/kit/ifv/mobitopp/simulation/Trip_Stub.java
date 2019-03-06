package edu.kit.ifv.mobitopp.simulation;

import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.simulation.person.BeamedTrip;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;

public class Trip_Stub implements TripData, TripIfc
{

	private final String name; 
	private final ZoneAndLocation origin; 
	private final ZoneAndLocation destination; 
	private final Time startDate; 

	public Trip_Stub (
		String name,
		ZoneAndLocation origin,
		ZoneAndLocation destination,
		Time startDate
	) {
		this.name=name;
		this.origin=origin;
		this.destination=destination;
		this.startDate=startDate;
	}

	public String toString() {
		return name;
	}

	public Mode mode() { return null; }

	public Time startDate() { return startDate; };

  public int plannedDuration() { return -1; }


  public ActivityIfc previousActivity() { return null; }
  public ActivityIfc nextActivity() { return null; }

	public int getOid() { return -1; }
  public Time calculatePlannedEndDate() {return null; }

	@Override
	public ZoneAndLocation origin() {
		return origin;
	}

	@Override
	public ZoneAndLocation destination() {
		return destination;
	}
	
	@Override
	public Optional<Time> timeOfNextChange() {
		return Optional.empty();
	}

	@Override
	public FinishedTrip finish(Time currentDate, PersonResults results) {
		return new BeamedTrip(this, currentDate);
	}

  @Override
  public void prepareTrip(ImpedanceIfc impedance, Time currentTime) {
  }

}

