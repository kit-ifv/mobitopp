package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.RelativeTime;

public interface TransferWalkTime {

	Optional<RelativeTime> walkTime(Stop from, Stop to);

	RelativeTime minimumChangeTime(int stopId);

}
