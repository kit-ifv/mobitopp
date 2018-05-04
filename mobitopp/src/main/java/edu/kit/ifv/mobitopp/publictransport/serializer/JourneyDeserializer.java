package edu.kit.ifv.mobitopp.publictransport.serializer;

import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;

interface JourneyDeserializer {

	List<String> journeys();

	ModifiableJourney deserializeJourney(String serialized);

}