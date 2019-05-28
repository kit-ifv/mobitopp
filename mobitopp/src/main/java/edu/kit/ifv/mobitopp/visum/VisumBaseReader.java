package edu.kit.ifv.mobitopp.visum;

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

  protected String yCoord() {
    return attribute(StandardAttributes.yCoord)  ;
  }

  protected String xCoord() {
    return attribute(StandardAttributes.xCoord);
  }

  protected String typeNumber() {
    return attribute(StandardAttributes.typeNumber);
  }

  protected String name() {
    return attribute(StandardAttributes.name);
  }

  protected String number() {
    return attribute(StandardAttributes.number);
  }

  protected String freeFlowSpeedCar() {
    return attribute(StandardAttributes.freeFlowSpeedCar);
  }

  protected String capacityCar() {
    return attribute(StandardAttributes.capacityCar);
  }

  protected String numberOfLanes() {
    return attribute(StandardAttributes.numberOfLanes);
  }

  protected String length() {
    return attribute(StandardAttributes.length);
  }

  protected String transportSystemsSet() {
    return attribute(StandardAttributes.transportSystemSet);
  }

  protected String toNode() {
    return attribute(StandardAttributes.toNodeNumber);
  }

  protected String fromNode() {
    return attribute(StandardAttributes.fromNodeNumber);
  }

  protected String areaId() {
    return attribute(StandardAttributes.areaId);
  }

  protected String direction() {
    return attribute(StandardAttributes.direction);
  }

  protected String nodeNumber() {
    return attribute(StandardAttributes.nodeNumber);
  }

  protected String code() {
    return attribute(StandardAttributes.code);
  }

  protected String vehicleCombinationNumber() {
    return attribute(StandardAttributes.vehicleCombinationNumber);
  }

  protected String directed() {
    return attribute(StandardAttributes.directed);
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

  protected String index() {
    return attribute(StandardAttributes.index);
  }

  protected String lineRouteName() {
    return attribute(StandardAttributes.lineRouteName);
  }

  protected String departure() {
    return attribute(StandardAttributes.departure);
  }

  protected String timeProfileName() {
    return attribute(StandardAttributes.timeProfileName);
  }

  protected String toTimeProfileElementIndex() {
    return attribute(StandardAttributes.toTimeProfileElementIndex);
  }

  protected String fromTimeProfileElementIndex() {
    return attribute(StandardAttributes.fromTimeProfileElementIndex);
  }

  protected String ringId() {
    return attribute(StandardAttributes.ringId);
  }

  protected String id() {
    return attribute(StandardAttributes.id);
  }

  protected String edgeId() {
    return attribute(StandardAttributes.edgeId);
  }

  protected String chargingStations() {
    return attribute(StandardAttributes.chargingStationsCode);
  }

  protected String chargingPoints() {
    return attribute(StandardAttributes.chargingPoints);
  }

  protected String poiCategoryPrefix() {
    return table(Table.poiCategoryPrefix);
  }

  protected String power() {
    return attribute(StandardAttributes.power);
  }

  protected String streetIso8859() {
    return attribute(StandardAttributes.streetIso8859);
  }

  protected String type() {
    return attribute(StandardAttributes.type);
  }

  protected String town() {
    return attribute(StandardAttributes.town);
  }

  protected String numberOfVehicles() {
    return attribute(StandardAttributes.numberOfVehicles);
  }

  protected String poiCategory() {
    return table(Table.poiCategory);
  }

  protected static Integer parseSpeed(String value, NetfileLanguage language) {
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

  protected String travelTimeCarAttribute() {
  	return attribute(StandardAttributes.travelTimeCar);
  }

}