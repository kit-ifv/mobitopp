package edu.kit.ifv.mobitopp.util.dataimport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class CsvFileTest {

	private static final String someHeader = "hEaDer1";
	private static final String otherHeader = "HeaDeR2";
	private static final String someContent = "some content";
	private static final String otherContent = "other content";

	private Reader reader;

	@Before
	public void initialise() {
		String headerLine = someHeader + ";" + otherHeader;
		String contentLine = someContent + ";" + otherContent;
		String content = headerLine + System.lineSeparator() + contentLine;
		reader = new InputStreamReader(new ByteArrayInputStream(content.getBytes()));
	}

	@Test
	public void ignoreCaseInHeader() {
		CsvFile csvFile = createFile();

		List<String> attributes = csvFile.getAttributes();

		assertThat(attributes, contains(someHeader.toLowerCase(), otherHeader.toLowerCase()));
	}
	
	@Test
	public void accessesValuesIgnoringCase() {
		CsvFile csvFile = createFile();
		
		String value = csvFile.getValue(0, someHeader);
		
		assertThat(value, is(equalTo(someContent)));
	}

	private CsvFile createFile() {
		return new CsvFile("dummy-file-name") {

			@Override
			protected Reader createReader(String filename) throws FileNotFoundException {
				return reader;
			}
		};
	}
}
