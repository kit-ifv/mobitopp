package edu.kit.ifv.mobitopp.visum.routes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.TableDescription;
import edu.kit.ifv.mobitopp.visum.TableDescriptionReader;

public abstract class VisumFileReader {

  private static final Charset defaultCharset = Charset.forName("ISO-8859-1");
  protected final String attributeSeparator;
  private final Charset charset;

  public VisumFileReader(String attributeSeparator, Charset charset) {
    super();
    this.attributeSeparator = attributeSeparator;
    this.charset = charset;
  }

  public VisumFileReader() {
    this(TableDescriptionReader.defaultAttributeSeparator, defaultCharset);
  }

  public Stream<Row> read(File routesFile, String tableName) {
    try {
      return doRead(routesFile, tableName);
    } catch (IOException cause) {
      throw new UncheckedIOException(cause);
    }
  }

  private Stream<Row> doRead(File file, String tableName) throws IOException {
    TableDescription description = getTableDescription(file, charset, tableName);
    return parseTable(file, description);
  }

  protected abstract TableDescription getTableDescription(
      File file, Charset charset, String tableName) throws IOException;

  private Stream<Row> parseTable(File routesFile, TableDescription contentDescription)
      throws IOException {
    return contentDescription.convert(linesOf(routesFile));
  }

  private Stream<String> linesOf(File routesFile) throws IOException {
    return createReader(routesFile, charset).lines();
  }

  protected abstract BufferedReader createReader(File routesFile, Charset charset)
      throws IOException;

}