package edu.kit.ifv.mobitopp.util.dataimport;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CsvFileTest {

  private static final String someHeader = "hEaDer1";
  private static final String otherHeader = "HeaDeR2";
  private static final String someContent = "some content";
  private static final String otherContent = "other content";
  private static final String escapedName = "\"other content\"";

  private StringBuilder content;

  @BeforeEach
  public void initialise() {
    String headerLine = someHeader + ";" + otherHeader;
    String contentLine = someContent + ";" + otherContent;
    content = new StringBuilder();
    content.append(headerLine);
    content.append(System.lineSeparator());
    content.append(contentLine);
    content.append(System.lineSeparator());
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

  @Test
  public void missingAttribute() {
    CsvFile csvFile = createFile();

    assertFalse(csvFile.hasAttribute("missing-attribute"));
  }

  @Test
  public void hasAttribute() {
    CsvFile csvFile = createFile();

    assertTrue(csvFile.hasAttribute(someHeader));
  }

  @Test
  public void ignoresEmptyLines() {
    content.append(System.lineSeparator());
    CsvFile csvFile = createFile();

    assertThat(csvFile.getLength(), is(1));
  }

  @Test
  void ignoreEscapeChars() throws Exception {
    StringBuilder content = new StringBuilder();
    String headerLine = escapedName;
    content.append(headerLine);
    content.append(System.lineSeparator());
    
    createFileFor(content);
  }
  
  @Test
	void streamContent() throws Exception {
		CsvFile csvFile = createFile();
		
		List<Row> content = csvFile.stream().collect(toList());
		
		Map<String, String> values = new HashMap<>();
		values.put(someHeader.toLowerCase(), someContent);
		values.put(otherHeader.toLowerCase(), otherContent);
		Row row = new Row(values);
		assertThat(content, contains(row));
	}

	private CsvFile createFileFor(StringBuilder content) {
    return new CsvFile("dummy-file-name") {

      @Override
      protected Reader createReader(File file) throws FileNotFoundException {
        return new InputStreamReader(new ByteArrayInputStream(content.toString().getBytes()));
      }
    };
  }

  private CsvFile createFile() {
    return new CsvFile("dummy-file-name") {

      @Override
      protected Reader createReader(File file) throws FileNotFoundException {
        return new InputStreamReader(new ByteArrayInputStream(content.toString().getBytes()));
      }
    };
  }
}
