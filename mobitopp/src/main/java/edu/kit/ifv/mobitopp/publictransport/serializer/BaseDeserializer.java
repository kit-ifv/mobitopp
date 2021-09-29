package edu.kit.ifv.mobitopp.publictransport.serializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.util.file.StreamContent;

abstract class BaseDeserializer {

	private static final int header = 1;

	protected BaseDeserializer() {
		super();
	}

	protected Stream<String> removeHeaderFrom(File file) throws IOException {
		return new BufferedReader(
			new InputStreamReader(StreamContent.of(file), TimetableFiles.encoding))
				.lines()
				.skip(header);
	}

}