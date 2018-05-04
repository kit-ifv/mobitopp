package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

public interface ForeignKeyDeserialiser<T> extends Closeable {

	List<T> deserialise(PopulationContext context) throws IOException;

}