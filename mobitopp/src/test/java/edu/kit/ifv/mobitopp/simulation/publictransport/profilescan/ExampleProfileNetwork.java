package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import static edu.kit.ifv.mobitopp.publictransport.model.ConnectionBuilder.connection;
import static edu.kit.ifv.mobitopp.publictransport.model.JourneyBuilder.journey;
import static edu.kit.ifv.mobitopp.publictransport.model.StopBuilder.stop;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;

class ExampleProfileNetwork {

	public Stop someStop;
	public Stop anotherStop;
	public Stop otherStop;
	public Stop yetAnotherStop;
	public Connection fromSomeToAnother;
	public Journey someJourney;
	public Journey yetAnotherJourney;
	public Connection fromAnotherToOther;
	public Connection fromOtherToYetAnother;
	public Connection laterFromSomeToAnother;
	public Connection laterFromAnotherToOther;
	public Journey otherJourney;
	public Journey laterJourney;
	private int journeyId;

	public ExampleProfileNetwork() {
		super();
		journeyId = 0;
		someStop = createSomeStop();
		anotherStop = createAnotherStop();
		otherStop = createOtherStop();
		yetAnotherStop = createYetAnotherStop();
		createSomeJourney();
		createOtherJourney();
		createLaterJourney();
		createYetAnotherJourney();
	}

	private Stop createSomeStop() {
		return stop()
				.withExternalId(1)
				.withId(0)
				.withName("some stop")
				.with(Data.coordinate(0, 0))
				.build();
	}

	private Stop createAnotherStop() {
		return stop()
				.withExternalId(2)
				.withId(1)
				.withName("another stop")
				.with(Data.coordinate(1, 2))
				.build();
	}

	private Stop createOtherStop() {
		return stop()
				.withExternalId(3)
				.withId(2)
				.withName("other stop")
				.with(Data.coordinate(3, 4))
				.build();
	}

	private Stop createYetAnotherStop() {
		return stop()
				.withExternalId(3)
				.withId(3)
				.withName("yet another stop")
				.with(Data.coordinate(5, 6))
				.build();
	}

	private void createSomeJourney() {
		someJourney = createJourney();
		fromSomeToAnother = connection()
				.withId(1)
				.startsAt(someStop)
				.endsAt(anotherStop)
				.departsAt(Data.someTime())
				.arrivesAt(Data.oneMinuteLater())
				.partOf(someJourney)
				.build();
		someJourney.connections().add(fromSomeToAnother);
	}

	private void createOtherJourney() {
		otherJourney = createJourney();
		fromAnotherToOther = connection()
				.withId(6)
				.startsAt(anotherStop)
				.endsAt(otherStop)
				.departsAt(Data.oneMinuteLater())
				.arrivesAt(Data.twoMinutesLater())
				.partOf(otherJourney)
				.build();
		otherJourney.connections().add(fromAnotherToOther);
	}

	private void createYetAnotherJourney() {
		yetAnotherJourney = createJourney();
		fromOtherToYetAnother = connection()
				.withId(7)
				.startsAt(otherStop)
				.endsAt(yetAnotherStop)
				.departsAt(Data.twoMinutesLater())
				.arrivesAt(Data.threeMinutesLater())
				.partOf(yetAnotherJourney)
				.build();
		yetAnotherJourney.connections().add(fromAnotherToOther);
	}

	private ModifiableJourney createJourney() {
		return journey().withId(journeyId++).build();
	}

	private void createLaterJourney() {
		laterJourney = createJourney();
		laterFromSomeToAnother = connection()
				.withId(2)
				.startsAt(Data.someStop())
				.endsAt(Data.anotherStop())
				.departsAt(Data.oneMinuteLater())
				.arrivesAt(Data.twoMinutesLater())
				.partOf(laterJourney)
				.build();
		laterFromAnotherToOther = connection()
				.withId(5)
				.startsAt(Data.anotherStop())
				.endsAt(Data.otherStop())
				.departsAt(Data.twoMinutesLater())
				.arrivesAt(Data.threeMinutesLater())
				.partOf(laterJourney)
				.build();
		laterJourney.connections().add(laterFromSomeToAnother);
		laterJourney.connections().add(laterFromAnotherToOther);
	}

}
