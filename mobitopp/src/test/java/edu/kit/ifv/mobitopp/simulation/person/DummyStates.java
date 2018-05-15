package edu.kit.ifv.mobitopp.simulation.person;

import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.events.DemandSimulationEventIfc;
import edu.kit.ifv.mobitopp.time.Time;

public enum DummyStates implements PersonState {
	some {

		@Override
		public PersonState nextState(SimulationPerson person, Time currentTime) {
			return another;
		}
	},
	another {

		@Override
		public PersonState nextState(SimulationPerson person, Time currentTime) {
			return some;
		}
	};

	@Override
	public boolean instantaneous() {
		return false;
	}

	@Override
	public void doActionAtStart(SimulationPerson person, Time currentTime) {
	}

	@Override
	public void doActionAtEnd(SimulationPerson person, Time currentTime) {
	}

	@Override
	public Optional<DemandSimulationEventIfc> nextEvent(
			SimulationPerson person, Time currentDate) {
		return Optional.empty();
	}

}
