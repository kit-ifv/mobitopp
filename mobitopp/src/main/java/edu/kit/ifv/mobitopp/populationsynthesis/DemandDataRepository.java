package edu.kit.ifv.mobitopp.populationsynthesis;

import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.DemandDataSerialiser;

public interface DemandDataRepository {

	void store(DataForZone demandData);

	void serialiseTo(DemandDataSerialiser serialiser);

}
