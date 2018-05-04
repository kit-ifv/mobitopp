package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import java.io.Closeable;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;

public interface ProfileWriter extends Closeable {

	void write(Stop source, ArrivalTimeFunction function);

	void write(FunctionEntry entry);

}
