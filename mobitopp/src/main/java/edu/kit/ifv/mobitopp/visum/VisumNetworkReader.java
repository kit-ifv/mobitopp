package edu.kit.ifv.mobitopp.visum;


import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.routes.Row;
import edu.kit.ifv.mobitopp.visum.routes.StreamReader;

public class VisumNetworkReader extends VisumBaseReader {

	static final double alwaysAllowed = 1.0;
	private final VisumReader reader;
  private File file;


	public VisumNetworkReader(VisumReader reader, NetfileLanguage language) {
		super(language);
	  this.reader=reader;
	}
	
	public VisumNetworkReader(VisumReader reader) {
	  this(reader, StandardNetfileLanguages.german());
	}

	public VisumNetwork readNetwork(String filename) {

		File file = new File(filename);

		return readNetwork(file);
	}

	public VisumNetwork readNetwork(File file) {
	this.file = file;
  long startTime = System.currentTimeMillis();
	long lastCurrentTime = startTime;

System.out.println("reading data...");
		Map<String,VisumTable> tables = reader.read(file, new ArrayList<String>());
System.out.println("done");

	long currentTime = System.currentTimeMillis();

	System.out.println("Reading raw data took " + ((currentTime-lastCurrentTime) / 1000) + " seconds");

		System.out.println("\n\n\n");

		for (String key : tables.keySet() ) {
			VisumTable table = tables.get(key);

			System.out.println(key + ": " + table.numberOfRows());
		}

		System.out.println("\n\n\n");

System.out.println("reading tables...");

	lastCurrentTime = System.currentTimeMillis();

		VisumTransportSystems transportSystems = readTransportSystems();
		VisumLinkTypes linkTypes = readLinkTypes(transportSystems);

System.out.println(" reading nodes...");
		Map<Integer, VisumNode> nodes = readNodes();

System.out.println(" reading links...");
		Map<Integer, VisumLink> links = readLinks(tables, nodes, transportSystems, linkTypes);

System.out.println(" reading turns...");
		Map<Integer, List<VisumTurn>> turns = readTurns(nodes, transportSystems);

System.out.println(" reading zones...");
		Map<Integer, VisumZone> zones = readZones();

System.out.println(" reading connectors...");
		Map<Integer, List<VisumConnector>> connectors = readConnectors(nodes, zones, transportSystems);

	currentTime = System.currentTimeMillis();

	System.out.println("Parsing network data took " + ((currentTime-lastCurrentTime) / 1000) + " seconds");

	lastCurrentTime = currentTime;

System.out.println(" public transport network...");
System.out.println(" reading other...");
		Map<Integer, VisumVehicleUnit> vehicleUnits = readVehicleUnits(tables, transportSystems);
		Map<Integer, VisumVehicleCombination> vehicleCombinations = readVehicleCombinations(tables, vehicleUnits);

System.out.println(" reading stop hierarchy...");
		Map<Integer, VisumPtStop> ptStops = readPtStations(tables);
		Map<Integer, VisumPtStopArea> ptStopAreas = readPtStopAreas(tables, nodes, ptStops);
		Map<Integer, VisumPtStopPoint> ptStopPoints = readPtStopPoints(tables, nodes, links, ptStopAreas, transportSystems);
		Map<StopAreaPair, VisumPtTransferWalkTimes> walkTimes = readTransferWalkTimesMatrix(tables, ptStopAreas);

System.out.println(" reading other...");
		Map<String, VisumPtLine> ptLines = readPtLines(tables, transportSystems);
		Map<String, VisumPtLineRoute> ptLineRoutes = readPtLineRoutes(tables, ptLines);
		readPtLineRouteElements(tables, ptLineRoutes, ptStopPoints, nodes);

System.out.println(" reading other...");
		Map<String,VisumPtTimeProfile> ptTimeProfiles = readPtTimeProfile(tables, ptLineRoutes);
		List<VisumPtVehicleJourney> ptVehicleJourneys
			= readPtVehicleJourneys(tables, ptLineRoutes, ptTimeProfiles, vehicleCombinations);

	currentTime = System.currentTimeMillis();

	System.out.println("Parsing public transport network data took " + ((currentTime-lastCurrentTime) / 1000) + " seconds");

	lastCurrentTime = currentTime;

System.out.println(" reading polygons...");

		SortedMap<Integer,VisumSurface> areas = readSurfaces();

	currentTime = System.currentTimeMillis();

	System.out.println("Parsing polygons took " + ((currentTime-lastCurrentTime) / 1000) + " seconds");

	lastCurrentTime = currentTime;

System.out.println(" reading custom data...");
		Map<Integer, VisumChargingFacility> chargingFacilities = readChargingFacilities();
		Map<Integer, VisumChargingPoint> chargingPoints = readChargingPoints();

		Map<Integer, VisumCarSharingStation> carSharingStationsStadtmobil = readCarSharingStadtmobil(tables);
		Map<Integer, VisumCarSharingStation> carSharingStationsFlinkster = readCarSharingFlinkster(tables);

		Map<String, Map<Integer, VisumCarSharingStation>> carSharingStations =
			new HashMap<>();

		carSharingStations.put("Stadtmobil",  Collections.unmodifiableMap(carSharingStationsStadtmobil));
		carSharingStations.put("Flinkster",  Collections.unmodifiableMap(carSharingStationsFlinkster));

	currentTime = System.currentTimeMillis();

	System.out.println("Parsing custom data " + ((currentTime-lastCurrentTime) / 1000) + " seconds");

	lastCurrentTime = currentTime;

System.out.println(" reading territories...");
		Map<Integer, VisumTerritory> territories = readTerritories(tables, areas);

	currentTime = System.currentTimeMillis();

	System.out.println("Parsing territory data " + ((currentTime-lastCurrentTime) / 1000) + " seconds");

	lastCurrentTime = currentTime;

		tables = null;
		System.gc();


		VisumNetwork network = new VisumNetwork(
																	transportSystems,
																	linkTypes, 
																	nodes,
																	links,
																	turns,
																	zones,
																	connectors,
																	vehicleCombinations,
																	ptStops,
																	ptStopAreas,
																	ptStopPoints,
																	walkTimes,
																	ptLines,
																	ptLineRoutes,
																	ptTimeProfiles,
																	ptVehicleJourneys,
																	areas,
																	chargingFacilities,
																	chargingPoints,
																	carSharingStations,
																	territories
														);

		currentTime = System.currentTimeMillis();

		System.out.println("Reading  data took " + ((currentTime-startTime) / 1000) + " seconds");

		return network;
	}

	private VisumTransportSystems readTransportSystems() {
	  String tableName = table(Table.transportSystems);
    Stream<Row> content = loadContentOf(tableName);
		VisumTransportSystemReader reader = new VisumTransportSystemReader(language);
		return reader.readTransportSystems(content);
	}

  Stream<Row> loadContentOf(String tableName) {
    return new StreamReader().read(file, tableName);
  }

  private VisumLinkTypes readLinkTypes(VisumTransportSystems allSystems) {
    VisumLinkTypeReader reader = new VisumLinkTypeReader(language);
    Stream<Row> rows = loadContentOf(table(Table.linkTypes));
    return reader.readLinkTypes(allSystems, rows);
  }

	static int walkSpeed(Row row, NetfileLanguage language) {
		String publicWalkSpeed = language.resolve(StandardAttributes.publicTransportWalkSpeed);
		String individualWalkSpeed = language.resolve(StandardAttributes.individualWalkSpeed);
		if (row.containsAttribute(publicWalkSpeed)) {
			Integer publicTransport = parseSpeed(row.get(publicWalkSpeed), language);
			if (row.containsAttribute(individualWalkSpeed)) {
				Integer individualTransport = parseSpeed(row.get(individualWalkSpeed), language);
				if (publicTransport.equals(individualTransport)) {
					return publicTransport;
				}
				System.err.println("Different speed values für walk speed in public transport walk type and individual traffic walk type");
				return 0;
			}
			return publicTransport;
		}
		if (row.containsAttribute(individualWalkSpeed)) {
			return parseSpeed(row.get(individualWalkSpeed), language);
		}
		return 0;
	}

  private Map<Integer, VisumNode> readNodes() {
    VisumNodeReader reader = new VisumNodeReader(language);
    Stream<Row> rows = loadContentOf(table(Table.nodes));
    return reader.readNodes(rows);
  }

  private Map<Integer, VisumLink> readLinks(
		Map<String,VisumTable> tables,
		Map<Integer, VisumNode> nodes, 
		VisumTransportSystems transportSystems, 
		VisumLinkTypes linkTypes
	) {

		VisumTable table = tables.get(table(Table.links));

		Map<Integer,VisumLink> data = new HashMap<>();

		for (int i=0; i<table.numberOfRows()-1; i+=2) {


			Integer id =	Integer.valueOf(table.getValue(i,number()));

			Row first = table.getRow(i);
			Row second = table.getRow(i+1);
      VisumOrientedLink link1 = readOrientedLink("" + id + ":1", nodes, first, transportSystems, linkTypes);
      VisumOrientedLink link2 = readOrientedLink("" + id + ":2", nodes, second, transportSystems, linkTypes);

			VisumLink tmp = new VisumLink(
															id,
															link1,
															link2
											);

			data.put(id, tmp);
		}

		return data;
	}

	private VisumOrientedLink readOrientedLink(
		String id,
		Map<Integer, VisumNode> nodes,
		Row row, 
		VisumTransportSystems allSystems, 
		VisumLinkTypes linkTypes
	) {

		VisumNode fromNode = nodes.get(row.valueAsInteger(fromNode()));
		VisumNode toNode = nodes.get(row.valueAsInteger(toNode()));
		String name = row.get(name());
		VisumLinkType linkType = linkTypes.getById(row.valueAsInteger(typeNumber()));
		String transportSystems = row.get(transportSystemsSet());
		VisumTransportSystemSet systemSet = VisumTransportSystemSet.getByCode(transportSystems, allSystems);
		Float distance = parseDistance(row.get(length()));
		Integer numberOfLanes = row.valueAsInteger(numberOfLanes());
		Integer capacity = row.valueAsInteger(capacityCar());
		Integer speed = parseSpeed(row.get(freeFlowSpeedCar()), language);
		int walkSpeed = walkSpeed(row, language);

		VisumOrientedLink link =  new VisumOrientedLink(
																		id,
																		fromNode,
																		toNode,
																		name,
																		linkType,
																		systemSet,
																		distance,
																		new VisumLinkAttributes(
																			numberOfLanes,
																			capacity,
																			speed,
																			walkSpeed
																		)
															);

		return link;
	}

  private Map<Integer, List<VisumTurn>> readTurns(
      Map<Integer, VisumNode> nodes, VisumTransportSystems allSystems) {
		Stream<Row> content = loadContentOf(table(Table.turns));
    Map<Integer, List<VisumTurn>> data = new VisumTurnsReader(language, nodes, allSystems)
        .readTurns(content);
    assignTurnsToNodes(nodes, data);
		return data;
	}

  public void assignTurnsToNodes(
      Map<Integer, VisumNode> nodes, Map<Integer, List<VisumTurn>> data) {
    for (Integer nodeId : nodes.keySet()) {
      VisumNode node = nodes.get(nodeId);
      if (data.containsKey(nodeId)) {
        List<VisumTurn> turns = data.get(nodeId);
        node.setTurns(turns);
      } else {
        System.out.println("\n\n\n nodeId= " + nodeId + " has no turns!!!\n\n\n");
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

  private Map<Integer, VisumVehicleUnit> readVehicleUnits(
		Map<String,VisumTable> tables, VisumTransportSystems allSystems
	) {
	  if (!tables.containsKey(table(Table.vehicleUnit))) {
      return emptyMap();
    }
		VisumTable table = tables.get(table(Table.vehicleUnit));
		if (null == table) {
			System.out.println("Vehicle units are missing!");
			return emptyMap();
		}

		Map<Integer, VisumVehicleUnit> data = new HashMap<>();

		for (int i=0; i<table.numberOfRows(); i++) {

			Integer id = Integer.valueOf(table.getValue(i,number()));

			String transportSystems = table.getValue(i, transportSystemsSet());
			VisumTransportSystemSet systemSet = VisumTransportSystemSet.getByCode(transportSystems, allSystems);
			VisumVehicleUnit tmp = new VisumVehicleUnit(
																		id,
																		table.getValue(i,code()),
																		table.getValue(i,name()),
																		systemSet,
																		Integer.valueOf(table.getValue(i,attribute(StandardAttributes.vehicleCapacity))),
																		Integer.valueOf(table.getValue(i,attribute(StandardAttributes.seats)))
													);

			data.put(id, tmp);
		}

		return data;
	}

  private Map<Integer, VisumVehicleCombination> readVehicleCombinations(
		Map<String,VisumTable> tables,
		Map<Integer, VisumVehicleUnit> vehicleUnits
	) {
	  if (!tables.containsKey(table(Table.vehicleCombinations))) {
      return emptyMap();
    }
	  if (!tables.containsKey(table(Table.vehicleUnitToCombinations))) {
      return emptyMap();
    }
		VisumTable vehicleCombinations = tables.get(table(Table.vehicleCombinations));
		VisumTable vehicleUnits2Combinations = tables.get(table(Table.vehicleUnitToCombinations));
		if (null == vehicleCombinations || null == vehicleUnits2Combinations) {
			System.out.println("Vehicle combinations are missing!");
			return emptyMap();
		}

		Map<Integer, VisumVehicleCombination> data = new HashMap<>();

		Map<Integer,Map<Integer,Integer>> units2combinations = new HashMap<>();

		for (int i=0; i<vehicleUnits2Combinations.numberOfRows(); i++) {

			Integer combinationId = Integer.valueOf(vehicleUnits2Combinations.getValue(i,vehicleCombinationNumber()));
			Integer unitId = Integer.valueOf(vehicleUnits2Combinations.getValue(i,attribute(StandardAttributes.vehicleUnitNumber)));
			Integer quantity = Integer.valueOf(vehicleUnits2Combinations.getValue(i,attribute(StandardAttributes.numberOfVehicleUnits)));

			if (!units2combinations.containsKey(combinationId)) {
				units2combinations.put(combinationId, new HashMap<Integer,Integer>());
			}

			units2combinations.get(combinationId).put(unitId,quantity);
		}

		for (int i=0; i<vehicleCombinations.numberOfRows(); i++) {

			Integer id = Integer.valueOf(vehicleCombinations.getValue(i,number()));

			Map<VisumVehicleUnit,Integer> units = new HashMap<>();

			for(Integer unitId : units2combinations.get(id).keySet()) {

				VisumVehicleUnit unit = vehicleUnits.get(unitId);
				Integer quantity = units2combinations.get(id).get(unitId);

				units.put(unit, quantity);
			}

			VisumVehicleCombination combination = new VisumVehicleCombination(
																								id,
																								vehicleCombinations.getValue(i, code()),
																								vehicleCombinations.getValue(i, name()),
																								units
																						);

			data.put(id, combination);
		}

		return data;
	}

  private Map<Integer, VisumPtStop> readPtStations(
		Map<String,VisumTable> tables
	) {
	  if (!tables.containsKey(table(Table.station))) {
      return emptyMap();
    }
	  
		VisumTable table = tables.get(table(Table.station));

		Map<Integer, VisumPtStop> data = new HashMap<>();

		for (int i=0; i<table.numberOfRows(); i++) {

			Integer id = Integer.valueOf(table.getValue(i,number()));

			VisumPtStop tmp = new VisumPtStop(
																		id,
																		table.getValue(i,code()),
																		table.getValue(i,name()),
																		Integer.valueOf(table.getValue(i,typeNumber())),
																		Float.parseFloat(table.getValue(i,xCoord())),
																		Float.parseFloat(table.getValue(i,yCoord()))
															);

			data.put(id, tmp);
		}

		return data;
	}

	private Map<Integer, VisumPtStopArea> readPtStopAreas(
		Map<String,VisumTable> tables,
		Map<Integer, VisumNode> nodes,
		Map<Integer, VisumPtStop> ptStops
	) {
	  if (!tables.containsKey(table(Table.stopArea))) {
      return emptyMap();
    }
		VisumTable table = tables.get(table(Table.stopArea));

		Map<Integer, VisumPtStopArea> data = new HashMap<>();

		for (int i=0; i<table.numberOfRows(); i++) {

			Integer id = Integer.valueOf(table.getValue(i,number()));
			Integer stopId = Integer.valueOf(table.getValue(i,attribute(StandardAttributes.stationNumber)));
			Integer nodeId = Integer.valueOf(table.getValue(i,nodeNumber()));

			VisumPtStopArea tmp = new VisumPtStopArea(
																		id,
																		ptStops.get(stopId),
																		table.getValue(i,code()),
																		table.getValue(i,name()),
																		nodes.get(nodeId),
																		Integer.valueOf(table.getValue(i,typeNumber())),
																		Float.parseFloat(table.getValue(i,xCoord())),
																		Float.parseFloat(table.getValue(i,yCoord()))
															);

			data.put(id, tmp);
		}

		return data;
	}

	private Map<Integer, VisumPtStopPoint> readPtStopPoints(
		Map<String,VisumTable> tables,
		Map<Integer, VisumNode> nodes,
		Map<Integer, VisumLink> links,
		Map<Integer, VisumPtStopArea> ptStopAreas, 
		VisumTransportSystems allSystems
	) {
	  if (!tables.containsKey(table(Table.stop))) {
      return emptyMap();
    }
		VisumTable table = tables.get(table(Table.stop));

		Map<Integer, VisumPtStopPoint> data = new HashMap<>();

		for (int i=0; i<table.numberOfRows(); i++) {

			Integer id = Integer.valueOf(table.getValue(i,number()));
			Integer areaId = Integer.valueOf(table.getValue(i,attribute(StandardAttributes.stopAreaNumber)));


			if (!table.getValue(i,nodeNumber()).isEmpty()) {

				Integer nodeId = Integer.valueOf(table.getValue(i,nodeNumber()));

				String transportSystems = table.getValue(i, transportSystemsSet());
				VisumTransportSystemSet systemSet = VisumTransportSystemSet.getByCode(transportSystems, allSystems);
				VisumPtStopPoint tmp = new VisumPtStopPoint.Node(
																		id,
																		ptStopAreas.get(areaId),
																		table.getValue(i,code()),
																		table.getValue(i,name()),
																		Integer.valueOf(table.getValue(i,typeNumber())),
																		systemSet,
																		Integer.valueOf(table.getValue(i,directed())) == 1,
																		nodes.get(nodeId)
															);
				data.put(id, tmp);
			} else {
				Integer nodeId = Integer.valueOf(table.getValue(i,fromNode()));
				Integer linkId = Integer.valueOf(table.getValue(i,attribute(StandardAttributes.linkNumber)));

				String transportSystems = table.getValue(i, transportSystemsSet());
				VisumTransportSystemSet systemSet = VisumTransportSystemSet.getByCode(transportSystems, allSystems);
				VisumPtStopPoint tmp = new VisumPtStopPoint.Link(
																		id,
																		ptStopAreas.get(areaId),
																		table.getValue(i,code()),
																		table.getValue(i,name()),
																		Integer.valueOf(table.getValue(i,typeNumber())),
																		systemSet,
																		table.getValue(i,directed()) == "1",
																		nodes.get(nodeId),
																		links.get(linkId),
																		Float.valueOf(table.getValue(i,attribute(StandardAttributes.relativePosition)))
															);
				data.put(id, tmp);
			}

		}

		return data;
	}

  private Map<StopAreaPair, VisumPtTransferWalkTimes> readTransferWalkTimesMatrix(
			Map<String, VisumTable> tables, Map<Integer, VisumPtStopArea> ptStopAreas) {
	  if (!tables.containsKey(table(Table.station))) {
      return emptyMap();
    }
		VisumTable table = tables.get(table(Table.transferWalkTimes));
		Map<StopAreaPair, VisumPtTransferWalkTimes> data = new HashMap<>();
		for (int i = 0; i < table.numberOfRows(); i++) {
			Integer fromAreaId = Integer.valueOf(table.getValue(i, attribute(StandardAttributes.fromStopArea)));
			Integer toAreaId = Integer.valueOf(table.getValue(i, attribute(StandardAttributes.toStopArea)));
			String vsysCode = table.getValue(i, transportSystemCode());
			Integer time = parseTime(table.getValue(i, attribute(StandardAttributes.time)));

			VisumPtStopArea fromArea = ptStopAreas.get(fromAreaId);
			VisumPtStopArea toArea = ptStopAreas.get(toAreaId);
			VisumPtTransferWalkTimes walkTime = new VisumPtTransferWalkTimes(fromArea, toArea,
					vsysCode, time);
			StopAreaPair key = new StopAreaPair(new StopArea(fromAreaId), new StopArea(toAreaId));
			data.put(key, walkTime);
		}

		return data;
	}

  private Map<String, VisumPtLine> readPtLines(
		Map<String,VisumTable> tables, VisumTransportSystems systems
	) {
	  if (!tables.containsKey(table(Table.line))) {
      return emptyMap();
    }
		VisumTable table = tables.get(table(Table.line));

		Map<String, VisumPtLine> data = new HashMap<>();

		for (int i=0; i<table.numberOfRows(); i++) {

			String name = table.getValue(i,name());

			String systemCode = table.getValue(i,transportSystemCode());
			VisumTransportSystem transportSystem = systems.getBy(systemCode);
			VisumPtLine tmp = new VisumPtLine(name, transportSystem);
			data.put(name, tmp);
		}

		return data;
	}

	private Map<String, VisumPtLineRoute> readPtLineRoutes(
		Map<String,VisumTable> tables,
		Map<String, VisumPtLine> ptLines
	) {
	  if (!tables.containsKey(table(Table.lineRoute))) {
      return emptyMap();
    }
		VisumTable table = tables.get(table(Table.lineRoute));

		Map<String, Map<String, VisumPtLineRoute>> data
		 	= new HashMap<>();

		Map<String, VisumPtLineRoute> result = new HashMap<>();

		for (int i=0; i<table.numberOfRows(); i++) {

			String lineName = table.getValue(i,lineName());
			String lineRouteName = table.getValue(i,name());
			String lineRouteDirection = table.getValue(i,directionCode());

			String id = lineName + ";" + lineRouteName + ";" + lineRouteDirection;

			VisumPtLineRouteDirection direction = direction(lineRouteDirection);

			if (!data.containsKey(lineName)) {
				data.put(lineName, new HashMap<String, VisumPtLineRoute>());
			}

			VisumPtLineRoute tmp = new VisumPtLineRoute(
																		ptLines.get(lineName),
																		lineRouteName,
																		direction
															);

			data.get(lineName).put(id,tmp);
			result.put(id, tmp);
		}

		for (String lineName : data.keySet()) {
			Map<String,VisumPtLineRoute> routes = data.get(lineName);

			ptLines.get(lineName).setLineRoutes(new ArrayList<>(routes.values()));
		}

		return result;
	}

  protected VisumPtLineRouteDirection direction(String lineRouteDirection) {
		return isInDirection(lineRouteDirection) ? VisumPtLineRouteDirection.H
		                    																						: VisumPtLineRouteDirection.R;
	}

	private boolean isInDirection(String lineRouteDirection) {
		return "H".equals(lineRouteDirection) || ">".equals(lineRouteDirection);
	}

	private void readPtLineRouteElements(
		Map<String,VisumTable> tables,
		Map<String, VisumPtLineRoute> ptLineRoutes,
		Map<Integer, VisumPtStopPoint> ptStopPoints,
		Map<Integer, VisumNode> nodes
	) {
	  if (!tables.containsKey(table(Table.lineRouteElement))) {
      return;
    }
		VisumTable table = tables.get(table(Table.lineRouteElement));

		Map<VisumPtLineRoute, SortedMap<Integer, VisumPtLineRouteElement>> data
			= new HashMap<>();

		for (int i=0; i<table.numberOfRows(); i++) {

			String lineName = table.getValue(i,lineName());
			String lineRouteName = table.getValue(i,lineRouteName());
			String lineRouteDirection = table.getValue(i,directionCode());

			String knotNr = table.getValue(i,nodeNumber());
			String hpunktNr = table.getValue(i,attribute(StandardAttributes.stopNumber));
			Integer index =	Integer.valueOf(table.getValue(i,index()));

			String id = lineName + ";" + lineRouteName + ";" + lineRouteDirection;

			VisumPtLineRoute route = ptLineRoutes.get(id);

			VisumPtLineRouteElement element = new VisumPtLineRouteElement(
																					route,
																					index,
																					table.getValue(i,attribute(StandardAttributes.isRoutePoint)).equals("1"),
																					knotNr.isEmpty() ? null : nodes.get(Integer.valueOf(knotNr)),
																					hpunktNr.isEmpty() ? null : ptStopPoints.get(Integer.valueOf(hpunktNr)),
																					parseDistance(table.getValue(i,attribute(StandardAttributes.toLength)))
																				);

			if (!data.containsKey(route)) {
				data.put(route, new TreeMap<Integer, VisumPtLineRouteElement>());
			}

			data.get(route).put(index,element);
		}

		for (VisumPtLineRoute route : ptLineRoutes.values()) {

			SortedMap<Integer, VisumPtLineRouteElement> elements = data.get(route);
			route.elements = elements;
		}
	}

  private Map<String,VisumPtTimeProfile> readPtTimeProfile(
		Map<String,VisumTable> tables,
		Map<String, VisumPtLineRoute> ptLineRoutes
	) {
	  if (!tables.containsKey(table(Table.timeProfileElement))) {
      return emptyMap();
    }
		VisumTable table = tables.get(table(Table.timeProfileElement));

		Map<String,Map<Integer,VisumPtTimeProfileElement>> elements
			= new HashMap<>();

		for (int i=0; i<table.numberOfRows(); i++) {

			String lineName = table.getValue(i,lineName());
			String lineRouteName = table.getValue(i,lineRouteName());
			String lineRouteDirection = table.getValue(i,directionCode());
			String profileName = table.getValue(i,timeProfileName());

			String lineId = lineName + ";" + lineRouteName + ";" + lineRouteDirection;
			String profileId = lineId + ";" + profileName;

			VisumPtLineRoute route = ptLineRoutes.get(lineId);

			int index = Integer.valueOf(table.getValue(i,index()));
			int lrelemindex = Integer.valueOf(table.getValue(i,attribute(StandardAttributes.lineRouteElementIndex)));
			boolean leave = table.getValue(i,attribute(StandardAttributes.getOff)).equals("1");
			boolean enter = table.getValue(i,attribute(StandardAttributes.board)).equals("1");
			int arrival = parseTimeAsSeconds(table.getValue(i,attribute(StandardAttributes.arrival)));
			int departure = parseTimeAsSeconds(table.getValue(i,departure()));

			VisumPtLineRouteElement lineRouteElement = route.elements.get(lrelemindex);


			VisumPtTimeProfileElement profileElement = new VisumPtTimeProfileElement(
																									route,
																									profileName,
																									index,
																									lrelemindex,
																									lineRouteElement,
																									leave,
																									enter,
																									arrival,
																									departure
																								);


			if (!elements.containsKey(profileId)) {
				elements.put(profileId, new HashMap<Integer,VisumPtTimeProfileElement>());
			}

			elements.get(profileId).put(index,profileElement);

		}



		table = tables.get(table(Table.timeProfile));

		Map<String,VisumPtTimeProfile> data = new HashMap<>();


		for (int i=0; i<table.numberOfRows(); i++) {

			String lineName = table.getValue(i,lineName());
			String lineRouteName = table.getValue(i,lineRouteName());
			String lineRouteDirection = table.getValue(i,directionCode());
			String profileName = table.getValue(i,name());

			String lineId = lineName + ";" + lineRouteName + ";" + lineRouteDirection;
			String profileId = lineId + ";" + profileName;


			VisumPtLineRoute route = ptLineRoutes.get(lineId);

			Map<Integer,VisumPtTimeProfileElement> routeElements = elements.get(profileId);

			VisumPtTimeProfile profile = new VisumPtTimeProfile(
																				profileName,
																				route,
																				routeElements
																		);

			data.put(profileId, profile);
		}


		return data;
	}

  private int parseTimeAsSeconds(String timeAsString) {

		String[] tmp = timeAsString.split(":");
		int hour   = Integer.valueOf(tmp[0]);
		int minute = Integer.valueOf(tmp[1]);
		int second = Integer.valueOf(tmp[2]);

		return ((hour*60)+minute)*60+second;
	}



	private List<VisumPtVehicleJourney> readPtVehicleJourneys(
		Map<String,VisumTable> tables,
		Map<String, VisumPtLineRoute> ptLineRoutes,
		Map<String,VisumPtTimeProfile> ptTimeProfiles,
		Map<Integer, VisumVehicleCombination> vehicleCombinations
	) {
	  if (!tables.containsKey(table(Table.vehicleJourneyPart))) {
			System.out.println("Vehicle journey parts are missing!");
			return emptyList();
		}

	  VisumTable table = tables.get(table(Table.vehicleJourneyPart));
		Map<Integer,List<VisumPtVehicleJourneySection>> sections
			= new HashMap<>();

		for (int i=0; i<table.numberOfRows(); i++) {

			int number = Integer.valueOf(table.getValue(i,number()));
			int journeyNumber = Integer.valueOf(table.getValue(i,attribute(StandardAttributes.vehicleJourneyNumber)));
			int fromIndex = Integer.valueOf(table.getValue(i,fromTimeProfileElementIndex()));
			int toIndex = Integer.valueOf(table.getValue(i,toTimeProfileElementIndex()));
			int day = Integer.valueOf(table.getValue(i,attribute(StandardAttributes.vehicleDayNumber)));
			int vehicleCombinationId = Integer.valueOf(table.getValue(i,vehicleCombinationNumber()));

			VisumPtVehicleJourneySection journeySection = new VisumPtVehicleJourneySection(
																												number,
																												journeyNumber,
																												fromIndex,
																												toIndex,
																												day,
																												vehicleCombinations.get(vehicleCombinationId)
																										);

			if (!sections.containsKey(journeyNumber)) {
				sections.put(journeyNumber, new ArrayList<VisumPtVehicleJourneySection>());
			}
				sections.get(journeyNumber).add(journeySection);
		}



		table = tables.get(table(Table.vehicleJourney));

		List<VisumPtVehicleJourney> data = new ArrayList<>();

		for (int i=0; i<table.numberOfRows(); i++) {

			String lineName = table.getValue(i,lineName());
			String lineRouteName = table.getValue(i,lineRouteName());
			String lineRouteDirection = table.getValue(i,directionCode());
			String profileName = table.getValue(i,timeProfileName());

			String lineId = lineName + ";" + lineRouteName + ";" + lineRouteDirection;
			String profileId = lineId + ";" + profileName;

			VisumPtLineRoute route = ptLineRoutes.get(lineId);
			VisumPtTimeProfile profile = ptTimeProfiles.get(profileId);

			int number = Integer.valueOf(table.getValue(i,number()));
			String name = table.getValue(i,name());
			int departure = parseTimeAsSeconds(table.getValue(i,departure()));
			int fromIndex = Integer.valueOf(table.getValue(i,fromTimeProfileElementIndex()));
			int toIndex = Integer.valueOf(table.getValue(i,toTimeProfileElementIndex()));

			VisumPtVehicleJourney journey = new VisumPtVehicleJourney(
																				number,
																				name,
																				departure,
																				route,
																				profile,
																				fromIndex,
																				toIndex,
																				sections.get(number)
																			);

			data.add(journey);
		}



	return data;
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
    POICategories categories = POICategories.from(content);
		if (categories.containsCode(chargingStations())) {
			return readChargingStations(categories);
		}
		return Collections.emptyMap();
	}

  private Map<Integer, VisumChargingFacility> readChargingStations(POICategories categories) {
    int nr = categories.numberByCode(chargingStations());
    Stream<Row> rows = loadContentOf(poiCategoryPrefix() + nr);
    return new VisumChargingFacilityReader(language).readStations(rows);
  }

  Map<Integer, VisumChargingPoint> readChargingPoints() {
    Stream<Row> content = loadContentOf(poiCategory());
    POICategories categories = POICategories.from(content);
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

	private  Map<Integer, VisumCarSharingStation> readCarSharingStadtmobil(
		Map<String,VisumTable> tables
	) {
		if (!tables.containsKey(poiCategory())) {
			return Collections.emptyMap();
		}
		final String categoryName = attribute(StandardAttributes.carsharingStadtmobil);

		Map<Integer, VisumCarSharingStation> data = new HashMap<>();

		POICategories categories = POICategories.from(tables.get(poiCategory()));

		if (!categories.containsCode(categoryName)) { return data; }

		int nr = categories.numberByCode(categoryName);

		VisumTable table = tables.get(poiCategoryPrefix() + nr);

		for (int i=0; i<table.numberOfRows(); i++) {

			Integer id = readStadtMobilId(table, i);

			VisumCarSharingStation tmp = new VisumCarSharingStation(
																		id,
																		table.getValue(i,name()),
																		Float.parseFloat(table.getValue(i,xCoord())),
																		Float.parseFloat(table.getValue(i,yCoord())),
																		Double.valueOf(table.getValue(i,numberOfVehicles())).intValue(),
																		table.getValue(i,town()) + " / " + table.getValue(i,streetIso8859()),
																		table.getValue(i,type())
													);

			data.put(id, tmp);
		}

		return data;
	}

  private int readStadtMobilId(VisumTable table, int i) {
    String attribute = attribute(StandardAttributes.objectId);
    if (table.containsAttribute(attribute)) {
      return Double.valueOf(table.getValue(i,attribute)).intValue();
    }
    return Double.valueOf(table.getValue(i,id())).intValue();
  }

  private  Map<Integer, VisumCarSharingStation> readCarSharingFlinkster(
		Map<String,VisumTable> tables
	) {
		if (!tables.containsKey(poiCategory())) {
			return Collections.emptyMap();
		}
		final String categoryName = attribute(StandardAttributes.carsharingFlinkster);

		Map<Integer, VisumCarSharingStation> data = new HashMap<>();

		POICategories categories = POICategories.from(tables.get(poiCategory()));

		if (!categories.containsCode(categoryName)) { return data; }

		int nr = categories.numberByCode(categoryName);

		VisumTable table = tables.get(poiCategoryPrefix() + nr);

		for (int i=0; i<table.numberOfRows(); i++) {

			Integer id = Double.valueOf(table.getValue(i,id())).intValue();

			VisumCarSharingStation tmp = new VisumCarSharingStation(
																		id,
																		table.getValue(i,name()),
																		Float.parseFloat(table.getValue(i,xCoord())),
																		Float.parseFloat(table.getValue(i,yCoord())),
																		Double.valueOf(table.getValue(i,numberOfVehicles())).intValue(),
																		table.getValue(i,town()) + " / " + table.getValue(i,streetIso8859()),
																		table.getValue(i,type())
													);

			data.put(id, tmp);
		}

		return data;
	}

  private Map<Integer,VisumTerritory> readTerritories(
		Map<String,VisumTable> tables,
		SortedMap<Integer,VisumSurface> polygons
	) {

		VisumTable table = tables.get(table(Table.territories));
		if (table == null) {
			return new HashMap<>();
		}

		Map<Integer,VisumTerritory> result = new HashMap<>();

		for (int i=0; i<table.numberOfRows(); i++) {
System.out.println("reading Territory " + i);

			int id = Integer.valueOf(table.getValue(i,number()));
			String code = code(table, i);
			String name = nameOf(table, i);
			int areaId = Integer.valueOf(table.getValue(i,areaId()));
			VisumSurface area = polygons.get(areaId);

			VisumTerritory t = new VisumTerritory(id, code, name, areaId, area);

			result.put(id,t);
		}

		return result;
	}

	private String nameOf(VisumTable table, int i) {
		if (table.containsAttribute(name())) {
			return table.getValue(i, name());
		}
		return table.getValue(i, attribute(StandardAttributes.item));
	}

	private String code(VisumTable table, int i) {
		String codeLc = attribute(StandardAttributes.codeLc);
    if (table.containsAttribute(codeLc)) {
			return table.getValue(i, codeLc);
		}
		return "";
	}

}
