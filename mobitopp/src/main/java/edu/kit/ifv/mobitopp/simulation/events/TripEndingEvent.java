package edu.kit.ifv.mobitopp.simulation.events;

import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.PersonResults;
import edu.kit.ifv.mobitopp.simulation.TripIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.OccupationIfc;
import edu.kit.ifv.mobitopp.simulation.person.BeamedTrip;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;

public class TripEndingEvent extends DemandSimulationEvent {

	public TripEndingEvent(int priority, SimulationPerson person, OccupationIfc occupation) {
		super(priority, person, occupation, occupation.calculatePlannedEndDate());
	}
	
	@Override
	public void writeRemaining(PersonResults results) {
		super.writeRemaining(results);
    Person person = getPerson();

    TripIfc trip = person.currentTrip();

		assert trip != null;
		assert trip == getOccupation();

		ActivityIfc prevActivity = trip.previousActivity();
		ActivityIfc activity = trip.nextActivity();

		assert activity != null : person.activitySchedule().toString();
		assert prevActivity != null;

		FinishedTrip finishedTrip = new BeamedTrip(trip, trip.calculatePlannedEndDate());
		results.notifyEndTrip(person, finishedTrip, activity);
		if (person.isCarDriver()) {
			results.notifyFinishCarTrip(person, person.whichCar(), trip, activity);
		}
	}
}
