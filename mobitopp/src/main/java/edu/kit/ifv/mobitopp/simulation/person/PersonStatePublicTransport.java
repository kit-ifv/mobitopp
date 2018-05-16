package edu.kit.ifv.mobitopp.simulation.person;

import java.util.Optional;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.publictransport.model.FootJourney;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.TripIfc;
import edu.kit.ifv.mobitopp.simulation.events.DemandSimulationEventIfc;
import edu.kit.ifv.mobitopp.simulation.events.Event;
import edu.kit.ifv.mobitopp.time.Time;

public enum PersonStatePublicTransport implements PersonState {
	FINISHED {
		
		@Override
		public Optional<DemandSimulationEventIfc> nextEvent(
				SimulationPerson person, Time currentDate) {
			return Optional.empty();
		}

		@Override
		public PersonState nextState(SimulationPerson person, Time currentTime) {
			throw new AssertionError();
		}
	},

	MAKE_TRIP {

		@Override
		public PersonState nextState(SimulationPerson person, Time currentTime) {
			return person.hasNextActivity() ? EXECUTE_ACTIVITY : FINISHED;
		}

		@Override
		public void doActionAtStart(SimulationPerson person, Time currentTime) {
			person.selectRoute(person.options().routeChoice(), person.currentTrip(), currentTime);
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

	FINISH_PUBLIC_TRANSPORT {

		@Override
		public PersonState nextState(SimulationPerson person, Time currentTime) {
			return person.hasNextActivity() ? EXECUTE_ACTIVITY : FINISHED;
		}

		@Override
		public void doActionAtEnd(SimulationPerson person, Time currentTime) {
			person.endTrip(person.options().impedance(), person.options().rescheduling(), currentTime);
		}

		@Override
		public Optional<DemandSimulationEventIfc> nextEvent(
				SimulationPerson person, Time currentDate) {
			TripIfc currentTrip = person.currentTrip();
			return Optional.of(Event.tripEnding(person, currentTrip));
		}
	},

	WAIT_FOR_VEHICLE {

		@Override
		public PersonState nextState(SimulationPerson person, Time currentTime) {
			return SEARCH_VEHICLE;
		}

		@Override
		public void doActionAtStart(SimulationPerson person, Time currentTime) {
			person.wait(currentTime);
		}

		@Override
		public Optional<DemandSimulationEventIfc> nextEvent(
				SimulationPerson person, Time currentDate) {
			return Optional.empty();
		}
	},

	RIDE_VEHICLE {

		@Override
		public PersonState nextState(SimulationPerson person, Time currentTime) {
			if (person.hasArrivedAtNextActivity()) {
				return FINISH_PUBLIC_TRANSPORT;
			}
			return SEARCH_VEHICLE;
		}

		@Override
		public void doActionAtStart(SimulationPerson person, Time currentTime) {
			person.boardPublicTransportVehicle(currentTime);
		}

		@Override
		public void doActionAtEnd(SimulationPerson person, Time currentTime) {
			person.getOffPublicTransportVehicle(currentTime);
		}

		@Override
		public Optional<DemandSimulationEventIfc> nextEvent(
				SimulationPerson person, Time currentDate) {
			if (isWalking(person)) {
				Time endOfRide = endOfRide(person, currentDate);
				return Optional.of(Event.exitVehicle(person, person.currentTrip(), endOfRide));
			}
			return Optional.empty();
		}

		private boolean isWalking(SimulationPerson person) {
			if (person.currentTrip() instanceof PublicTransportTrip) {
				return ((PublicTransportTrip) person.currentTrip())
						.currentLeg()
						.map(PublicTransportLeg::journey)
						.map(FootJourney.footJourney::equals)
						.orElse(false);
			}
			return false;
		}

		private Time endOfRide(SimulationPerson person, Time currentDate) {
			PublicTransportTrip currentTrip = (PublicTransportTrip) person.currentTrip();
			return currentTrip
					.currentLeg()
					.map(PublicTransportLeg::arrival)
					.orElse(currentDate);
		}
	},

	TRY_BOARDING {

		@Override
		public PersonState nextState(SimulationPerson person, Time currentTime) {
			return person.hasPlaceInPublicTransportVehicle() ? RIDE_VEHICLE : SEARCH_VEHICLE;
		}

		@Override
		public void doActionAtStart(SimulationPerson person, Time currentTime) {
			if (person.hasPlaceInPublicTransportVehicle()) {
				return;
			}
			person.changeToNewTrip(currentTime);
		}

		@Override
		public boolean instantaneous() {
			return true;
		}

	},

	SEARCH_VEHICLE {

		@Override
		public PersonState nextState(SimulationPerson person, Time currentTime) {
			if (person.hasPublicTransportVehicleDeparted(currentTime)) {
				return SEARCH_VEHICLE;
			}
			return person.isPublicTransportVehicleAvailable(currentTime) ? TRY_BOARDING
					: WAIT_FOR_VEHICLE;
		}

		@Override
		public boolean instantaneous() {
			return true;
		}
		
		@Override
		public void doActionAtStart(SimulationPerson person, Time currentTime) {
			if (person.hasPublicTransportVehicleDeparted(currentTime)) {
				person.changeToNewTrip(currentTime);
			}
		}
	},

	USE_OTHER_MODE {

		@Override
		public PersonState nextState(SimulationPerson person, Time currentTime) {
			return MAKE_TRIP;
		}

		@Override
		public boolean instantaneous() {
			return true;
		}

		@Override
		public void doActionAtStart(SimulationPerson person, Time currentTime) {
			TripIfc currentTrip = person.currentTrip();
			person.allocateCar(person.options().impedance(), currentTrip, currentTrip.startDate());
		}

	},

	USE_PUBLIC_TRANSPORT {

		@Override
		public PersonState nextState(SimulationPerson person, Time currentTime) {
			return SEARCH_VEHICLE;
		}

		@Override
		public void doActionAtStart(SimulationPerson person, Time currentTime) {
			person.selectRoute(person.options().routeChoice(), person.currentTrip(), currentTime);
			person.startTrip(person.options().impedance(), person.currentTrip(), currentTime);
		}
		
		@Override
		public void doActionAtEnd(SimulationPerson person, Time currentTime) {
			person.enterFirstStop(currentTime);
		}
		
		@Override
		public Optional<DemandSimulationEventIfc> nextEvent(SimulationPerson person, Time currentDate) {
			TripIfc currentTrip = person.currentTrip();
			Function<Time, DemandSimulationEventIfc> toEvent = departure -> Event.enterStartStop(person,
					currentTrip, departure);
			return currentTrip.timeOfNextChange().map(toEvent);
		}
		
	},

	SELECT_MODE {

		@Override
		public PersonState nextState(SimulationPerson person, Time currentTime) {
			if (Mode.PUBLICTRANSPORT.equals(person.currentTrip().mode())) {
				return USE_PUBLIC_TRANSPORT;
			}
			return USE_OTHER_MODE;
		}

		@Override
		public boolean instantaneous() {
			return true;
		}

		@Override
		public void doActionAtStart(SimulationPerson person, Time currentTime) {
			SimulationOptions options = person.options();
			person.selectDestinationAndMode(options.destinationChoiceModel(), options.modeChoiceModel(),
					options.impedance(), true);
		}

	},

	EXECUTE_ACTIVITY {

		@Override
		public void doActionAtStart(SimulationPerson person, Time currentTime) {
			person.startActivity(person.currentTrip(), person.options().rescheduling(), currentTime);
		}

		@Override
		public void doActionAtEnd(SimulationPerson person, Time currentTime) {
			person.endActivity();
		}

		@Override
		public Optional<DemandSimulationEventIfc> nextEvent(
				SimulationPerson person, Time currentDate) {
			return Optional.of(Event.activityEnding(person, person.currentActivity()));
		}

		@Override
		public PersonState nextState(SimulationPerson person, Time currentTime) {
			return SELECT_MODE;
		}
	},

	UNINITIALIZED {

		@Override
		public Optional<DemandSimulationEventIfc> nextEvent(
				SimulationPerson person, Time currentDate) {
			return Optional.empty();
		}
		
		@Override
		public PersonState nextState(SimulationPerson person, Time currentTime) {
			return EXECUTE_ACTIVITY;
		}
	};

	@Override
	public boolean instantaneous() {
		return false;
	}

	@Override
	public abstract PersonState nextState(SimulationPerson person, Time currentTime);

	@Override
	public void doActionAtStart(SimulationPerson person, Time currentTime) {
	}

	@Override
	public void doActionAtEnd(SimulationPerson person, Time currentTime) {
	}

	/**
	 * Must be implemented, when {@link #instantaneous()} returns <code>false</code>.
	 */
	@Override
	public Optional<DemandSimulationEventIfc> nextEvent(
			SimulationPerson person, Time currentDate) {
		assert false : person.currentState();

		throw new AssertionError();
	}

}
