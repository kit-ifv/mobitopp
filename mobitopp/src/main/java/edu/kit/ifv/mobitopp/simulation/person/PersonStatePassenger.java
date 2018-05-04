package edu.kit.ifv.mobitopp.simulation.person;


import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.events.DemandSimulationEventIfc;
import edu.kit.ifv.mobitopp.simulation.events.Event;
import edu.kit.ifv.mobitopp.time.Time;

public enum PersonStatePassenger
	implements PersonState
{

	FINISHED(false) {
		
		@Override
		public void doActionAtStart(SimulationPerson person, Time currentTime) {
			person.endActivity();
		}
	},

	EXECUTE_LAST_ACTIVITY(true, FINISHED) {
		@Override
		public void doActionAtStart(SimulationPerson person, Time currentTime) {
			person.startActivity(person.currentTrip(), person.options().rescheduling(), currentTime);
		}
	},

	EXECUTE_ACTIVITY_MODE_DESTINATION_CHOICE(true) {
		
		@Override
		public void doActionAtStart(SimulationPerson person, Time currentTime) {
			person.selectDestinationAndMode(person.options().destinationChoiceModel(),
					person.options().modeChoiceModel(), person.options().impedance(), true);

			person.allocateCar(person.options().impedance(), person.currentTrip(),
					person.currentTrip().startDate());

			person.offerRide(currentTime, person.options());
		}

		public PersonState nextState(SimulationPerson person, Time currentTime) {
			if (person.currentTrip().mode() == Mode.PASSENGER) {
				return PersonStatePassenger.EXECUTE_ACTIVITY_CHECKRIDEOFFER;
			} else {
				return PersonStatePassenger.EXECUTE_ACTIVITYMODECHOSEN;
			}
		}
	},


	EXECUTE_ACTIVITY(EXECUTE_ACTIVITY_MODE_DESTINATION_CHOICE) {
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

	START_ACTIVITY(true) {

		public PersonState nextState(SimulationPerson person, Time currentTime) {
			if  (person.hasNextActivity()) {
				if ( person.nextActivityStartsAfterSimulationEnd()) {
					return PersonStatePassenger.EXECUTE_ACTIVITY_ENDING_LATE;
				}
				return PersonStatePassenger.EXECUTE_ACTIVITY;
			} else {
				return PersonStatePassenger.EXECUTE_LAST_ACTIVITY;
			}
		}
	},

	MAKE_TRIP(START_ACTIVITY) {
		
		@Override
		public void doActionAtStart(SimulationPerson person, Time currentTime) {
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
	ROUTE_CHOICE(true, MAKE_TRIP) {
		@Override
		public void doActionAtStart(SimulationPerson person, Time currentTime) {
			person.selectRoute( person.options().routeChoice(), person.currentTrip(), currentTime);
		}
	},
	PREPARE_TRIP(true, ROUTE_CHOICE) {

		@Override
		public void doActionAtStart(SimulationPerson person, Time currentTime) {
			person.revokeRideOffer(person.options().rideSharingOffers(), person.currentTrip(),
					currentTime);
		}
	},
	ACTIVITY_FINISHED(true, PREPARE_TRIP) {
		@Override
		public void doActionAtStart(SimulationPerson person, Time currentTime) {
			person.endActivity();
		}
	},
	EXECUTE_ACTIVITYMODECHOSEN(false, ACTIVITY_FINISHED) {
		@Override
		public Optional<DemandSimulationEventIfc> nextEvent(
				SimulationPerson person, Time currentDate) {
			return Optional.of(Event.modeChosen(person, person.currentActivity()));
		}
	},

	EXECUTE_ACTIVITY_CHECKRIDEOFFER(true) {
		
		@Override
		public void doActionAtStart(SimulationPerson person, Time currentTime) {
			person.findAndAcceptBestMatchingRideOffer(person.options().rideSharingOffers(),
					person.currentTrip(), person.options().maxDifferenceMinutes());
		}
		
		@Override
		public PersonState nextState(SimulationPerson person, Time currentTime) {
			if (person.rideOfferAccepted()) {
				return PersonStatePassenger.EXECUTE_ACTIVITYMODECHOSEN;
			} else {
				if (currentTime.isBefore(person.currentActivity().calculatePlannedEndDate())) {
					 return PersonStatePassenger.EXECUTE_ACTIVITY_WAIT4RIDE;
				} else {
					return PersonStatePassenger.EXECUTE_ACTIVITYNORIDE;
				}
			}
		}
	},

	EXECUTE_ACTIVITY_WAIT4RIDE(EXECUTE_ACTIVITY_CHECKRIDEOFFER) {
		@Override
		public Optional<DemandSimulationEventIfc> nextEvent(
				SimulationPerson person, Time currentDate) {
			return Optional.of(Event.waitForRide(person, person.currentActivity()));
		}
	},
	ACTIVITY_FINISHED_NO_RIDE(true, PREPARE_TRIP) {
		@Override
		public void doActionAtStart(SimulationPerson person, Time currentTime) {
			person.endActivity();
		}
	},
	EXECUTE_ACTIVITYNORIDE(ACTIVITY_FINISHED_NO_RIDE) {
		
		@Override
		public void doActionAtStart(SimulationPerson person, Time currentTime) {
			person.selectDestinationAndMode(person.options().destinationChoiceModel(),
					person.options().modeChoiceModel(), person.options().impedance(), false);

			person.allocateCar(person.options().impedance(), person.currentTrip(),
					person.currentTrip().startDate());

			person.offerRide(currentTime, person.options());
		}
		@Override
		public Optional<DemandSimulationEventIfc> nextEvent(
				SimulationPerson person, Time currentDate) {
			return Optional.of(Event.modeChosen(person, person.currentActivity()));
		}
	},
	EXECUTE_ACTIVITY_ENDING_LATE(true,EXECUTE_ACTIVITY),
	UNINITIALIZED(false,START_ACTIVITY)
	; 		

	private final boolean instantaneous;
	private final PersonStatePassenger nextState;

	private PersonStatePassenger(boolean instantaneous, PersonStatePassenger nextState) {
		this.instantaneous = instantaneous;
		this.nextState = nextState;
	}

	private PersonStatePassenger(boolean instantaneous) {
		this(instantaneous, null);
	}

	private PersonStatePassenger(PersonStatePassenger nextState) {
		this(false, nextState);
	}

	private PersonStatePassenger() {
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

	@Override
	public void doActionAtStart(SimulationPerson person, Time currentTime) {
	}
	
	@Override
	public void doActionAtEnd(SimulationPerson person, Time currentTime) {
	}

	@Override
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
