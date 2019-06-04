package edu.kit.ifv.mobitopp.visum;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.util.file.StreamContent;
import edu.kit.ifv.mobitopp.visum.routes.VisumFileReader;

public final class CachedVisumReader extends VisumFileReader {

  private final Map<String, TableDescription> descriptions;
  private File file;
  private byte[] storedInput;

  public CachedVisumReader(String attributeSeparator, Charset charset) {
    super(attributeSeparator, charset);
    this.descriptions = new HashMap<>();
  }

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
  protected TableDescription getTableDescription(File file, Charset charset, String tableName)
      throws IOException {
    buildUpDescriptionCache(file, charset);
    return descriptions.getOrDefault(tableName, TableDescriptionReader.emptyDescription);
  }

  private void buildUpDescriptionCache(File file, Charset charset) throws IOException {
    if (null == descriptions || descriptions.isEmpty()) {
      cacheDescriptionsFrom(file, charset);
    }
  }

  private void cacheDescriptionsFrom(File file, Charset charset) throws IOException {
    descriptions
        .putAll(new TableDescriptionReader(attributeSeparator)
            .readTables(createReader(file, charset)));
  }
}