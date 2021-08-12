package edu.kit.ifv.mobitopp.simulation;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.data.IntegerMatrix;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.routing.Path;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.simulation.person.StartedTrip;
import edu.kit.ifv.mobitopp.simulation.tour.Subtour;
import edu.kit.ifv.mobitopp.simulation.tour.Tour;

public class AggregateDemandOfStartedTrips implements PersonListener {

	private final Consumer<IntegerMatrix> output;
	private final IntegerMatrix matrix;
	private final BiPredicate<Person, StartedTrip> demandFilter;
	private final int scaleFactor;

	public AggregateDemandOfStartedTrips(final List<ZoneId> zones,
			final BiPredicate<Person, StartedTrip> demandFilter,
			final Consumer<IntegerMatrix> output, final int scaleFactor) {

		this.matrix = new IntegerMatrix(zones);
		this.demandFilter = demandFilter;
		this.output = output;
		this.scaleFactor = scaleFactor;
	}

	@Override
	public void notifyEndTrip(final Person person, final FinishedTrip trip) {
	}

	@Override
	public void notifyStartTrip(Person person, StartedTrip trip) {
		if (demandFilter.test(person, trip)) {
			matrix.add(trip.origin().zone().getId(), trip.destination().zone().getId(),
					scaleFactor);
		}
	}

	@Override
	public void notifyFinishCarTrip(final Person person, final Car car, final FinishedTrip trip,
			final ActivityIfc activity) {
	}

	@Override
	public void notifyStartActivity(final Person person, final ActivityIfc activity) {
	}

	@Override
	public void notifySelectCarRoute(final Person person, final Car car, final TripData trip,
			final Path route) {
	}

	@Override
	public void writeTourinfoToFile(final Person person, final Tour tour,
			final Zone tourDestination, final Mode tourMode) {
	}

	@Override
	public void notifyStateChanged(final StateChange stateChange) {
	}

	@Override
	public void notifyFinishSimulation() {
	}

	public void writeMatrix() {
		output.accept(matrix);
	}

	@Override
	public void writeSubtourinfoToFile(Person person, Tour tour, Subtour subtour, Mode tourMode) {
	}

}
