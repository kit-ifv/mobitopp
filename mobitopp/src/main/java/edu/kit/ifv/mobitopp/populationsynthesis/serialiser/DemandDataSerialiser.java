package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import java.io.Closeable;

import edu.kit.ifv.mobitopp.populationsynthesis.OpportunityLocations;
import edu.kit.ifv.mobitopp.populationsynthesis.Population;

public interface DemandDataSerialiser extends Closeable {

	void serialise(Population population);

	void serialise(OpportunityLocations locations);

}
