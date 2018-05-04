package edu.kit.ifv.mobitopp.publictransport.serializer;

import java.io.Closeable;
import java.io.IOException;

import edu.kit.ifv.mobitopp.publictransport.model.ConnectionConsumer;

public interface Serializer extends StopSerializer, ConnectionConsumer, JourneySerializer,
		StationSerializer, Closeable {

	void writeHeaders() throws IOException;

}
