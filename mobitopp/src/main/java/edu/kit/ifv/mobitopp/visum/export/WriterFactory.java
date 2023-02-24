package edu.kit.ifv.mobitopp.visum.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * A factory for creating and closing buffered file writers.
 */
public class WriterFactory {

	/**
	 * Creates a buffered file writer for the given file path.
	 *
	 * @param path the file path to be written
	 * @return the file writer for the given path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static Writer getWriter(File path) throws IOException {
		FileWriter fileWriter = new FileWriter(path);
		return new BufferedWriter(fileWriter);
	}

	/**
	 * Finish the given writer.
	 * 
	 * This appends a line-break, flushes and closes the writer.
	 *
	 * @param writer the writer to be finished
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void finishWriter(Writer writer) throws IOException {
		writer.write("\r\n");
		writer.flush();
		writer.close();
	}

}
