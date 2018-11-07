package edu.kit.ifv.mobitopp.populationsynthesis;

import java.io.IOException;

public interface DemandDataRepository {

	void store(DataForZone demandData);

	void finishExecution() throws IOException;

}
