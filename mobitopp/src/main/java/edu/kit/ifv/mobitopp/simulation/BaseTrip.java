package edu.kit.ifv.mobitopp.simulation;

import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.BeamedTrip;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;
import edu.kit.ifv.mobitopp.time.Time;


public class BaseTrip implements TripData, TripIfc {
  
  private final TripData data;
  private final SimulationPerson person;

  public BaseTrip(TripData data, SimulationPerson person) {
    super();
    this.data = data;
    this.person = person;
  }
  
  protected TripData trip() {
    return data;
  }
  
  protected SimulationPerson person() {
    return person;
  }
  
  @Override
  public int getOid() {
    return data.getOid();
  }

  @Override
  public Time calculatePlannedEndDate() {
    return data.calculatePlannedEndDate();
  }

  @Override
  public ZoneAndLocation origin() {
    return data.origin();
  }

  @Override
  public ZoneAndLocation destination() {
    return data.destination();
  }

  @Override
  public Mode mode() {
    return data.mode();
  }

  @Override
  public Time startDate() {
    return data.startDate();
  }

  @Override
  public int plannedDuration() {
    return data.plannedDuration();
  }

  @Override
  public ActivityIfc previousActivity() {
    return data.previousActivity();
  }

  @Override
  public ActivityIfc nextActivity() {
    return data.nextActivity();
  }

  @Override
  public Optional<Time> timeOfNextChange() {
    return Optional.empty();
  }

  @Override
  public void prepareTrip(ImpedanceIfc impedance, Time currentTime) {
  }

  @Override
  public FinishedTrip finish(Time currentDate, PersonResults results) {
    return new BeamedTrip(data, currentDate);
  }

  @Override
  public String toString() {
    return "TripDecorator [trip=" + data + "]";
  }

}
