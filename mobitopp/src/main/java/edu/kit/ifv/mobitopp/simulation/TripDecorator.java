package edu.kit.ifv.mobitopp.simulation;

import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;
import edu.kit.ifv.mobitopp.time.Time;


public class TripDecorator implements TripIfc {
  
  private final TripIfc trip;
  private final SimulationPerson person;

  public TripDecorator(TripIfc trip, SimulationPerson person) {
    super();
    this.trip = trip;
    this.person = person;
  }
  
  protected SimulationPerson person() {
    return person;
  }
  
  @Override
  public int getOid() {
    return trip.getOid();
  }

  @Override
  public Time calculatePlannedEndDate() {
    return trip.calculatePlannedEndDate();
  }

  @Override
  public ZoneAndLocation origin() {
    return trip.origin();
  }

  @Override
  public ZoneAndLocation destination() {
    return trip.destination();
  }

  @Override
  public Mode mode() {
    return trip.mode();
  }

  @Override
  public Time startDate() {
    return trip.startDate();
  }

  @Override
  public int plannedDuration() {
    return trip.plannedDuration();
  }

  @Override
  public ActivityIfc previousActivity() {
    return trip.previousActivity();
  }

  @Override
  public ActivityIfc nextActivity() {
    return trip.nextActivity();
  }

  @Override
  public Optional<Time> timeOfNextChange() {
    return trip.timeOfNextChange();
  }

  @Override
  public void allocateVehicle(ImpedanceIfc impedance, Time currentTime) {
    trip.allocateVehicle(impedance, currentTime);
  }

  @Override
  public FinishedTrip finish(Time currentDate) {
    return trip.finish(currentDate);
  }

  @Override
  public String toString() {
    return "TripDecorator [trip=" + trip + "]";
  }

}
