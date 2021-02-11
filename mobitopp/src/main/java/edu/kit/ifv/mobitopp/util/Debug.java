package edu.kit.ifv.mobitopp.util;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;

public enum Debug {

	log;

	private final BufferedWriter output;

	private Debug() {
		output = createOutput();
	}

	private BufferedWriter createOutput() {
		try {
			return Files.newBufferedWriter(file().toPath(), CREATE, WRITE);
		} catch (IOException cause) {
			throw new UncheckedIOException(cause);
		}
	}

	private File file() {
		String name = "debug";
		String extension = ".log";
		File file = new File(name + extension);
		for (int number = 0; file.exists(); number++) {
			file = new File(name + number + extension);
		}
		return file;
	}

	public void line(String message) {
		try {
			output.write(message);
			output.newLine();
			output.flush();
		} catch (IOException cause) {
			throw new UncheckedIOException(cause);
		}
	}
}
