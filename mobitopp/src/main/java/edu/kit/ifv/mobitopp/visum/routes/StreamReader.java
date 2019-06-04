package edu.kit.ifv.mobitopp.visum.routes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

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
}
