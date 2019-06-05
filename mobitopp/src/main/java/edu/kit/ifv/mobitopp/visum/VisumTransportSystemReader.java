package edu.kit.ifv.mobitopp.visum;

import static java.util.stream.Collectors.toMap;

import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.routes.Row;

public class VisumTransportSystemReader extends VisumBaseReader {

  public VisumTransportSystemReader(NetfileLanguage language) {
    super(language);
  }

  public VisumTransportSystems readTransportSystems(Stream<Row> content) {
    return new VisumTransportSystems(content.collect(toMap(r -> codeOf(r), r -> systemOf(r))));
  }

  private VisumTransportSystem systemOf(Row row) {
    return new VisumTransportSystem(codeOf(row), nameOf(row), typeOf(row));
  }

}
