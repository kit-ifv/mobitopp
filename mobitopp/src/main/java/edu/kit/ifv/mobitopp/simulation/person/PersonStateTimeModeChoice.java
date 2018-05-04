package edu.kit.ifv.mobitopp.simulation.person;

import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.events.DemandSimulationEventIfc;
import edu.kit.ifv.mobitopp.simulation.events.Event;
import edu.kit.ifv.mobitopp.time.Time;

public enum PersonStateTimeModeChoice implements PersonState {

	FINISHED(false) {

		@Override
		public void doActionAtStart(SimulationPerson person, Time currentTime) {
			person.endActivity();
		}

		@Override
		public Optional<DemandSimulationEventIfc> nextEvent(
				SimulationPerson person, Time currentDate) {
			return Optional.empty();
		}
	},

	EXECUTE_LAST_ACTIVITY(true, FINISHED) {
		@Override
		public void doActionAtStart(SimulationPerson person, Time currentTime) {
			person.startActivity(person.currentTrip(), person.options().rescheduling(), currentTime);
		}
	},									


	START_ACTIVITY(true) {
		@Override
		public PersonState nextState(SimulationPerson person, Time currentTime) {
			if  (person.hasNextActivity()) {
				return EXECUTE_ACTIVITY;
			} else {
				return EXECUTE_LAST_ACTIVITY;
			}
		}
	},

	MAKE_TRIP(START_ACTIVITY) {
		
		@Override
		public void doActionAtStart(SimulationPerson person, Time currentTime) {
			person.selectRoute( person.options().routeChoice(), person.currentTrip(), currentTime);
			person.startTrip(person.options().impedance(), person.currentTrip(), currentTime);
		}
		
		@Override
		public void doActionAtEnd(SimulationPerson person, Time currentTime) {
			person.endTrip(person.options().impedance(), person.options().rescheduling(), currentTime);
		}

		@Override
		public Optional<DemandSimulationEventIfc> nextEvent(
				SimulationPerson person, Time currentDate) {
			return Optional.of(Event.tripEnding(person, person.currentTrip()));
		}
	},
	
	ACTIVITY_FINISHED(true, MAKE_TRIP) {
		@Override
		public void doActionAtStart(SimulationPerson person, Time currentTime) {
			person.endActivity();
			person.selectDestinationAndMode(person.options().destinationChoiceModel(),
					person.options().modeChoiceModel(), person.options().impedance(), true);
			person.allocateCar(person.options().impedance(), person.currentTrip(),
					person.currentTrip().startDate());
		}
	},			
	EXECUTE_ACTIVITY(ACTIVITY_FINISHED) {
		
		@Override
		public void doActionAtStart(SimulationPerson person, Time currentTime) {
			person.startActivity(person.currentTrip(), person.options().rescheduling(), currentTime);
		}

		@Override
		public Optional<DemandSimulationEventIfc> nextEvent(
				SimulationPerson person, Time currentDate) {
			Time modeChoiceDate = dateForDestinationAndModeChoice(person.currentActivity(),
					person.options().maxDifferenceMinutes());
			return Optional.of(Event.activityEnding(person, person.currentActivity(), modeChoiceDate));
		}
	},			

	UNINITIALIZED(false,START_ACTIVITY)								
	;	

	private final boolean instantaneous;
	private final PersonStateTimeModeChoice nextState;

	private PersonStateTimeModeChoice(boolean instantaneous, PersonStateTimeModeChoice nextState) {
		this.instantaneous = instantaneous;
		this.nextState = nextState;
	}

	private PersonStateTimeModeChoice(boolean instantaneous) {
		this(instantaneous, null);
	}

	private PersonStateTimeModeChoice(PersonStateTimeModeChoice nextState) {
		this(false, nextState);
	}

	private PersonStateTimeModeChoice() {
		this(false,null);
	}

	public boolean instantaneous() {
		return this.instantaneous;
	}

	public PersonState nextState(SimulationPerson person, Time currentTime) {
		if (this.nextState != null)
			return this.nextState;
		throw new AssertionError();
	}

	public void doActionAtStart(SimulationPerson person, Time currentTime) {
	}

	public void doActionAtEnd(SimulationPerson person, Time currentTime){
	}

	public Optional<DemandSimulationEventIfc> nextEvent(
			SimulationPerson person, Time currentDate) {
		throw new AssertionError("No next event for this state configured: " + person.currentState());
	}

	private static Time dateForDestinationAndModeChoice(
		ActivityIfc currentActivity,
		int minutes_before
	) {

		Time activityStart = currentActivity.startDate();

		if (currentActivity.duration() > minutes_before) {

			return activityStart.plusMinutes( currentActivity.duration() - minutes_before);
		} else {
			return activityStart;
		}
	}
	
}

