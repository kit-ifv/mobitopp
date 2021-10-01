package edu.kit.ifv.mobitopp.publictransport.serializer;

import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;

public interface JourneyProvider {

	ModifiableJourney get(int someJourneyId);

	Stream<ModifiableJourney> stream();

}
