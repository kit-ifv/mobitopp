package edu.kit.ifv.mobitopp.visum.routes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.ContentDescription;

public abstract class BaseVisumReader {

  protected static final long defaultStart = Long.MIN_VALUE;

  private static final String defaultAttributeSeparator = ";";
  protected final String attributeSeparator;

  public BaseVisumReader(String attributeSeparator) {
    super();
    this.attributeSeparator = attributeSeparator;
  }

  public BaseVisumReader() {
    this(defaultAttributeSeparator);
  }

  public Stream<Row> read(File routesFile, String tableName) {
    try {
      return doRead(routesFile, tableName);
    } catch (IOException cause) {
      throw new UncheckedIOException(cause);
    }
  }

  private Stream<Row> doRead(File file, String tableName) throws IOException {
    ContentDescription contentDescription = getContentDescription(file, tableName);
    return parseContent(file, contentDescription);
  }

  protected abstract ContentDescription getContentDescription(File file, String tableName) throws IOException;

  protected abstract BufferedReader createReader(File routesFile, Charset charset) throws IOException;
  

  protected boolean isStartOfTable(String tableName, String line) {
    return line.startsWith("$") && tableName.equals(tableName(line));
  }

  protected boolean hasNoContent(String line) {
    return line.isEmpty() || line.charAt(0) != '$' || !line.contains(":");
  }

  protected boolean isContentFinished(long startOfContent, String line) {
    return defaultStart != startOfContent && (line.startsWith("$") || line.isEmpty());
  }

  protected Charset charset() {
    return Charset.forName("ISO-8859-1");
  }

  protected String tableName(String line) {
    String[] fields = line.split(":");
    return fields[0].substring(1);
  }

  protected List<String> tableAttributes(String line) {
    String[] fields = line.split(":");
    String[] attributes = fields[1].split(attributeSeparator);
    return Arrays.asList(attributes);
  }

  private Stream<Row> parseContent(File routesFile, ContentDescription contentDescription)
      throws IOException {
    return contentDescription.convert(linesOf(routesFile));
  }

  private Stream<String> linesOf(File routesFile) throws IOException {
    return createReader(routesFile, charset()).lines();
  }

}