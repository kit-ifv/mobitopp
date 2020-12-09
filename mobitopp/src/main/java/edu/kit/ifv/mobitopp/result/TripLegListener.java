package edu.kit.ifv.mobitopp.result;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.routing.Path;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.PersonListener;
import edu.kit.ifv.mobitopp.simulation.StateChange;
import edu.kit.ifv.mobitopp.simulation.TripData;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.simulation.person.StartedTrip;
import edu.kit.ifv.mobitopp.simulation.tour.Subtour;
import edu.kit.ifv.mobitopp.simulation.tour.Tour;

/**
 * This class logs all end trip events for all legs.
 * 
 * @author Lars Briem
 *
 */
public class TripLegListener implements PersonListener {

	private final PersonListener other;

	public TripLegListener(PersonListener other) {
		this.other = other;
	}

	@Override
	public void notifyEndTrip(Person person, FinishedTrip trip) {
		trip.forEachLeg(leg -> other.notifyEndTrip(person, leg));
	}

	@Override
	public void notifyStartTrip(Person person, StartedTrip trip) {
		other.notifyStartTrip(person, trip);
	}

	@Override
	public void notifyFinishCarTrip(Person person, Car car, FinishedTrip trip,
			ActivityIfc activity) {
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
	public void writeSubtourinfoToFile(Person person, Tour tour, Subtour subtour, Mode tourMode) {
		other.writeSubtourinfoToFile(person, tour, subtour, tourMode);
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
	public void notifyFinishSimulation() {
		other.notifyFinishSimulation();
	}

}
