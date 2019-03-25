package edu.kit.ifv.mobitopp.simulation;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.routing.Path;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.simulation.tour.Subtour;
import edu.kit.ifv.mobitopp.simulation.tour.Tour;

public class StatisticResults implements PersonListener {

	private final Collection<FinishedTrip> trips;
	private final Collection<StateChange> stateChanges;

	public StatisticResults() {
		super();
		trips = new LinkedList<>();
		stateChanges = new LinkedList<>();
	}

	@Override
	public void notifyEndTrip(Person person, FinishedTrip trip, ActivityIfc activity) {
		trips.add(trip);
	}

	@Override
	public void notifyStateChanged(StateChange change) {
		stateChanges.add(change);
	}

	public Collection<FinishedTrip> trips() {
		return Collections.unmodifiableCollection(trips);
	}
	
	public Collection<StateChange> stateChanges() {
		return Collections.unmodifiableCollection(stateChanges);
	}

  @Override
  public void notifyFinishCarTrip(Person person, Car car, FinishedTrip trip, ActivityIfc activity) {
  }

  @Override
  public void notifyStartActivity(Person person, ActivityIfc activity) {
  }

  @Override
  public void notifySelectCarRoute(Person person, Car car, TripData trip, Path route) {
  }

  @Override
  public void writeSubourinfoToFile(Person person, Tour tour, Subtour subtour, Mode tourMode) {
  }

  @Override
  public void writeTourinfoToFile(Person person, Tour tour, Zone tourDestination, Mode tourMode) {
  }

  @Override
  public void notifyFinishSimulation() {
  }

}
