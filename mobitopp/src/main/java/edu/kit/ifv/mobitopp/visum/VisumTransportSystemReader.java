package edu.kit.ifv.mobitopp.visum;

import static java.util.stream.Collectors.toMap;

import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.routes.Row;

public class VisumTransportSystemReader {

  private final NetfileLanguage language;

  public VisumTransportSystemReader(NetfileLanguage language) {
    super();
    this.language = language;
  }

  public VisumTransportSystems readTransportSystems(Stream<Row> content) {
    return new VisumTransportSystems(
        content.collect(toMap(r -> codeIn(r), r -> systemIn(r))));
  }

  private VisumTransportSystem systemIn(Row row) {
    return new VisumTransportSystem(codeIn(row), nameIn(row), typeIn(row));
  }

  private String codeIn(Row row) {
    return row.get(language.resolve(StandardAttributes.code));
  }

  private String nameIn(Row row) {
    return row.get(language.resolve(StandardAttributes.name));
  }

  private String typeIn(Row row) {
    return row.get(language.resolve(StandardAttributes.type));
  }

}
