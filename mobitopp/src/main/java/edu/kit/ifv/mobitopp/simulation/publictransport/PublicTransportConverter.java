package edu.kit.ifv.mobitopp.simulation.publictransport;

import java.util.List;

import edu.kit.ifv.mobitopp.time.Time;

public interface PublicTransportConverter {

	PublicTransportTimetable convert();

	List<Time> simulationDates();

}