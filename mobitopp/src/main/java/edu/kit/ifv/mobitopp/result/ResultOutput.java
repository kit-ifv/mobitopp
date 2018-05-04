package edu.kit.ifv.mobitopp.result;

import java.io.IOException;
import java.io.UncheckedIOException;

public interface ResultOutput {

	void writeLine(String text) throws UncheckedIOException;

	void close() throws IOException;

}
