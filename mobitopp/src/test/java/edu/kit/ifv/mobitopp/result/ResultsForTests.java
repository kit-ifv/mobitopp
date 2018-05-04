package edu.kit.ifv.mobitopp.result;

import java.io.IOException;

import org.junit.rules.TemporaryFolder;

public class ResultsForTests {

	public static ResultWriter at(TemporaryFolder baseFolder) throws IOException {
		return ResultWriter.create(baseFolder.newFolder());
	}
}
