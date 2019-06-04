package edu.kit.ifv.mobitopp.visum.routes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

import edu.kit.ifv.mobitopp.visum.ContentDescription;

public class StreamReader extends BaseVisumReader {

  public StreamReader(String attributeSeparator) {
    super(attributeSeparator);
  }

  public StreamReader() {
    super();
  }

  @Override
  protected BufferedReader createReader(File routesFile, Charset charset) throws IOException {
    return Files.newBufferedReader(routesFile.toPath(), charset);
  }

  @Override
  protected ContentDescription getContentDescription(File file, String tableName) throws IOException {
    int currentLine = 0;
    long startOfContent = defaultStart;
    long endOfContent = currentLine;
    List<String> attributes = new LinkedList<>();
    try (BufferedReader reader = createReader(file, charset())) {
      while (reader.ready()) {
        String line = reader.readLine();
        if (isContentFinished(startOfContent, line)) {
          endOfContent = currentLine;
          break;
        }
        currentLine++;
        if (hasNoContent(line)) {
          continue;
        }
  
        if (isStartOfTable(tableName, line)) {
          attributes.addAll(tableAttributes(line));
          startOfContent = currentLine;
        }
      }
      if (startOfContent == endOfContent || 0 == endOfContent) {
        endOfContent = currentLine;
      }
    }
    if (defaultStart == startOfContent) {
      return new ContentDescription(0, 0, attributes, tableName);
    }
    return new ContentDescription(startOfContent, endOfContent, attributes, attributeSeparator);
  }
}
