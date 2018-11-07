package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import java.io.Closeable;

public interface ForeignKeySerialiser<T> extends Closeable {

	void writeHeader();

	void write(T element);

}