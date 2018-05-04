package edu.kit.ifv.mobitopp.result;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResultFile implements ResultOutput, Closeable {

	static final String extension = ".log";

	private final BufferedWriter writer;

	ResultFile(BufferedWriter writer) {
		super();
		this.writer = writer;
	}

	@Override
	public void writeLine(String text) throws UncheckedIOException {
		try {
			writer.write(text);
			writer.newLine();
			writer.flush();
		} catch (IOException cause) {
			throw new UncheckedIOException(cause);
		}
	}

	public static ResultFile create(File atBaseFolder, Category category) throws UncheckedIOException {
		BufferedWriter output = writerFor(category, atBaseFolder);
		ResultFile resultFile = new ResultFile(output);
		resultFile.writeLine(category.header());
		return resultFile;
	}

	private static BufferedWriter writerFor(Category category, File baseFolder)
			throws UncheckedIOException {
		Path path = new File(baseFolder, fileName(category)).toPath();
		try {
			return Files.newBufferedWriter(path, TRUNCATE_EXISTING, WRITE, CREATE);
		} catch (IOException cause) {
			throw new UncheckedIOException(cause);
		}
	}

	private static String fileName(Category category) {
		return category.name() + extension;
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}

}
