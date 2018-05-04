package edu.kit.ifv.mobitopp.result;

import static edu.kit.ifv.mobitopp.util.matcher.FileMatchers.contains;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ResultFileTest {

	@Rule
	public final TemporaryFolder tempFolder = new TemporaryFolder();

	private File baseFolder;

	@Before
	public void initialise() throws IOException {
		baseFolder = tempFolder.newFolder();
	}

	@Test
	public void writeToFile() throws IOException {
		String someLine = "some text";
		String anotherLine = "another text";
		try (ResultFile result = ResultFile.create(baseFolder, category())) {
			result.writeLine(someLine);
			result.writeLine(anotherLine);
		}

		assertThat(logFile(), contains(category().header(), someLine, anotherLine));
	}

	@Test
	public void writesHeaderToFile() throws IOException {
		ResultFile.create(baseFolder, category()).close();

		assertThat(logFile(), contains(category().header()));
	}

	private Category category() {
		return new Category("category", header());
	}

	private List<String> header() {
		return asList("column 1", "column 2");
	}

	private File logFile() {
		return new File(baseFolder, category().name() + ResultFile.extension);
	}

	@Test
	public void closesWriter() throws IOException {
		BufferedWriter writer = mock(BufferedWriter.class);

		new ResultFile(writer).close();

		verify(writer).close();
	}
}
