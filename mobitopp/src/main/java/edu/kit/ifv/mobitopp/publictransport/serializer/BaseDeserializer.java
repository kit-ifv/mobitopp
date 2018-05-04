package edu.kit.ifv.mobitopp.publictransport.serializer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Stream;

abstract class BaseDeserializer {

	private static final int header = 1;

	protected BaseDeserializer() {
		super();
	}

	protected Stream<String> removeHeaderFrom(File file) throws IOException {
		return Files.lines(file.toPath(), StandardCharsets.UTF_8).skip(header);
	}

}