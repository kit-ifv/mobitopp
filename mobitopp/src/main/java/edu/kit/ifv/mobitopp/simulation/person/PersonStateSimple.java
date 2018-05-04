package edu.kit.ifv.mobitopp.simulation.person;

import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.events.DemandSimulationEventIfc;
import edu.kit.ifv.mobitopp.simulation.events.Event;
import edu.kit.ifv.mobitopp.time.Time;

public enum PersonStateSimple implements PersonState {

	FINISHED(false) {

		@Override
		public Optional<DemandSimulationEventIfc> nextEvent(
				SimulationPerson person, Time currentDate) {
			return Optional.empty();
		}
	},


	MAKE_TRIP(false){
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
		public PersonState nextState(SimulationPerson person, Time currentTime) {
			if  (person.hasNextActivity()) {
				return EXECUTE_ACTIVITY;
			} else {
				return FINISHED;
			}
		}
		@Override
		public Optional<DemandSimulationEventIfc> nextEvent(
				SimulationPerson person, Time currentDate) {
			return Optional.of(Event.tripEnding(person, person.currentTrip()));
		}
	},

	EXECUTE_ACTIVITY(MAKE_TRIP) {
		
		@Override
		public void doActionAtStart(SimulationPerson person, Time currentTime) {
			person.startActivity(person.currentTrip(), person.options().rescheduling(), currentTime);
		}
		
		@Override
		public void doActionAtEnd(SimulationPerson person, Time currentTime) {
			person.endActivity();
			person.selectDestinationAndMode(person.options().destinationChoiceModel(),
					person.options().modeChoiceModel(), person.options().impedance(), true);
			person.allocateCar(person.options().impedance(), person.currentTrip(),
					person.currentTrip().startDate());
		}
		
		@Override
		public Optional<DemandSimulationEventIfc> nextEvent(
				SimulationPerson person, Time currentDate) {
			return Optional.of(Event.activityEnding(person, person.currentActivity()));
		}
	},			

	UNINITIALIZED(false,EXECUTE_ACTIVITY) {

		@Override
		public Optional<DemandSimulationEventIfc> nextEvent(
				SimulationPerson person, Time currentDate) {
			return Optional.empty();
		}
	}								
	; 		

	private final boolean instantaneous;
	private final PersonStateSimple nextState;

	private PersonStateSimple(boolean instantaneous, PersonStateSimple nextState) {
		this.instantaneous = instantaneous;
		this.nextState = nextState;
	}

	private PersonStateSimple(boolean instantaneous) {
		this(instantaneous, null);
	}

	private PersonStateSimple(PersonStateSimple nextState) {
		this(false, nextState);
	}

	private PersonStateSimple() {
		this(false,null);
	}

	public boolean instantaneous() {
		return this.instantaneous;
	}

	public PersonState nextState(SimulationPerson person, Time currentTime) {
		if (this.nextState != null) {
			return this.nextState;
		}
		throw new AssertionError();
	}

	public void doActionAtStart(SimulationPerson person, Time currentTime) {
	}

	public void doActionAtEnd(SimulationPerson person, Time currentTime) {
	}

	public abstract Optional<DemandSimulationEventIfc> nextEvent(
			SimulationPerson person, Time currentDate);
}
