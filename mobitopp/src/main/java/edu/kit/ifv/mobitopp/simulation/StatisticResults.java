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

public class StatisticResults implements PersonResults {

	private final PersonResults other;
	private final Collection<FinishedTrip> trips;
	private final Collection<StateChange> stateChanges;

	public StatisticResults(PersonResults other) {
		super();
		this.other = other;
		trips = new LinkedList<>();
		stateChanges = new LinkedList<>();
	}

	@Override
	public void notifyEndTrip(Person person, FinishedTrip trip, ActivityIfc activity) {
		other.notifyEndTrip(person, trip, activity);
		trips.add(trip);
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
	public void notifyStateChanged(StateChange change) {
		other.notifyStateChanged(change);
		stateChanges.add(change);
	}

	public Collection<FinishedTrip> trips() {
		return Collections.unmodifiableCollection(trips);
	}
	
	public Collection<StateChange> stateChanges() {
		return Collections.unmodifiableCollection(stateChanges);
	}

	@Override
	public void writeSubourinfoToFile(Person person, Tour tour, Subtour subtour, Mode tourMode) {
		other.writeSubourinfoToFile(person, tour, subtour, tourMode);
	}

	@Override
	public void writeTourinfoToFile(Person person, Tour tour, Zone tourDestination, Mode tourMode) {
		other.writeTourinfoToFile(person, tour, tourDestination, tourMode);
	}

}
