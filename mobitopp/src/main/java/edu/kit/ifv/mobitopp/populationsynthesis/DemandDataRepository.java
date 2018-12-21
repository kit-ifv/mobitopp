package edu.kit.ifv.mobitopp.populationsynthesis;

import java.io.IOException;

import edu.kit.ifv.mobitopp.data.DemandZone;

public interface DemandDataRepository {

	void store(DemandZone zone);

	void finishExecution() throws IOException;

}
