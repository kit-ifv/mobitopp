package edu.kit.ifv.mobitopp.visum;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.kit.ifv.mobitopp.visum.routes.Row;

public class VisumTurnsReader extends VisumBaseReader {

  private final Map<Integer, VisumNode> nodes;
  private final VisumTransportSystems allSystems;

  public VisumTurnsReader(NetfileLanguage language, Map<Integer, VisumNode> nodes, VisumTransportSystems allSystems) {
    super(language);
    this.nodes = nodes;
    this.allSystems = allSystems;
  }

  public Map<Integer, List<VisumTurn>> readTurns(VisumTable table) {
    return table
        .rows()
        .map(this::createTurn)
        .collect(groupingBy(t -> t.node))
        .entrySet()
        .stream()
        .collect(toMap(e -> e.getKey().id(), Entry::getValue));
  }
  
  private VisumTurn createTurn(Row row) {
    int nodeId = row.valueAsInteger(attribute(StandardAttributes.viaNodeNumber));
    String transportSystems = row.get(transportSystemsSet());
    VisumTransportSystemSet systemSet = VisumTransportSystemSet.getByCode(transportSystems, allSystems);
    VisumNode node = nodes.get(nodeId);
    VisumNode from = nodes.get(row.valueAsInteger(fromNode()));
    VisumNode to = nodes.get(row.valueAsInteger(toNode()));
    int type = row.valueAsInteger(typeNumber());
    int capacity = row.valueAsInteger(capacityCar());
    Integer timePenaltyInSec = parseTime(row.get(attribute(StandardAttributes.freeFlowTravelTimeCar)));
    return new VisumTurn(node, from, to, type, systemSet, capacity, timePenaltyInSec);
  }

}
