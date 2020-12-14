package edu.kit.ifv.mobitopp.simulation;

import java.util.ArrayList;
import java.util.Collections;
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
	private final BiPredicate<Person, StartedTrip<?>> demandFilter;
	private final int scaleFactor;
	private final ArrayList<Long> times;

	public AggregateDemandOfStartedTrips(final List<ZoneId> zones,
			final BiPredicate<Person, StartedTrip<?>> demandFilter,
			final Consumer<IntegerMatrix> output, final int scaleFactor) {

		List<ZoneId> moreZones = new ArrayList<ZoneId>();
		moreZones.addAll(zones);
		int maxId = zones.stream().map(zone -> zone.getMatrixColumn()).max(Integer::compare).get();
		for (int i = 0; i < zones.size() * 10; i++) {
			maxId++;
			moreZones.add(new ZoneId("ADD_ID_" + maxId, maxId));
		}

		this.matrix = new IntegerMatrix(moreZones);
		this.demandFilter = demandFilter;
		this.output = output;
		this.scaleFactor = scaleFactor;
		this.times = new ArrayList<Long>();
	}

	@Override
	public void notifyEndTrip(final Person person, final FinishedTrip trip) {
		// Moved matrix increment to notifyStartTrip compared to AggregateDemand
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
	public void writeSubourinfoToFile(final Person person, final Tour tour, final Subtour subtour,
			final Mode tourMode) {
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
		// write matrix is not performed at the end of the simulation anymore
		//The DemandResultsListenerRegistry takes care of that now
	}

	public void writeMatrix() {
		long start = System.nanoTime();
		output.accept(matrix);
		long time = System.nanoTime() - start;
		this.times.add(time);
		System.out.println(this.times.stream().mapToDouble(a -> a).average());
	}

}
