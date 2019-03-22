package edu.kit.ifv.mobitopp.simulation;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;

public class StatisticResults extends PersonResultsDecorator implements PersonResults {

	private final Collection<FinishedTrip> trips;
	private final Collection<StateChange> stateChanges;

	public StatisticResults(PersonResults other) {
		super(other);
		trips = new LinkedList<>();
		stateChanges = new LinkedList<>();
	}

	@Override
	public void notifyEndTrip(Person person, FinishedTrip trip, ActivityIfc activity) {
		super.notifyEndTrip(person, trip, activity);
		trips.add(trip);
	}

	@Override
	public void notifyStateChanged(StateChange change) {
		super.notifyStateChanged(change);
		stateChanges.add(change);
	}

	public Collection<FinishedTrip> trips() {
		return Collections.unmodifiableCollection(trips);
	}
	
	public Collection<StateChange> stateChanges() {
		return Collections.unmodifiableCollection(stateChanges);
	}

}
