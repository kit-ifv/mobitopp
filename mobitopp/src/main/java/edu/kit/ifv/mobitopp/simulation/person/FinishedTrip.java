package edu.kit.ifv.mobitopp.simulation.person;

import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.time.Time;

public interface FinishedTrip extends StartedTrip {

	Time endDate();

	Statistic statistic();

	void forEachFinishedLeg(Consumer<FinishedTrip> consumer);
}