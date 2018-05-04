package edu.kit.ifv.mobitopp.publictransport.serializer;

import edu.kit.ifv.mobitopp.publictransport.model.DefaultModifiableJourney;
import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;
import edu.kit.ifv.mobitopp.publictransport.model.TransportSystem;
import edu.kit.ifv.mobitopp.time.Time;

public class DefaultJourneyFactory implements JourneyFactory {

	@Override
	public ModifiableJourney createJourney(int id, Time day, int capacity, TransportSystem system) {
		return new DefaultModifiableJourney(id, day, system, capacity);
	}

}
