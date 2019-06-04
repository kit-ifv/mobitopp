package edu.kit.ifv.mobitopp.visum;

import static java.util.Collections.emptyList;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.util.file.StreamContent;
import edu.kit.ifv.mobitopp.visum.routes.BaseVisumReader;

public final class CachedVisumReader extends BaseVisumReader {

  private File file;
  private byte[] storedInput;
  private final Map<String, ContentDescription> descriptions;

  public CachedVisumReader() {
    super();
    this.descriptions = new HashMap<>();
  }

  protected BufferedReader createReader(File file, Charset charset) throws IOException {
    if (null == this.file || !this.file.equals(file)) {
      storedInput = StreamContent.readBytes(file);
      this.file = file;
      this.descriptions.clear();
    }
    return new BufferedReader(
        new InputStreamReader(new ByteArrayInputStream(storedInput), charset));
  }

  @Override
  protected ContentDescription getContentDescription(File file, String tableName) throws IOException {
    if (!descriptions.isEmpty()) {
      ContentDescription emptyDescription = new ContentDescription(0, 0, emptyList(), attributeSeparator);
      return descriptions.getOrDefault(tableName, emptyDescription);
    }
    cacheDescriptionsFrom(file);
    return descriptions.get(tableName);
  }

  private void cacheDescriptionsFrom(File file) throws IOException {
    int currentLine = 0;
    long startOfContent = defaultStart;
    long endOfContent = currentLine;
    String currentTable = "";
    List<String> attributes = new LinkedList<>();
    try (BufferedReader reader = createReader(file, charset())) {
      while (reader.ready()) {
        String line = reader.readLine();
        if (isContentFinished(startOfContent, line)) {
          endOfContent = currentLine;
          ContentDescription contentDescription = new ContentDescription(startOfContent, endOfContent, attributes, attributeSeparator);
          descriptions.put(currentTable, contentDescription);
          startOfContent = defaultStart;
          endOfContent = 0;
          currentLine++;
          continue;
        }
        currentLine++;
        if (hasNoContent(line)) {
          continue;
        }
  
        if (isStartOfTable(line)) {
          attributes = new LinkedList<>();
          attributes.addAll(tableAttributes(line));
          startOfContent = currentLine;
          currentTable = tableName(line);
        }
      }
      if (startOfContent == endOfContent || 0 == endOfContent) {
        endOfContent = currentLine;
      }
    }
    if (defaultStart == startOfContent) {
      ContentDescription contentDescription = new ContentDescription(0, 0, attributes, attributeSeparator);
      descriptions.put(currentTable, contentDescription);
      return;
    }
    ContentDescription contentDescription = new ContentDescription(startOfContent, endOfContent, attributes, attributeSeparator);
    descriptions.put(currentTable, contentDescription);
  }
  
  private boolean isStartOfTable(String line) {
    return line.startsWith("$");
  }
}