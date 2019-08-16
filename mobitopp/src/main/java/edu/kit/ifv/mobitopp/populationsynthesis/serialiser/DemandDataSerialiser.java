package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import java.io.Closeable;
import java.util.Collection;

import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.OpportunityLocations;

public interface DemandDataSerialiser extends Closeable {

	void serialise(HouseholdForSetup household);

	void serialise(Collection<HouseholdForSetup> households);

	void serialise(OpportunityLocations opportunities);

}
