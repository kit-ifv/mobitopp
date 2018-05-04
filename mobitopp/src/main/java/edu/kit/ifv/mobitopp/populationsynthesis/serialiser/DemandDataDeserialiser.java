package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import java.io.Closeable;
import java.io.IOException;

import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.Population;

public interface DemandDataDeserialiser extends Closeable {

	Population loadPopulation() throws IOException;

	void addOpportunitiesTo(ZoneRepository zoneRepository) throws IOException;

}
