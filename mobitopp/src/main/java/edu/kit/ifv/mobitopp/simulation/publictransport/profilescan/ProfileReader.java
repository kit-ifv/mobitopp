package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import java.io.Closeable;
import java.io.IOException;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;

public interface ProfileReader extends Closeable {

	Stop readStop() throws IOException;

	ArrivalTimeFunction readFunction() throws IOException;

	boolean next() throws IOException;

	static ProfileReader emptyReader() {
		return EmptyReader.instance();
	}

}
