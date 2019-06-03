package edu.kit.ifv.mobitopp.simulation.person;

import static edu.kit.ifv.mobitopp.simulation.publictransport.model.PassengerEvent.board;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.PassengerEvent.getOff;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.PassengerEvent.wait;

import java.util.LinkedList;
import java.util.List;

import edu.kit.ifv.mobitopp.simulation.publictransport.model.PassengerEvent;
import edu.kit.ifv.mobitopp.time.RelativeTime;

public class Events {

	private final List<Event> events;

	public Events() {
		super();
		events = new LinkedList<>();
	}

	public void add(Event event) {
		events.add(event);
	}

	public RelativeTime inVehicleTime() {
		return durationBetween(board, getOff);
	}

	public RelativeTime waitingTime() {
		return durationBetween(wait, board);
	}

	public RelativeTime startWaitTime() {
		Event firstWait = firstWait();
		Event firstBoard = firstBoard();
		if (null != firstWait && null != firstBoard) {
			int duration = firstBoard.time().differenceTo(firstWait.time()).toMinutes();
			return RelativeTime.ofMinutes(duration);
		}
		return RelativeTime.ZERO;
	}

	private Event firstBoard() {
		for (Event event : events) {
			if (board.equals(event.type())) {
				return event;
			}
		}
		return null;
	}

	private Event firstWait() {
		for (Event event : events) {
			if (board.equals(event.type())) {
				return null;
			}
			if (wait.equals(event.type())) {
				return event;
			}
		}
		return null;
	}

	public RelativeTime inBetweenWaitTime() {
		Event lastStart = null;
		int duration = 0;
		boolean beforeFirstBoarding = true;
		for (Event event : events) {
			if (beforeFirstBoarding && !board.equals(event.type())) {
				continue;
			}
			beforeFirstBoarding = false;
			if (lastStart != null && board.equals(event.type())) {
				duration += event.time().differenceTo(lastStart.time()).toMinutes();
				lastStart = null;
			}
			if (lastStart == null && wait.equals(event.type())) {
				lastStart = event;
			}
		}
		return RelativeTime.ofMinutes(duration);
	}

	public Statistic statistic() {
		Statistic statistic = new Statistic();
		statistic.add(Element.waiting, waitingTime());
		statistic.add(Element.inVehicle, inVehicleTime());
		statistic.add(Element.startWaiting, startWaitTime());
		statistic.add(Element.inBetweenWaiting, inBetweenWaitTime());
		return statistic;
	}

	private RelativeTime durationBetween(PassengerEvent start, PassengerEvent end) {
		Event lastStart = null;
		int duration = 0;
		for (Event event : events) {
			if (lastStart != null && end.equals(event.type())) {
				duration += event.time().differenceTo(lastStart.time()).toMinutes();
				lastStart = null;
			}
			if (lastStart == null && start.equals(event.type())) {
				lastStart = event;
			}
		}
		return RelativeTime.ofMinutes(duration);
	}

}
