package edu.kit.ifv.mobitopp.publictransport.serializer;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.time.Time;

interface ConnectionFormat {

	String serialize(Connection connection, Time day);

	Connection deserialize(
			String serialized, StopResolver stopResolver, JourneyProvider journeyProvider, Time day);

}
