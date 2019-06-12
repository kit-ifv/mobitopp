package edu.kit.ifv.mobitopp.visum.reader;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.Row;
import edu.kit.ifv.mobitopp.visum.VisumPtLine;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystem;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystems;

public class VisumPtLineReader extends VisumBaseReader {

  private final VisumTransportSystems systems;

  public VisumPtLineReader(NetfileLanguage language, VisumTransportSystems systems) {
    super(language);
    this.systems = systems;
  }

  public Map<String, VisumPtLine> readPtLines(Stream<Row> content) {
    return content.map(this::createPtLine).collect(toMap(l -> l.name, Function.identity()));
  }

  private VisumPtLine createPtLine(Row row) {
    String name = nameOf(row);
    VisumTransportSystem transportSystem = transportSystemOf(row);
    return new VisumPtLine(name, transportSystem);
  }

  private VisumTransportSystem transportSystemOf(Row row) {
    String systemCode = row.get(transportSystemCode());
    return systems.getBy(systemCode);
  }

}
