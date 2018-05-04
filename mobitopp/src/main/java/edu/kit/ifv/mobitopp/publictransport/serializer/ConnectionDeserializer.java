package edu.kit.ifv.mobitopp.publictransport.serializer;

import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;

interface ConnectionDeserializer {

	List<String> connections();

	Connection deserializeConnection(
			String serialized, StopResolver stopPoints, JourneyProvider journeys);

}