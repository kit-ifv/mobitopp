package edu.kit.ifv.mobitopp.simulation;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.routing.Path;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.simulation.person.StartedTrip;
import edu.kit.ifv.mobitopp.simulation.tour.Subtour;
import edu.kit.ifv.mobitopp.simulation.tour.Tour;

public class MultipleResults implements PersonResults {

  private final List<PersonListener> listeners;

  public MultipleResults() {
    super();
    listeners = new LinkedList<>();
  }

  @Override
  public void notifyEndTrip(Person person, FinishedTrip trip) {
    notifyAllListener(l -> l.notifyEndTrip(person, trip));
  }
  
  @Override
  public void notifyStartTrip(Person person, StartedTrip trip) {
    notifyAllListener(l -> l.notifyStartTrip(person, trip));
  }

  @Override
  public void notifyFinishCarTrip(Person person, Car car, FinishedTrip trip, ActivityIfc activity) {
    notifyAllListener(l -> l.notifyFinishCarTrip(person, car, trip, activity));
  }

  @Override
  public void notifyStartActivity(Person person, ActivityIfc activity) {
    notifyAllListener(l -> l.notifyStartActivity(person, activity));
  }

  @Override
  public void notifySelectCarRoute(Person person, Car car, TripData trip, Path route) {
    notifyAllListener(l -> l.notifySelectCarRoute(person, car, trip, route));
  }

  @Override
  public void writeSubourinfoToFile(Person person, Tour tour, Subtour subtour, Mode tourMode) {
    notifyAllListener(l -> l.writeSubourinfoToFile(person, tour, subtour, tourMode));
  }

  @Override
  public void writeTourinfoToFile(Person person, Tour tour, Zone tourDestination, Mode tourMode) {
    notifyAllListener(l -> l.writeTourinfoToFile(person, tour, tourDestination, tourMode));
  }

  @Override
  public void notifyStateChanged(StateChange stateChange) {
    notifyAllListener(l -> l.notifyStateChanged(stateChange));
  }

  @Override
  public void notifyFinishSimulation() {
    notifyAllListener(l -> l.notifyFinishSimulation());
  }

  private void notifyAllListener(Consumer<PersonListener> listener) {
    listeners.stream().forEach(listener);
  }

  public void addListener(PersonListener listener) {
    this.listeners.add(listener);
  }

  public void removeListener(PersonListener listener) {
    this.listeners.remove(listener);
  }
}
