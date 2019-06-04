package edu.kit.ifv.mobitopp.visum;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import edu.kit.ifv.mobitopp.util.file.StreamContent;
import edu.kit.ifv.mobitopp.visum.routes.StreamReader;

public final class CachedVisumReader extends StreamReader {

  private File file;
  private byte[] storedInput;

  public CachedVisumReader() {
    super();
  }

  protected BufferedReader createReader(File file, Charset charset) throws IOException {
    if (null == this.file || !this.file.equals(file)) {
      storedInput = StreamContent.readBytes(file);
      this.file = file;
    }
    return new BufferedReader(
        new InputStreamReader(new ByteArrayInputStream(storedInput), charset));
  }
}