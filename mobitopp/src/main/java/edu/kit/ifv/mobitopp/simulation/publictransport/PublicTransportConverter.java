package edu.kit.ifv.mobitopp.simulation.publictransport;

import java.io.IOException;
import java.util.List;

import edu.kit.ifv.mobitopp.time.Time;

public interface PublicTransportConverter {

	PublicTransportTimetable convert() throws IOException;

	List<Time> simulationDates();

}