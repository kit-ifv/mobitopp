package edu.kit.ifv.mobitopp.simulation.person;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.PassengerEvent.board;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.PassengerEvent.getOff;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.PassengerEvent.wait;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class EventsTest {

	private static final int singleWaitTime = 1;
	private static final int singleInVehicleTime = 2;
	private static final int singleLegTime = singleWaitTime + singleInVehicleTime;
	private static final int numberOfLegs = 3;

	private Journey someJourney;
	private Time someDate;
	private Events events;
	private int legs;

	@Before
	public void initialise() {
		someJourney = mock(Journey.class);
		someDate = someTime();
		events = new Events();
	}

	@Test
	public void inVehicleTime() {
		createLegs();

		Statistic statistic = events.statistic();

		assertThat(statistic.get(Element.inVehicle), is(equalTo(overallInVehicleTime())));
	}

	private RelativeTime overallInVehicleTime() {
		return RelativeTime.of(singleInVehicleTime * numberOfLegs, MINUTES);
	}

	@Test
	public void waitingTime() {
		createLegs();

		Statistic statistic = events.statistic();

		assertThat(statistic.get(Element.waiting), is(equalTo(overallWaitingTime())));
	}

	private RelativeTime overallWaitingTime() {
		return RelativeTime.of(singleWaitTime * numberOfLegs, MINUTES);
	}

	@Test
	public void startWaitTime() {
		createLegs();
		RelativeTime startWaitTime = RelativeTime.of(singleWaitTime, MINUTES);

		Statistic statistic = events.statistic();

		assertThat(statistic.get(Element.startWaiting), is(equalTo(startWaitTime)));
	}

	@Test
	public void inBetweenWaitTime() {
		createLegs();
		RelativeTime inBetweenWaitTime = RelativeTime.of((legs - 1) * singleWaitTime, MINUTES);

		Statistic statistic = events.statistic();

		assertThat(statistic.get(Element.inBetweenWaiting), is(equalTo(inBetweenWaitTime)));
	}

	@Test
	public void rejectedBoarding() {
		Time startOfLeg = someDate;
		Time rejectedBoarding = startOfLeg.plusMinutes(singleWaitTime);
		Time boarding = rejectedBoarding.plusMinutes(singleWaitTime);
		Time exiting = boarding.plusMinutes(singleInVehicleTime);
		events.add(wait(startOfLeg));
		events.add(wait(rejectedBoarding));
		events.add(board(boarding));
		events.add(getOff(exiting));

		RelativeTime rejectedWaitingTime = RelativeTime.of(2 * singleWaitTime, MINUTES);
		assertThat(events.waitingTime(), is(equalTo(rejectedWaitingTime)));
		RelativeTime inVehicleTime = RelativeTime.of(singleInVehicleTime, MINUTES);
		assertThat(events.inVehicleTime(), is(equalTo(inVehicleTime)));
	}

	private void createLegs() {
		for (int i = 0; i < numberOfLegs; i++) {
			addLeg();
		}
	}

	private void addLeg() {
		Time startOfLeg = someDate.plusMinutes(legs * singleLegTime);
		Time boardingTime = startOfLeg.plusMinutes(singleWaitTime);
		Time exitTime = startOfLeg.plusMinutes(singleLegTime);
		events.add(wait(startOfLeg));
		events.add(board(boardingTime));
		events.add(getOff(exitTime));
		legs++;
	}

	private Event wait(Time startOfLeg) {
		return new Event(wait, startOfLeg, someJourney);
	}

	private Event board(Time boardingTime) {
		return new Event(board, boardingTime, someJourney);
	}

	private Event getOff(Time exitTime) {
		return new Event(getOff, exitTime, someJourney);
	}
}
