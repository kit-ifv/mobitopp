package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

public interface Deserialiser<T> extends Closeable {

	List<T> deserialise() throws IOException;

}