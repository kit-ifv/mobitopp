package edu.kit.ifv.mobitopp.simulation;

import java.io.UncheckedIOException;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.routing.Path;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.simulation.tour.Subtour;
import edu.kit.ifv.mobitopp.simulation.tour.Tour;


public class PersonResultsDecorator implements PersonResults {

  private final PersonResults other;

  public PersonResultsDecorator(PersonResults other) {
    super();
    this.other = other;
  }

  @Override
  public void notifyEndTrip(Person person, FinishedTrip trip, ActivityIfc activity) {
    other.notifyEndTrip(person, trip, activity);
  }

  @Override
  public void notifyFinishCarTrip(Person person, Car car, FinishedTrip trip, ActivityIfc activity) {
    other.notifyFinishCarTrip(person, car, trip, activity);
  }

  @Override
  public void notifyStartActivity(Person person, ActivityIfc activity) {
    other.notifyStartActivity(person, activity);
  }

  @Override
  public void notifySelectCarRoute(Person person, Car car, TripData trip, Path route) {
    other.notifySelectCarRoute(person, car, trip, route);
  }

  @Override
  public void writeSubourinfoToFile(Person person, Tour tour, Subtour subtour, Mode tourMode) {
    other.writeSubourinfoToFile(person, tour, subtour, tourMode);
  }

  @Override
  public void writeTourinfoToFile(Person person, Tour tour, Zone tourDestination, Mode tourMode) {
    other.writeTourinfoToFile(person, tour, tourDestination, tourMode);
  }

  @Override
  public void notifyStateChanged(StateChange stateChange) {
    other.notifyStateChanged(stateChange);
  }

  @Override
  public void close() throws UncheckedIOException {
    other.close();
  }

}
