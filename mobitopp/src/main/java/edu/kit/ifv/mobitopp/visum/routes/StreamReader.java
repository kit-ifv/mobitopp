package edu.kit.ifv.mobitopp.visum.routes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import edu.kit.ifv.mobitopp.visum.TableDescription;
import edu.kit.ifv.mobitopp.visum.TableDescriptionReader;

public class StreamReader extends VisumFileReader {

  public StreamReader(String attributeSeparator, Charset charset) {
    super(attributeSeparator, charset);
  }

  public StreamReader() {
    super();
  }

  @Override
  protected BufferedReader createReader(File routesFile, Charset charset) throws IOException {
    return Files.newBufferedReader(routesFile.toPath(), charset);
  }

  @Override
  protected TableDescription getTableDescription(File file, Charset charset, String tableName)
      throws IOException {
    return new TableDescriptionReader(attributeSeparator)
        .readTable(tableName, createReader(file, charset));
  }
}
