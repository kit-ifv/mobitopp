package edu.kit.ifv.mobitopp.result;

import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;

public interface TripConverter {

	String convert(final Person person, final FinishedTrip finishedTrip);

}
