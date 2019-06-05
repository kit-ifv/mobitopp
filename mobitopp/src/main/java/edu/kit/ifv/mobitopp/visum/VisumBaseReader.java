package edu.kit.ifv.mobitopp.visum;

import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.visum.routes.Row;

public class VisumBaseReader {

  protected final NetfileLanguage language;

  public VisumBaseReader(NetfileLanguage language) {
    super();
    this.language = language;
  }

  protected String attribute(StandardAttributes tabletransportsystems) {
    return language.resolve(tabletransportsystems);
  }

  protected String table(Table table) {
    return language.resolve(table);
  }

  protected String unit(Unit unit) {
    return language.resolve(unit);
  }

  protected String direction() {
    return attribute(StandardAttributes.direction);
  }

  protected String nodeNumber() {
    return attribute(StandardAttributes.nodeNumber);
  }

  protected String transportSystemCode() {
    return attribute(StandardAttributes.transportSystemCode);
  }

  protected String directionCode() {
    return attribute(StandardAttributes.directionCode);
  }

  protected String lineName() {
    return attribute(StandardAttributes.lineName);
  }

  protected String id() {
    return attribute(StandardAttributes.id);
  }

  private Integer parseSpeed(String value, NetfileLanguage language) {
    String unit = language.resolve(Unit.velocity);
    return Float.valueOf(value.replace(unit,"")).intValue();
  }
  
  protected Float parseDistance(String value) {
    String unit = unit(Unit.distance);
  	return Float.valueOf(value.replace(unit,""));
  }

  protected Integer parseTime(String value) {
    String unit = unit(Unit.time);
  	return Double.valueOf(value.replace(unit,"")).intValue();
  }

  protected int parseTimeAsSeconds(String timeAsString) {
    String[] tmp = timeAsString.split(":");
    int hour   = Integer.valueOf(tmp[0]);
    int minute = Integer.valueOf(tmp[1]);
    int second = Integer.valueOf(tmp[2]);
    return ((hour * RelativeTime.minutesPerHour) + minute) * RelativeTime.secondsPerMinute + second;
  }

  protected int typeNumberOf(Row row) {
    return row.valueAsInteger(attribute(StandardAttributes.typeNumber));
  }

  protected int areaIdOf(Row row) {
    return row.valueAsInteger(attribute(StandardAttributes.areaId));
  }

  protected float yCoordOf(Row row) {
    return row.valueAsFloat(attribute(StandardAttributes.yCoord));
  }

  protected float xCoordOf(Row row) {
    return row.valueAsFloat(attribute(StandardAttributes.xCoord));
  }

  protected String nameOf(Row row) {
    return row.get(attribute(StandardAttributes.name));
  }

  protected int numberOf(Row row) {
    return row.valueAsInteger(attribute(StandardAttributes.number));
  }

  protected int numberOfLanesOf(Row row) {
    return row.valueAsInteger(attribute(StandardAttributes.numberOfLanes));
  }

  protected int capacityCarOf(Row row) {
    return row.valueAsInteger(attribute(StandardAttributes.capacityCar));
  }

  protected int freeFlowSpeedOf(Row row) {
    return parseSpeed(row.get(attribute(StandardAttributes.freeFlowSpeedCar)), language);
  }

  protected float lengthOf(Row row) {
    return parseDistance(row.get(attribute(StandardAttributes.length)));
  }

  protected int fromNodeOf(Row row) {
    return row.valueAsInteger(attribute(StandardAttributes.fromNodeNumber));
  }

  protected int toNodeOf(Row row) {
    return row.valueAsInteger(attribute(StandardAttributes.toNodeNumber));
  }

  protected VisumTransportSystemSet transportSystemsOf(Row row, VisumTransportSystems allSystems) {
    String transportSystems = row.get(attribute(StandardAttributes.transportSystemSet));
    return VisumTransportSystemSet.getByCode(transportSystems, allSystems);
  }

  protected int walkSpeedOf(Row row) {
    String publicWalkSpeed = language.resolve(StandardAttributes.publicTransportWalkSpeed);
    String individualWalkSpeed = language.resolve(StandardAttributes.individualWalkSpeed);
    if (row.containsAttribute(publicWalkSpeed)) {
      Integer publicTransport = parseSpeed(row.get(publicWalkSpeed), language);
      if (row.containsAttribute(individualWalkSpeed)) {
        Integer individualTransport = parseSpeed(row.get(individualWalkSpeed), language);
        if (publicTransport.equals(individualTransport)) {
          return publicTransport;
        }
        System.err
            .println(
                "Different speed values für walk speed in public transport walk type and individual traffic walk type");
        return 0;
      }
      return publicTransport;
    }
    if (row.containsAttribute(individualWalkSpeed)) {
      return parseSpeed(row.get(individualWalkSpeed), language);
    }
    return 0;
  }

  protected int nodeNumberOf(Row row) {
    return row.valueAsInteger(nodeNumber());
  }

  protected String codeOf(Row row) {
    return row.get(attribute(StandardAttributes.code));
  }

  protected int indexOf(Row row) {
    return row.valueAsInteger(attribute(StandardAttributes.index));
  }

  protected int edgeIdOf(Row row) {
    return row.valueAsInteger(attribute(StandardAttributes.edgeId));
  }

  protected int ringIdOf(Row row) {
    return row.valueAsInteger(attribute(StandardAttributes.ringId));
  }

  protected Integer vehicleCombinationOf(Row row) {
    String combination = row.get(attribute(StandardAttributes.vehicleCombinationNumber));
    if (combination.isEmpty()) {
      return 0;
    }
    return Integer.valueOf(combination);
  }

  protected String typeOf(Row row) {
    return row.get(attribute(StandardAttributes.type));
  }

  protected int toTimeProfileElementIndexOf(Row row) {
    return row.valueAsInteger(attribute(StandardAttributes.toTimeProfileElementIndex));
  }

  protected int fromTimeProfileElementIndexOf(Row row) {
    return row.valueAsInteger(attribute(StandardAttributes.fromTimeProfileElementIndex));
  }

  protected int departureOf(Row row) {
    return parseTimeAsSeconds(row.get(attribute(StandardAttributes.departure)));
  }

  protected String profileNameOf(Row row) {
    return row.get(attribute(StandardAttributes.timeProfileName));
  }

  protected String lineRouteIdOf(Row row) {
    String lineName = row.get(lineName());
    String lineRouteName = row.get(attribute(StandardAttributes.lineRouteName));
    String lineRouteDirection = row.get(directionCode());
    return lineName + ";" + lineRouteName + ";" + lineRouteDirection;
  }

  protected String profileIdOf(Row row) {
    return lineRouteIdOf(row) + ";" + profileNameOf(row);
  }

  protected int idOf(Row row) {
    return row.valueAsInteger(id());
  }

}