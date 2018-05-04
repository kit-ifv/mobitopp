package edu.kit.ifv.mobitopp.publictransport.serializer;

import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;

public interface JourneyProvider {

	ModifiableJourney get(int someJourneyId);

}
