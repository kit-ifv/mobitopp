package edu.kit.ifv.mobitopp.util.dataimport;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class CsvFileTest {

	private static final String someHeader = "hEaDer1";
  private static final String otherHeader = "HeaDeR2";
  private static final int someValue = 1;
  private static final String someContent = String.valueOf(someValue);
  private static final String otherContent = "other content";
  private static final String escapedName = "\"other content\"";

  private StringBuilder content;
  
  @TempDir
  Path tempDir = Paths.get("tempDir");

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
  public void ignoreCaseInHeader() throws IOException {
    CsvFile csvFile = createFromFile();

    List<String> attributes = csvFile.getAttributes();

    assertThat(attributes, contains(someHeader.toLowerCase(), otherHeader.toLowerCase()));
  }

  @Test
  public void accessesValuesIgnoringCase() throws IOException {
    CsvFile csvFile = createFromFile();

    String value = csvFile.getValue(0, someHeader);

    assertThat(value, is(equalTo(someContent)));
  }
  
  @Test
  public void getValues() throws IOException {
  	CsvFile csvFile = createFromFile();
  	
  	assertAll(() -> assertThat(csvFile.getInteger(0, someHeader)).isEqualTo(someValue),
  			() -> assertThat(csvFile.getDouble(0, someHeader)).isCloseTo(someValue, offset(1e-6d)),
  			() -> assertThat(csvFile.getFloat(0, someHeader)).isCloseTo(someValue, offset(1e-6f)));
  }

  @Test
  public void missingAttribute() throws IOException {
    CsvFile csvFile = createFromFile();

    assertFalse(csvFile.hasAttribute("missing-attribute"));
  }

  @Test
  public void hasAttribute() throws IOException {
    CsvFile csvFile = createFromFile();

    assertTrue(csvFile.hasAttribute(someHeader));
  }

  @Test
  public void ignoresEmptyLines() throws IOException {
    content.append(System.lineSeparator());
    CsvFile csvFile = createFromFile();

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
		CsvFile csvFile = createFromStream();
		
		List<Row> content = csvFile.stream().collect(toList());
		
		Map<String, String> values = new HashMap<>();
		values.put(someHeader.toLowerCase(), someContent);
		values.put(otherHeader.toLowerCase(), otherContent);
		Row row = new Row(values);
		assertThat(content, contains(row));
	}

	private CsvFile createFileFor(StringBuilder content) throws IOException {
    return CsvFile.createFrom(new ByteArrayInputStream(content.toString().getBytes()));
  }
	
	private CsvFile createFromStream() throws IOException {
		return createFileFor(content);
	}
	
  private CsvFile createFromFile() throws IOException {
  	Path tempFile = tempDir.resolve("file");
		Files.write(tempFile, content.toString().getBytes());
    return CsvFile.createFrom(tempFile.toFile());
  }
}
