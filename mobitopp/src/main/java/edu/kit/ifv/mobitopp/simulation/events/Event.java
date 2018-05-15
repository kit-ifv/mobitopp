package edu.kit.ifv.mobitopp.simulation.events;

import edu.kit.ifv.mobitopp.simulation.TripIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.OccupationIfc;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;
import edu.kit.ifv.mobitopp.time.Time;

public abstract class Event {

	public static DemandSimulationEventIfc activityEnding(
			SimulationPerson person, ActivityIfc activity) {
		return activityEnding(person, activity, activity.calculatePlannedEndDate());
	}

	public static DemandSimulationEventIfc activityEnding(
			SimulationPerson person, ActivityIfc activity, Time eventDate) {
		return new DemandSimulationEvent(20, person, activity, eventDate);
	}

	public static DemandSimulationEventIfc tripEnding(
			SimulationPerson person, OccupationIfc occupation) {
		return new TripEndingEvent(10, person, occupation);
	}

	public static DemandSimulationEventIfc waitForRide(SimulationPerson person, ActivityIfc activity) {
		return new DemandSimulationEvent(30, person, activity, activity.calculatePlannedEndDate());
	}

	public static DemandSimulationEventIfc modeChosen(SimulationPerson person, ActivityIfc activity) {
		return new DemandSimulationEvent(40, person, activity, activity.calculatePlannedEndDate());
	}

	public static DemandSimulationEventIfc exitVehicle(
			SimulationPerson person, TripIfc trip, Time date) {
		return new DemandSimulationEvent(0, person, trip, date);
	}
	
	public static DemandSimulationEventIfc enterStartStop(
			SimulationPerson person, TripIfc trip, Time date) {
		return new DemandSimulationEvent(10, person, trip, date);
	}

}
