package edu.kit.ifv.mobitopp.simulation.person;

import edu.kit.ifv.mobitopp.time.Time;

public interface FinishedTrip extends StartedTrip<FinishedTrip> {

	Time endDate();

	Statistic statistic();

}