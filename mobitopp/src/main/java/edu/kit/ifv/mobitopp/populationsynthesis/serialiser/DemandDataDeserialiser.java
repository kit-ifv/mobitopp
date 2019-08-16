package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.Population;

public interface DemandDataDeserialiser extends Closeable {

	static Predicate<HouseholdForSetup> acceptAll() {
		return h -> true;
	}

	Population loadPopulation() throws IOException;

	Population loadPopulation(Predicate<HouseholdForSetup> householdFilter) throws IOException;

	void addOpportunitiesTo(ZoneRepository zoneRepository) throws IOException;

}
