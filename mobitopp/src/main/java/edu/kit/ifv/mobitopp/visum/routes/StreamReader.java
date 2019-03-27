package edu.kit.ifv.mobitopp.visum.routes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class StreamReader {

  public Stream<Row> read(File routesFile, String tableName) {
    try {
      return doRead(routesFile, tableName);
    } catch (IOException cause) {
      throw new UncheckedIOException(cause);
    }
  }

  private Stream<Row> doRead(File routesFile, String tableName) throws IOException {
    int startOfTable = 0;
    List<String> attributes = new LinkedList<>();
    try (BufferedReader reader = Files.newBufferedReader(routesFile.toPath(), charset());) {
      while (reader.ready()) {
        String line = reader.readLine();
        startOfTable++;
        if (line.isEmpty() || line.charAt(0) != '$' || !line.contains(":")) {
          continue;
        }

        if (tableName.equals(tableName(line))) {
          attributes.addAll(tableAttributes(line));
          break;
        }
      }
    }
    return parseContent(routesFile, startOfTable, attributes);
  }

  private Charset charset() {
    return Charset.forName("ISO-8859-1");
  }

  private String tableName(String line) {
    String[] fields = line.split(":");
    return fields[0].substring(1);
  }

  private List<String> tableAttributes(String line) {
    String[] fields = line.split(":");
    String[] attributes = fields[1].split(";");
    return Arrays.asList(attributes);
  }

  private Stream<Row> parseContent(File routesFile, int startOfTable, List<String> attributes)
      throws IOException {
    return Files
        .lines(routesFile.toPath(), charset())
        .skip(startOfTable)
        .map(line -> parseLine(line, attributes.size()))
        .map(line -> Row.createRow(line, attributes));
  }

  private List<String> parseLine(String line, int numFields) {
    String[] fields = line.split(";");
    List<String> values = new ArrayList<>(numFields);
    values.addAll(Arrays.asList(fields));
    for (int i = values.size(); i < numFields; i++) {
      values.add("");
    }
    return values;
  }
}
