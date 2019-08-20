package edu.kit.ifv.mobitopp.visum.reader;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.util.dataimport.Row;
import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.StandardAttributes;
import edu.kit.ifv.mobitopp.visum.StopArea;
import edu.kit.ifv.mobitopp.visum.StopAreaPair;
import edu.kit.ifv.mobitopp.visum.VisumPtStopArea;
import edu.kit.ifv.mobitopp.visum.VisumPtTransferWalkTimes;

public class VisumPtTransferWalkTimesReader extends VisumBaseReader {

  private final Map<Integer, VisumPtStopArea> ptStopAreas;

  public VisumPtTransferWalkTimesReader(
      NetfileLanguage language, Map<Integer, VisumPtStopArea> ptStopAreas) {
    super(language);
    this.ptStopAreas = ptStopAreas;
  }

  public Map<StopAreaPair, VisumPtTransferWalkTimes> readTransferWalkTimes(Stream<Row> content) {
    return content
        .map(this::createTransferWalkTime)
        .collect(toMap(this::createKey, Function.identity()));
  }

  private StopAreaPair createKey(VisumPtTransferWalkTimes walkTime) {
    return new StopAreaPair(new StopArea(walkTime.fromArea.id), new StopArea(walkTime.toArea.id));
  }

  private VisumPtTransferWalkTimes createTransferWalkTime(Row row) {
    String vsysCode = row.get(transportSystemCode());
    int time = parseTime(row.get(attribute(StandardAttributes.time)));
    VisumPtStopArea fromArea = fromAreaOf(row);
    VisumPtStopArea toArea = toAreaOf(row);
    return new VisumPtTransferWalkTimes(fromArea, toArea, vsysCode, time);
  }

  private VisumPtStopArea toAreaOf(Row row) {
    int toAreaId = row.valueAsInteger(attribute(StandardAttributes.toStopArea));
    return ptStopAreas.get(toAreaId);
  }

  private VisumPtStopArea fromAreaOf(Row row) {
    int fromAreaId = row.valueAsInteger(attribute(StandardAttributes.fromStopArea));
    return ptStopAreas.get(fromAreaId);
  }

}
