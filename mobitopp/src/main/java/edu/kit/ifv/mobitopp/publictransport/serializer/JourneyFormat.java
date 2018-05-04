package edu.kit.ifv.mobitopp.publictransport.serializer;

import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;

interface JourneyFormat {

	String serialize(Journey journey);

	ModifiableJourney deserialize(String serialized);

}
