package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.Comparator;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;

class ConnectionComparator implements Comparator<Connection> {
	private static final int orderIsNotRelevant = 0;
	private static final int firstIsLess = -1;
	private static final int secondIsLess = 1;

	@Override
	public int compare(Connection first, Connection second) {
		int firstToSecond = doCompare(first, second);
		int secondToFirst = doCompare(second, first);
		if (firstToSecond != -secondToFirst) {
			throw new IllegalArgumentException("Bad comparison");
		}
		return firstToSecond;
	}

	private int doCompare(Connection first, Connection second) {
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
		boolean firstEndEqualsSecondStart = first.end().equals(second.start());
		boolean firstStartEqualsSecondEnd = first.start().equals(second.end());
		if (firstEndEqualsSecondStart && !firstStartEqualsSecondEnd) {
			return firstIsLess;
		}
		if (firstStartEqualsSecondEnd && !firstEndEqualsSecondStart) {
			return secondIsLess;
		}
		return first.id().compareTo(second.id());
	}
}