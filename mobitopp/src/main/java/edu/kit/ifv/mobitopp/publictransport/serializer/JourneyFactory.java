package edu.kit.ifv.mobitopp.publictransport.serializer;

import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;
import edu.kit.ifv.mobitopp.publictransport.model.TransportSystem;
import edu.kit.ifv.mobitopp.time.Time;

public interface JourneyFactory {

	ModifiableJourney createJourney(int id, Time day, int capacity, TransportSystem system);

}
