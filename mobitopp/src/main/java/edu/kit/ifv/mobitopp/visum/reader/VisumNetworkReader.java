package edu.kit.ifv.mobitopp.visum.reader;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.util.StopWatch;
import edu.kit.ifv.mobitopp.util.dataimport.Row;
import edu.kit.ifv.mobitopp.visum.CachedVisumReader;
import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.POICategories;
import edu.kit.ifv.mobitopp.visum.StandardAttributes;
import edu.kit.ifv.mobitopp.visum.StandardNetfileLanguages;
import edu.kit.ifv.mobitopp.visum.StopAreaPair;
import edu.kit.ifv.mobitopp.visum.Table;
import edu.kit.ifv.mobitopp.visum.VisumCarSharingStation;
import edu.kit.ifv.mobitopp.visum.VisumChargingFacility;
import edu.kit.ifv.mobitopp.visum.VisumChargingPoint;
import edu.kit.ifv.mobitopp.visum.VisumConnector;
import edu.kit.ifv.mobitopp.visum.VisumEdge;
import edu.kit.ifv.mobitopp.visum.VisumFace;
import edu.kit.ifv.mobitopp.visum.VisumLink;
import edu.kit.ifv.mobitopp.visum.VisumLinkTypes;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.VisumNode;
import edu.kit.ifv.mobitopp.visum.VisumPoint;
import edu.kit.ifv.mobitopp.visum.VisumPtLine;
import edu.kit.ifv.mobitopp.visum.VisumPtLineRoute;
import edu.kit.ifv.mobitopp.visum.VisumPtLineRouteDirection;
import edu.kit.ifv.mobitopp.visum.VisumPtLineRouteElement;
import edu.kit.ifv.mobitopp.visum.VisumPtStop;
import edu.kit.ifv.mobitopp.visum.VisumPtStopArea;
import edu.kit.ifv.mobitopp.visum.VisumPtStopPoint;
import edu.kit.ifv.mobitopp.visum.VisumPtTimeProfile;
import edu.kit.ifv.mobitopp.visum.VisumPtTimeProfileElement;
import edu.kit.ifv.mobitopp.visum.VisumPtTransferWalkTimes;
import edu.kit.ifv.mobitopp.visum.VisumPtVehicleJourney;
import edu.kit.ifv.mobitopp.visum.VisumPtVehicleJourneySection;
import edu.kit.ifv.mobitopp.visum.VisumSurface;
import edu.kit.ifv.mobitopp.visum.VisumTerritory;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystems;
import edu.kit.ifv.mobitopp.visum.VisumTurn;
import edu.kit.ifv.mobitopp.visum.VisumVehicleCombination;
import edu.kit.ifv.mobitopp.visum.VisumVehicleCombinationUnit;
import edu.kit.ifv.mobitopp.visum.VisumVehicleUnit;
import edu.kit.ifv.mobitopp.visum.VisumZone;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VisumNetworkReader extends VisumBaseReader {

  private final StopWatch stopWatch;
  private File file;
  private VisumFileReader visumReader;
	private String ptSystemCode;

  public VisumNetworkReader(NetfileLanguage language) {
    super(language);
    stopWatch = new StopWatch(LocalDateTime::now);
  }

  public VisumNetworkReader() {
    this(StandardNetfileLanguages.defaultSystems().german());
  }

  public VisumNetwork readNetwork(File file, String ptSystemCode) {
		stopWatch.start();
    this.file = file;
    this.ptSystemCode = ptSystemCode;
    visumReader = new CachedVisumReader();

    log.info("reading tables...");
    VisumTransportSystems transportSystems = readTransportSystems();
    finishStep("transport systems");
    VisumLinkTypes linkTypes = readLinkTypes(transportSystems);
    finishStep("link types");

    log.info(" reading nodes...");
    Map<Integer, VisumNode> nodes = readNodes();
    finishStep("nodes");

    log.info(" reading links...");
    Map<Integer, VisumLink> links = readLinks(nodes, transportSystems, linkTypes);
    finishStep("links");

    log.info(" reading turns...");
    Map<Integer, List<VisumTurn>> turns = readTurns(nodes, transportSystems);
    finishStep("turns");

    log.info(" reading zones...");
    Map<Integer, VisumZone> zones = readZones();
    finishStep("zones");

    log.info(" reading connectors...");
    Map<Integer, List<VisumConnector>> connectors = readConnectors(nodes, zones, transportSystems);
    finishStep("connectors");

    log.info(" public transport network...");
    log.info(" reading other...");
    Map<Integer, VisumVehicleUnit> vehicleUnits = readVehicleUnits(transportSystems);
    finishStep("vehicle units");
    Map<Integer, VisumVehicleCombination> vehicleCombinations = readVehicleCombinations(
        vehicleUnits);
    finishStep("vehicle combinations");

    log.info(" reading stop hierarchy...");
    Map<Integer, VisumPtStop> ptStops = readPtStations();
    finishStep("stops");
    Map<Integer, VisumPtStopArea> ptStopAreas = readPtStopAreas(nodes, ptStops);
    finishStep("stop areas");
    Map<Integer, VisumPtStopPoint> ptStopPoints = readPtStopPoints(nodes, links, ptStopAreas,
        transportSystems);
    finishStep("stop points");
    Map<StopAreaPair, VisumPtTransferWalkTimes> walkTimes = readTransferWalkTimesMatrix(
        ptStopAreas);
    finishStep("walk times");

    log.info(" reading other...");
    Map<String, VisumPtLine> ptLines = readPtLines(transportSystems);
    finishStep("lines");
    Map<String, VisumPtLineRoute> ptLineRoutes = readPtLineRoutes(ptLines);
    finishStep("line routes");
    readPtLineRouteElements(ptLineRoutes, ptStopPoints, nodes);

    log.info(" reading other...");
    Map<String, VisumPtTimeProfile> ptTimeProfiles = readPtTimeProfile(ptLineRoutes);
    finishStep("time profiles");
    List<VisumPtVehicleJourney> ptVehicleJourneys = readPtVehicleJourneys(ptLineRoutes,
        ptTimeProfiles, vehicleCombinations);
    finishStep("vehicle journeys");

    log.info(" reading polygons...");
    SortedMap<Integer, VisumSurface> areas = readSurfaces();
    finishStep("surfaces");

    log.info(" reading custom data...");
    Map<Integer, VisumChargingFacility> chargingFacilities = readChargingFacilities();
    finishStep("charging facilities");
    Map<Integer, VisumChargingPoint> chargingPoints = readChargingPoints();
    finishStep("charging points");

    Map<Integer, VisumCarSharingStation> carSharingStationsStadtmobil = readCarSharingStadtmobil();
    finishStep("car sharing stations stadtmobil");
    Map<Integer, VisumCarSharingStation> carSharingStationsFlinkster = readCarSharingFlinkster();
    finishStep("car sharing stations flinkster");

    Map<String, Map<Integer, VisumCarSharingStation>> carSharingStations = new HashMap<>();
    carSharingStations.put("Stadtmobil", Collections.unmodifiableMap(carSharingStationsStadtmobil));
    carSharingStations.put("Flinkster", Collections.unmodifiableMap(carSharingStationsFlinkster));
    finishStep("car sharing stations");

    log.info(" reading territories...");
    Map<Integer, VisumTerritory> territories = readTerritories(areas);
    finishStep("territories");

    VisumNetwork network = new VisumNetwork(transportSystems, linkTypes, nodes, links, turns, zones,
        connectors, vehicleCombinations, ptStops, ptStopAreas, ptStopPoints, walkTimes, ptLines,
        ptLineRoutes, ptTimeProfiles, ptVehicleJourneys, areas, chargingFacilities, chargingPoints,
        carSharingStations, territories);

    System.gc();
    finishStep("garbage collection");
    printRuntimeInformation();
    return network;
  }

  private void printRuntimeInformation() {
    stopWatch.forEach((m, d) -> log.info(m + " " + d));
  }

  private void finishStep(String message) {
    log.info("{} loaded", message);
    stopWatch.measurePoint(message);
  }

  private VisumTransportSystems readTransportSystems() {
    String tableName = table(Table.transportSystems);
    Stream<Row> content = loadContentOf(tableName);
    VisumTransportSystemReader reader = new VisumTransportSystemReader(language);
    return reader.readTransportSystems(content);
  }

  Stream<Row> loadContentOf(String tableName) {
    return visumReader.read(file, tableName);
  }

  private VisumLinkTypes readLinkTypes(VisumTransportSystems allSystems) {
    VisumLinkTypeReader reader = new VisumLinkTypeReader(language);
    Stream<Row> rows = loadContentOf(table(Table.linkTypes));
    return reader.readLinkTypes(allSystems, rows);
  }

  private Map<Integer, VisumNode> readNodes() {
    VisumNodeReader reader = new VisumNodeReader(language);
    Stream<Row> rows = loadContentOf(table(Table.nodes));
    return reader.readNodes(rows);
  }

  private Map<Integer, VisumLink> readLinks(
      Map<Integer, VisumNode> nodes, VisumTransportSystems transportSystems,
      VisumLinkTypes linkTypes) {
    Stream<Row> content = loadContentOf(table(Table.links));
    return new VisumLinkReader(language, nodes, transportSystems, linkTypes).readLinks(content);
  }

  private Map<Integer, List<VisumTurn>> readTurns(
      Map<Integer, VisumNode> nodes, VisumTransportSystems allSystems) {
    Stream<Row> content = loadContentOf(table(Table.turns));
    Map<Integer, List<VisumTurn>> data = new VisumTurnsReader(language, nodes, allSystems)
        .readTurns(content);
    assignTurnsToNodes(nodes, data);
    return data;
  }

  private void assignTurnsToNodes(
      Map<Integer, VisumNode> nodes, Map<Integer, List<VisumTurn>> data) {
    for (Integer nodeId : nodes.keySet()) {
      VisumNode node = nodes.get(nodeId);
      if (data.containsKey(nodeId)) {
        List<VisumTurn> turns = data.get(nodeId);
        node.setTurns(turns);
      } else {
        log.error("\n\n\n nodeId= " + nodeId + " has no turns!!!\n\n\n");
      }
    }
  }

  Map<Integer, VisumZone> readZones() {
    Stream<Row> content = loadContentOf(table(Table.zones));
    return new VisumZoneReader(language).readZones(content);
  }

  private Map<Integer, List<VisumConnector>> readConnectors(
      Map<Integer, VisumNode> nodes, Map<Integer, VisumZone> zones,
      VisumTransportSystems allSystems) {
    Stream<Row> content = loadContentOf(table(Table.connectors));
    return new VisumConnectorReader(language, nodes, zones, allSystems).readConnectors(content);
  }

  private Map<Integer, VisumVehicleUnit> readVehicleUnits(VisumTransportSystems allSystems) {
    Stream<Row> content = loadContentOf(table(Table.vehicleUnit));
    Map<Integer, VisumVehicleUnit> vehicleUnits = new VisumVehicleUnitReader(language, allSystems)
        .readVehicleUnits(content);
    if (vehicleUnits.isEmpty()) {
      log.error("Vehicle units are missing!");
    }
    return vehicleUnits;
  }

  private Map<Integer, VisumVehicleCombination> readVehicleCombinations(
      Map<Integer, VisumVehicleUnit> vehicleUnits) {
    Stream<Row> content = loadContentOf(table(Table.vehicleUnitToCombinations));
    Map<Integer, List<VisumVehicleCombinationUnit>> units2combinations = new VisumVehicleUnitsToCombinationsReader(
        language, vehicleUnits).readMapping(content);

    Stream<Row> combinationContent = loadContentOf(table(Table.vehicleCombinations));
    Map<Integer, VisumVehicleCombination> combinations = new VisumVehicleCombinationReader(language,
        units2combinations).readCombinations(combinationContent);
    if (combinations.isEmpty()) {
      log.error("Vehicle combinations are missing!");
    }
    return combinations;
  }

  private Map<Integer, VisumPtStop> readPtStations() {
    Stream<Row> content = loadContentOf(table(Table.station));
    return new VisumPtStationReader(language).readPtStops(content);
  }

  private Map<Integer, VisumPtStopArea> readPtStopAreas(
      Map<Integer, VisumNode> nodes, Map<Integer, VisumPtStop> ptStops) {
    Stream<Row> content = loadContentOf(table(Table.stopArea));
    return new VisumPtStopAreaReader(language, nodes, ptStops).readPtStopAreas(content);
  }

  private Map<Integer, VisumPtStopPoint> readPtStopPoints(
      Map<Integer, VisumNode> nodes, Map<Integer, VisumLink> links,
      Map<Integer, VisumPtStopArea> ptStopAreas, VisumTransportSystems allSystems) {
    Stream<Row> content = loadContentOf(table(Table.stop));
    return new VisumPtStopPointReader(language, nodes, links, ptStopAreas, allSystems)
        .readPtStopPoints(content);
  }

  private Map<StopAreaPair, VisumPtTransferWalkTimes> readTransferWalkTimesMatrix(
      Map<Integer, VisumPtStopArea> ptStopAreas) {
    Stream<Row> content = loadContentOf(table(Table.transferWalkTimes));
    return new VisumPtTransferWalkTimesReader(language, ptStopAreas, ptSystemCode).readTransferWalkTimes(content);
  }

  private Map<String, VisumPtLine> readPtLines(VisumTransportSystems systems) {
    Stream<Row> content = loadContentOf(table(Table.line));
    return new VisumPtLineReader(language, systems).readPtLines(content);
  }

  private Map<String, VisumPtLineRoute> readPtLineRoutes(Map<String, VisumPtLine> ptLines) {
    Stream<Row> content = loadContentOf(table(Table.lineRoute));
    Map<String, VisumPtLineRoute> result = new VisumPtLineRouteReader(language, ptLines,
        this::direction).readPtLineRoutes(content);
    assignRoutesToLines(ptLines, result);
    return result;
  }

  private void assignRoutesToLines(
      Map<String, VisumPtLine> ptLines, Map<String, VisumPtLineRoute> result) {
    result
        .values()
        .stream()
        .collect(groupingBy(r -> r.line.name))
        .entrySet()
        .forEach(e -> ptLines.get(e.getKey()).setLineRoutes(e.getValue()));
  }

  private VisumPtLineRouteDirection direction(String lineRouteDirection) {
    return isInDirection(lineRouteDirection) ? VisumPtLineRouteDirection.H
        : VisumPtLineRouteDirection.R;
  }

  private boolean isInDirection(String lineRouteDirection) {
    return "H".equals(lineRouteDirection) || ">".equals(lineRouteDirection);
  }

  private void readPtLineRouteElements(
      Map<String, VisumPtLineRoute> ptLineRoutes, Map<Integer, VisumPtStopPoint> ptStopPoints,
      Map<Integer, VisumNode> nodes) {
    Stream<Row> content = loadContentOf(table(Table.lineRouteElement));

    Map<VisumPtLineRoute, SortedMap<Integer, VisumPtLineRouteElement>> data = new VisumPtLineRouteElementReader(
        language, ptLineRoutes, ptStopPoints, nodes).readElements(content);

    data.entrySet().forEach(e -> e.getKey().setElements(e.getValue()));
  }

  private Map<String, VisumPtTimeProfile> readPtTimeProfile(
      Map<String, VisumPtLineRoute> ptLineRoutes) {
    Stream<Row> content = loadContentOf(table(Table.timeProfileElement));
    Map<String, Map<Integer, VisumPtTimeProfileElement>> elements = new VisumPtTimeProfileElementReader(
        language, ptLineRoutes).readElements(content);

    Stream<Row> profileContent = loadContentOf(table(Table.timeProfile));
    return new VisumPtTimeProfileReader(language, ptLineRoutes, elements)
        .readProfile(profileContent);
  }

  private List<VisumPtVehicleJourney> readPtVehicleJourneys(
      Map<String, VisumPtLineRoute> ptLineRoutes, Map<String, VisumPtTimeProfile> ptTimeProfiles,
      Map<Integer, VisumVehicleCombination> vehicleCombinations) {
    Stream<Row> content = loadContentOf(table(Table.vehicleJourneyPart));
    Map<Integer, List<VisumPtVehicleJourneySection>> sections = new VisumPtVehicleJourneySectionReader(
        language, vehicleCombinations).readSections(content);

    if (sections.isEmpty()) {
      log.error("Vehicle journey parts are missing!");
    }

    Stream<Row> journeyContent = loadContentOf(table(Table.vehicleJourney));
    return new VisumPtVehicleJourneyReader(language, ptLineRoutes, ptTimeProfiles, sections)
        .readJourneys(journeyContent);
  }

  private SortedMap<Integer, VisumSurface> readSurfaces() {
    Map<Integer, VisumPoint> points = readPoints();
    Map<Integer, SortedMap<Integer, VisumPoint>> intermediatePoints = readIntermediatePoints();
    Map<Integer, VisumEdge> lines = readEdges(points, intermediatePoints);
    Map<Integer, VisumFace> rings = readFaces(lines);

    Stream<Row> table = loadContentOf(table(Table.surface));
    return new VisumSurfaceReader(language, rings).readSurfaces(table);
  }

  private Map<Integer, VisumPoint> readPoints() {
    Stream<Row> rows = loadContentOf(table(Table.point));
    return new VisumPointReader(language).readPoints(rows);
  }

  private Map<Integer, SortedMap<Integer, VisumPoint>> readIntermediatePoints() {
    Stream<Row> content = loadContentOf(table(Table.intermediatePoint));
    return new VisumIntermediatePointReader(language).readPoints(content);
  }

  private Map<Integer, VisumEdge> readEdges(
      Map<Integer, VisumPoint> points,
      Map<Integer, SortedMap<Integer, VisumPoint>> intermediatePoints) {
    Stream<Row> content = loadContentOf(table(Table.edges));
    return new VisumEdgeReader(language, points, intermediatePoints).readEdges(content);
  }

  private Map<Integer, VisumFace> readFaces(Map<Integer, VisumEdge> lines) {
    Stream<Row> content = loadContentOf(table(Table.faces));
    return new VisumFaceReader(language, lines).readFaces(content);
  }

  Map<Integer, VisumChargingFacility> readChargingFacilities() {
    Stream<Row> content = loadContentOf(poiCategory());
    List<Row> rows = content.collect(toList());
    POICategories categories = POICategories.from(rows.stream(), language);
    if (categories.containsCode(chargingStations())) {
      return readChargingStations(categories);
    }
    return Collections.emptyMap();
  }

  private String poiCategory() {
    return table(Table.poiCategory);
  }

  private Map<Integer, VisumChargingFacility> readChargingStations(POICategories categories) {
    int nr = categories.numberByCode(chargingStations());
    Stream<Row> rows = loadContentOf(poiCategoryPrefix() + nr);
    return new VisumChargingFacilityReader(language).readStations(rows);
  }

  private String chargingStations() {
    return attribute(StandardAttributes.chargingStationsCode);
  }

  private String poiCategoryPrefix() {
    return table(Table.poiCategoryPrefix);
  }

  Map<Integer, VisumChargingPoint> readChargingPoints() {
    Stream<Row> content = loadContentOf(poiCategory());
    POICategories categories = POICategories.from(content, language);
    if (categories.containsCode(chargingPoints())) {
      return readChargingPoints(categories);
    }
    return emptyMap();
  }

  private Map<Integer, VisumChargingPoint> readChargingPoints(POICategories categories) {
    int nr = categories.numberByCode(chargingPoints());
    Stream<Row> content = loadContentOf(poiCategoryPrefix() + nr);
    return new VisumChargingPointReader(language).readPoints(content);
  }

  private String chargingPoints() {
    return attribute(StandardAttributes.chargingPoints);
  }

  private Map<Integer, VisumCarSharingStation> readCarSharingStadtmobil() {
    final String categoryName = attribute(StandardAttributes.carsharingStadtmobil);
    POICategories categories = POICategories.from(loadContentOf(poiCategory()), language);
    if (!categories.containsCode(categoryName)) {
      return emptyMap();
    }
    int nr = categories.numberByCode(categoryName);
    Stream<Row> content = loadContentOf(poiCategoryPrefix() + nr);
    return new VisumCarsharingReader(language).readCarsharingStations(content);
  }

  private Map<Integer, VisumCarSharingStation> readCarSharingFlinkster() {
    final String categoryName = attribute(StandardAttributes.carsharingFlinkster);
    POICategories categories = POICategories.from(loadContentOf(poiCategory()), language);
    if (!categories.containsCode(categoryName)) {
      return emptyMap();
    }
    int nr = categories.numberByCode(categoryName);
    Stream<Row> content = loadContentOf(poiCategoryPrefix() + nr);
    return new VisumCarsharingReader(language).readCarsharingStations(content);
  }

  private Map<Integer, VisumTerritory> readTerritories(SortedMap<Integer, VisumSurface> polygons) {
    Stream<Row> rows = loadContentOf(table(Table.territories));
    return new VisumTerritoryReader(language, polygons).readTerritories(rows);
  }

}
