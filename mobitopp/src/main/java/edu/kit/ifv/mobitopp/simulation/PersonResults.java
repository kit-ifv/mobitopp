package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.routing.Path;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.tour.Subtour;
import edu.kit.ifv.mobitopp.simulation.tour.Tour;

public interface PersonResults {

	void notifyEndTrip(Person person, TripIfc trip, ActivityIfc activity);

	void notifyFinishCarTrip(Person person, Car car, TripIfc trip, ActivityIfc activity);

	void notifyStartActivity(Person person, ActivityIfc activity);

	void notifySelectCarRoute(Person person, Car car, TripIfc trip, Path route);

	void writeSubourinfoToFile(Person person, Tour tour, Subtour subtour, Mode tourMode);

	void writeTourinfoToFile(Person person, Tour tour, Zone tourDestination, Mode tourMode);

}