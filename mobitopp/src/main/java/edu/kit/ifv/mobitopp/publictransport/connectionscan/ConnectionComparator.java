package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.Comparator;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;

class ConnectionComparator implements Comparator<Connection> {
	private static final int orderIsNotRelevant = 0;
	private static final int firstIsLess = -1;
	private static final int secondIsLess = 1;

	@Override
	public int compare(Connection first, Connection second) {
		int departureDifference = first.departure().compareTo(second.departure());
		if (departureDifference == 0) {
			int arrivalDifference = first.arrival().compareTo(second.arrival());
			if (arrivalDifference == 0) {
				return endBeforeStart(first, second);
			}
			return arrivalDifference;
		}
		return departureDifference;
	}

	private int endBeforeStart(Connection first, Connection second) {
		if (first.end().equals(second.start())) {
			return firstIsLess;
		}
		if (first.start().equals(second.end())) {
			return secondIsLess;
		}
		return orderIsNotRelevant;
	}
}