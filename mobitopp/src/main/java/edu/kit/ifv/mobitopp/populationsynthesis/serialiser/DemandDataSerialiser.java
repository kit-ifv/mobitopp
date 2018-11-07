package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import java.io.Closeable;
import java.util.Collection;

import edu.kit.ifv.mobitopp.populationsynthesis.OpportunityLocations;
import edu.kit.ifv.mobitopp.simulation.Household;

public interface DemandDataSerialiser extends Closeable {

	void serialise(Household household);

	void serialise(Collection<Household> households);

	void serialise(OpportunityLocations opportunities);

}
