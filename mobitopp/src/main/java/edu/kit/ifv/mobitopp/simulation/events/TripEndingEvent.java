package edu.kit.ifv.mobitopp.simulation.events;

import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.PersonListener;
import edu.kit.ifv.mobitopp.simulation.Trip;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.OccupationIfc;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;
import edu.kit.ifv.mobitopp.time.Time;

public class TripEndingEvent extends DemandSimulationEvent {

	public TripEndingEvent(int priority, SimulationPerson person, OccupationIfc occupation, Time plannedEndDate) {
		super(priority, person, occupation, plannedEndDate);
	}
	
	public TripEndingEvent(int priority, SimulationPerson person, OccupationIfc occupation) {
		this(priority, person, occupation, occupation.calculatePlannedEndDate());
	}
	
	@Override
	public void writeRemaining(PersonListener listener) {
		super.writeRemaining(listener);
    Person person = getPerson();

    Trip trip = person.currentTrip();

		assert trip != null;
		assert trip == getOccupation();

		ActivityIfc prevActivity = trip.previousActivity();
		ActivityIfc activity = trip.nextActivity();

		assert activity != null : person.activitySchedule().toString();
		assert prevActivity != null;

		FinishedTrip finishedTrip = trip.finish(trip.calculatePlannedEndDate(), listener);
		listener.notifyEndTrip(person, finishedTrip);
		if (person.isCarDriver()) {
			listener.notifyFinishCarTrip(person, person.whichCar(), finishedTrip, activity);
		}
	}
}
