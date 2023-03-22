package edu.kit.ifv.mobitopp.visum;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import edu.kit.ifv.mobitopp.util.file.StreamContent;
import edu.kit.ifv.mobitopp.visum.reader.VisumFileReader;

public class StreamReader extends VisumFileReader {

  public StreamReader(String attributeSeparator, Charset charset) {
    super(attributeSeparator, charset);
  }

  public StreamReader() {
    super();
  }

  @Override
  protected BufferedReader createReader(File routesFile, Charset charset) throws IOException {
	return new BufferedReader(new InputStreamReader(StreamContent.of(routesFile), charset));
  }

  @Override
  protected TableDescription getTableDescription(File file, Charset charset, String tableName)
      throws IOException {
    return new TableDescriptionReader(attributeSeparator)
        .readTable(tableName, createReader(file, charset));
  }
}
