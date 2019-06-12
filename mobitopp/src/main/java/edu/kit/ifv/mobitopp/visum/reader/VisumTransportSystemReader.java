package edu.kit.ifv.mobitopp.visum.reader;

import static java.util.stream.Collectors.toMap;

import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.Row;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystem;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystems;

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
