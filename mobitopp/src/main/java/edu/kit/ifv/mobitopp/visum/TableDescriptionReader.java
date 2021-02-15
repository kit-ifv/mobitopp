package edu.kit.ifv.mobitopp.visum;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;
import static java.util.Collections.emptyList;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TableDescriptionReader {

  private static final String tableNameSeparator = ":";
  public static final String defaultAttributeSeparator = ";";
  public static final TableDescription emptyDescription = new TableDescription(0, 0, emptyList(),
      defaultAttributeSeparator);

  private static final long defaultStart = Long.MIN_VALUE;

  private final Map<String, TableDescription> descriptions;
  private final String attributeSeparator;
  private int currentLine;
  private long startOfTable;
  private String currentTable;
  private List<String> attributes;

  public TableDescriptionReader(String attributeSeparator) {
    super();
    this.attributeSeparator = attributeSeparator;
    this.descriptions = new HashMap<>();
  }

  public TableDescription readTable(String tableName, BufferedReader reader) throws IOException {
    return readTables(reader).getOrDefault(tableName, warn(tableName, "table description", emptyDescription, log));
  }

  public Map<String, TableDescription> readTables(BufferedReader reader) throws IOException {
    currentLine = 0;
    reset();
    try {
      while (reader.ready()) {
        String line = reader.readLine();
        processLine(line);
      }
    } finally {
      reader.close();
    }
    if (defaultStart != startOfTable) {
      addDescription();
    }
    return descriptions;
  }

  private void reset() {
    startOfTable = defaultStart;
    currentTable = "";
    attributes = new LinkedList<>();
  }

  private void processLine(String line) {
    nextLine();
    if (isTableFinished(startOfTable, line)) {
      finishTable();
    } else if (hasNoContent(line)) {
    } else if (isStartOfTable(line)) {
      startNewTable(line);
    }
  }

  private void nextLine() {
    currentLine++;
  }

  private boolean isTableFinished(long startOfTable, String line) {
    return defaultStart != startOfTable && (line.startsWith("$") || line.isEmpty());
  }

  private void finishTable() {
    addDescription();
    reset();
  }

  private void addDescription() {
    int endOfTable = currentLine - 1;
    TableDescription description = new TableDescription(startOfTable, endOfTable, attributes,
        attributeSeparator);
    descriptions.put(currentTable, description);
  }

  private boolean hasNoContent(String line) {
    return line.isEmpty() || line.charAt(0) != '$' || !line.contains(tableNameSeparator);
  }

  private boolean isStartOfTable(String line) {
    return line.startsWith("$");
  }

  private void startNewTable(String line) {
    attributes = new LinkedList<>();
    attributes.addAll(tableAttributes(line));
    startOfTable = currentLine;
    currentTable = tableName(line);
  }

  private List<String> tableAttributes(String line) {
    String[] fields = line.split(tableNameSeparator);
    String[] attributes = fields[1].split(attributeSeparator);
    return Arrays.asList(attributes);
  }

  private String tableName(String line) {
    String[] fields = line.split(tableNameSeparator);
    return fields[0].substring(1);
  }
}
