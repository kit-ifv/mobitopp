package edu.kit.ifv.mobitopp.visum;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.routes.Row;

public class VisumTurnsReader extends VisumBaseReader {

  private final Map<Integer, VisumNode> nodes;
  private final VisumTransportSystems allSystems;

  public VisumTurnsReader(NetfileLanguage language, Map<Integer, VisumNode> nodes, VisumTransportSystems allSystems) {
    super(language);
    this.nodes = nodes;
    this.allSystems = allSystems;
  }

  public Map<Integer, List<VisumTurn>> readTurns(Stream<Row> content) {
    return content
        .map(this::createTurn)
        .collect(groupingBy(t -> t.node))
        .entrySet()
        .stream()
        .collect(toMap(e -> e.getKey().id(), Entry::getValue));
  }
  
  private VisumTurn createTurn(Row row) {
    int nodeId = row.valueAsInteger(attribute(StandardAttributes.viaNodeNumber));
    VisumTransportSystemSet systemSet = transportSystemsOf(row, allSystems);
    VisumNode node = nodes.get(nodeId);
    VisumNode from = nodes.get(fromNodeOf(row));
    VisumNode to = nodes.get(toNodeOf(row));
    int type = typeNumberOf(row);
    int capacity = capacityCarOf(row);
    Integer timePenaltyInSec = parseTime(row.get(attribute(StandardAttributes.freeFlowTravelTimeCar)));
    return new VisumTurn(node, from, to, type, systemSet, capacity, timePenaltyInSec);
  }

}
