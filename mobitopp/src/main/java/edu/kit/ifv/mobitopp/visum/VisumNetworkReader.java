package edu.kit.ifv.mobitopp.visum;


import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class VisumNetworkReader {

	static final double alwaysAllowed = 1.0;
	private static final String transferWalkTimes = "UEBERGANGSGEHZEITHSTBER";
	private final VisumReader reader;


	public VisumNetworkReader(VisumReader reader) {
		this.reader=reader;
	}



	public VisumNetwork readNetwork(String filename) {

		File file = new File(filename);

		return readNetwork(file);
	}

	public VisumNetwork readNetwork(File file) {


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

		VisumTransportSystems transportSystems = readTransportSystems(tables);
		VisumLinkTypes linkTypes = readLinkTypes(tables, transportSystems);

System.out.println(" reading nodes...");
		Map<Integer, VisumNode> nodes = readNodes(tables);

System.out.println(" reading links...");
		Map<Integer, VisumLink> links = readLinks(tables, nodes, transportSystems, linkTypes);

System.out.println(" reading turns...");
		Map<Integer, List<VisumTurn>> turns = readTurns(tables, nodes, transportSystems);

System.out.println(" reading zones...");
		Map<Integer, VisumZone> zones = readZones(tables);

System.out.println(" reading connectors...");
		Map<Integer, List<VisumConnector>> connectors = readConnectors(tables, nodes, zones, transportSystems);

	currentTime = System.currentTimeMillis();

	System.out.println("Parsing network data took " + ((currentTime-lastCurrentTime) / 1000) + " seconds");

	lastCurrentTime = currentTime;

System.out.println(" public transport network...");
System.out.println(" reading other...");
		Map<Integer, VisumVehicleUnit> vehicleUnits = readVehicleUnits(tables, transportSystems);
		Map<Integer, VisumVehicleCombination> vehicleCombinations = readVehicleCombinations(tables, vehicleUnits);

System.out.println(" reading stop hierarchy...");
		Map<Integer, VisumPtStop> ptStops = readPtStops(tables);
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

		SortedMap<Integer,VisumSurface> areas = readSurfaces(tables);

	currentTime = System.currentTimeMillis();

	System.out.println("Parsing polygons took " + ((currentTime-lastCurrentTime) / 1000) + " seconds");

	lastCurrentTime = currentTime;

System.out.println(" reading custom data...");
		Map<Integer, VisumChargingFacility> chargingFacilities = readChargingFacilities(tables);
		Map<Integer, VisumChargingPoint> chargingPoints = readChargingPoints(tables);

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

	public VisumRoadNetwork readRoadNetwork(String filename) {

		File file = new File(filename);

		return readRoadNetwork(file);
	}

	public VisumRoadNetwork readRoadNetwork(File file) {

		List<String> tablesToRead = new ArrayList<>(
																	Arrays.asList(
																		"VSYS","STRECKENTYP","KNOTEN","STRECKE","ABBIEGER","ANBINDUNG","BEZIRK",
																		"POIKATEGORIE", "POIOFCAT_46", "POIOFCAT_45", "POIOFCAT_47",
																		"PUNKT", "ZWISCHENPUNKT", "KANTE", "TEILFLAECHENELEMENT", "FLAECHENELEMENT",
																		"GEBIET"
																	)
																);

System.out.println("reading data...");
		Map<String,VisumTable> tables = reader.read(file, tablesToRead);
System.out.println("done");


System.out.println("reading tables...");

		VisumTransportSystems transportSystems = readTransportSystems(tables);
		VisumLinkTypes linkTypes = readLinkTypes(tables, transportSystems);
		Map<Integer, VisumNode> nodes = readNodes(tables);
		Map<Integer, VisumLink> links = readLinks(tables, nodes, transportSystems, linkTypes);
		Map<Integer, List<VisumTurn>> turns = readTurns(tables, nodes, transportSystems);

		Map<Integer, VisumChargingFacility> chargingFacilities = readChargingFacilities(tables);
		Map<Integer, VisumChargingPoint> chargingPoints = readChargingPoints(tables);

		Map<Integer, VisumCarSharingStation> carSharingStationsStadtmobil = readCarSharingStadtmobil(tables);
		Map<Integer, VisumCarSharingStation> carSharingStationsFlinkster = readCarSharingFlinkster(tables);

		Map<String, Map<Integer, VisumCarSharingStation>> carSharingStations =
				new HashMap<>();

		carSharingStations.put("Stadtmobil",  Collections.unmodifiableMap(carSharingStationsStadtmobil));
		carSharingStations.put("Flinkster",  Collections.unmodifiableMap(carSharingStationsFlinkster));

		Map<Integer, VisumZone> zones = readZones(tables);
		Map<Integer, List<VisumConnector>> connectors = readConnectors(tables, nodes, zones, transportSystems);

		SortedMap<Integer,VisumSurface> areas = readSurfaces(tables);
		Map<Integer, VisumTerritory> territories = readTerritories(tables, areas);



		tables = null;
		System.gc();


		VisumRoadNetwork network = new VisumRoadNetwork(
																	transportSystems,
																	linkTypes,
																	nodes,
																	links,
																	turns,
																	zones,
																	areas,
																	territories,
																	connectors,
																	chargingFacilities,
																	chargingPoints,
																	carSharingStations
														);

		return network;
	}

	private VisumTransportSystems readTransportSystems(Map<String, VisumTable> tables) {
		VisumTable table = tables.get("VSYS");
		VisumTransportSystemReader reader = new VisumTransportSystemReader(table);
		return reader.readTransportSystems();
	}

	private VisumLinkTypes readLinkTypes(
			Map<String, VisumTable> tables, VisumTransportSystems allSystems) {
		VisumTable table = tables.get("STRECKENTYP");
		VisumLinkTypeReader reader = new VisumLinkTypeReader(table);
		return reader.readLinkTypes(allSystems);
	}
	
	static int walkSpeed(VisumTable table, int row) {
		String publicWalkSpeed = "VSTD-OEVSYS(F)";
		String individualWalkSpeed = "VMAX-IVSYS(FUSS)";
		if (table.containsAttribute(publicWalkSpeed)) {
			Integer publicTransport = parseSpeed(table.getValue(row, publicWalkSpeed));
			if (table.containsAttribute(individualWalkSpeed)) {
				Integer individualTransport = parseSpeed(table.getValue(row, individualWalkSpeed));
				if (publicTransport.equals(individualTransport)) {
					return publicTransport;
				}
				System.err.println("Different speed values für walk speed in public transport walk type and individual traffic walk type");
				return 0;
			}
			return publicTransport;
		}
		if (table.containsAttribute(individualWalkSpeed)) {
			return parseSpeed(table.getValue(row, individualWalkSpeed));
		}
		return 0;
	}

	private Map<Integer, VisumNode> readNodes(
		Map<String,VisumTable> tables
	) {

		VisumTable table = tables.get("KNOTEN");

		Map<Integer, VisumNode> data = new HashMap<>();

		for (int i=0; i<table.numberOfRows(); i++) {

			Integer id = Integer.valueOf(table.getValue(i,"NR"));

			VisumNode tmp = new VisumNode(
																		id,
																		table.getValue(i,"NAME"),
																		Integer.valueOf(table.getValue(i,"TYPNR")),
																		Float.parseFloat(table.getValue(i,"XKOORD")),
																		Float.parseFloat(table.getValue(i,"YKOORD")),
																		Float.parseFloat(table.getValue(i,"ZKOORD"))
													);

			data.put(id, tmp);
		}

		return data;
	}


	private Map<Integer, VisumLink> readLinks(
		Map<String,VisumTable> tables,
		Map<Integer, VisumNode> nodes, 
		VisumTransportSystems transportSystems, 
		VisumLinkTypes linkTypes
	) {

		VisumTable table = tables.get("STRECKE");

		Map<Integer,VisumLink> data = new HashMap<>();

		for (int i=0; i<table.numberOfRows()-1; i+=2) {


			Integer id =	Integer.valueOf(table.getValue(i,"NR"));

			VisumOrientedLink link1 = readOrientedLink("" + id + ":1", table, nodes, i, transportSystems, linkTypes);
			VisumOrientedLink link2 = readOrientedLink("" + id + ":2", table, nodes, i+1, transportSystems, linkTypes);

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
		VisumTable table,
		Map<Integer, VisumNode> nodes,
		int row, 
		VisumTransportSystems allSystems, 
		VisumLinkTypes linkTypes
	) {

		VisumNode fromNode = nodes.get(Integer.valueOf(table.getValue(row,"VONKNOTNR")));
		VisumNode toNode = nodes.get(Integer.valueOf(table.getValue(row,"NACHKNOTNR")));
		String name =  table.getValue(row,"NAME");
		VisumLinkType linkType = linkTypes.getById(Integer.valueOf(table.getValue(row,"TYPNR")));
		String transportSystems = table.getValue(row,"VSYSSET");
		VisumTransportSystemSet systemSet = VisumTransportSystemSet.getByCode(transportSystems, allSystems);
		Float distance = parseDistance(table.getValue(row,"LAENGE"));
		Integer numberOfLanes = Integer.valueOf(table.getValue(row,"ANZFAHRSTREIFEN"));
		Integer capacity = Integer.valueOf(table.getValue(row,"KAPIV"));
		Integer speed = parseSpeed(table.getValue(row,"V0IV"));
		int walkSpeed = walkSpeed(table, row);

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
		Map<String,VisumTable> tables,
		Map<Integer, VisumNode> nodes, 
		VisumTransportSystems allSystems
	) {

		VisumTable table = tables.get("ABBIEGER");
		if (null == table) {
			System.out.println("Turns are missing!");
			return emptyMap();
		}

		Map<Integer,List<VisumTurn>> data = new HashMap<>();

		for (int i=0; i<table.numberOfRows(); i++) {

			Integer nodeId = Integer.valueOf(table.getValue(i,"UEBERKNOTNR"));

			String transportSystems = table.getValue(i, "VSYSSET");
			VisumTransportSystemSet systemSet = VisumTransportSystemSet.getByCode(transportSystems, allSystems);
			VisumTurn turn = new VisumTurn(
																nodes.get(Integer.valueOf(table.getValue(i,"UEBERKNOTNR"))),
																nodes.get(Integer.valueOf(table.getValue(i,"VONKNOTNR"))),
																nodes.get(Integer.valueOf(table.getValue(i,"NACHKNOTNR"))),
																Integer.valueOf(table.getValue(i,"TYPNR")),
																systemSet,
																Integer.valueOf(table.getValue(i,"KAPIV")),
																parseTime(table.getValue(i,"T0IV"))
														);

			if (!data.containsKey(nodeId)) {
				data.put(nodeId, new ArrayList<VisumTurn>());
			}
			data.get(nodeId).add(turn);
		}

		for (Integer nodeId : nodes.keySet()) {

			VisumNode node = nodes.get(nodeId);

			if (data.containsKey(nodeId)) {
				List<VisumTurn> turns = data.get(nodeId);
				node.setTurns(turns);
			} else {
System.out.println("\n\n\n nodeId= " + nodeId + " has no turns!!!\n\n\n");
			}

		}

		return data;
	}

	Map<Integer, VisumZone> readZones(
		Map<String,VisumTable> tables
	) {

		VisumTable table = tables.get("BEZIRK");

		Map<Integer,VisumZone> data = new HashMap<>();

		for (int i=0; i<table.numberOfRows(); i++) {
			Integer id =	Integer.valueOf(table.getValue(i,"NR"));
			VisumZone tmp = zoneWithChargingStations(table, i, id);
			data.put(id, tmp);
		}

		return data;
	}

	protected VisumZone zoneWithChargingStations(VisumTable table, int i, Integer id) {
		String charging_facilities = chargingFacilities(table, i);
		String floatingCarArea = car2GoGebiet(table, i);
		String floatingCarNumber = floatingCarNumber(table, i);
		Map<String, Float> carsharingcarDensities = carSharingDensities(table, i);

		double privateChargingProbaibility = privateChargingProbability(table, i);
		float innerZonePublicTransport = innerZonePublicTransportTime(table, i);
		VisumZone tmp = new VisumZone(
													id,
													table.getValue(i,"NAME"),
													Integer.valueOf(table.getValue(i,"OBEZNR")),
													Integer.valueOf(table.getValue(i,"TYPNR")),
													Float.parseFloat(table.getValue(i,"XKOORD")),
													Float.parseFloat(table.getValue(i,"YKOORD")),
													Integer.valueOf(table.getValue(i,"FLAECHEID")),
													Integer.valueOf(charging_facilities),
													floatingCarArea.equals("1") ? "Car2Go" : "",
													Integer.valueOf(floatingCarArea),
													Integer.valueOf(floatingCarNumber),
													privateChargingProbaibility,
													carsharingcarDensities,
													innerZonePublicTransport 
										);
		return tmp;
	}

	protected float innerZonePublicTransportTime(VisumTable table, int i) {
		if (table.containsAttribute("DIAG_OEV")) {
			return Float.parseFloat(table.getValue(i, "DIAG_OEV"));
		}
		System.out.println(
				"No travel time for public transport inside a single zone is given. Using 0 as travel time.");
		return 0.0f;
	}

	protected Map<String, Float> carSharingDensities(VisumTable table, int i) {
		Map<String, Float> carsharingcarDensities = new HashMap<>();
		if (table.containsAttribute("FZ_FL_SM") && table.containsAttribute("FZ_FL_FL")
				&& table.containsAttribute("FZ_FL_C2G")) {
			carsharingcarDensities.put("Stadtmobil", Float.valueOf(table.getValue(i, "FZ_FL_SM")));
			carsharingcarDensities.put("Flinkster", Float.valueOf(table.getValue(i, "FZ_FL_FL")));
			carsharingcarDensities.put("Car2Go", Float.valueOf(table.getValue(i, "FZ_FL_C2G")));
		}
		return carsharingcarDensities;
	}

	protected String floatingCarNumber(VisumTable table, int i) {
		if (table.containsAttribute("CAR2GO_AUSGANGSZUSTAND")) {
			String floatingCarNumber = table.getValue(i, "CAR2GO_AUSGANGSZUSTAND");
			return floatingCarNumber.isEmpty() ? "0" : floatingCarNumber;
		}
		return "0";
	}

	protected String car2GoGebiet(VisumTable table, int i) {
		if (table.containsAttribute("CAR2GO_GEBIET")) {
			String floatingCarArea = table.getValue(i, "CAR2GO_GEBIET");
			return floatingCarArea.isEmpty() ? "0" : floatingCarArea;
		}
		return "0";
	}

	protected String chargingFacilities(VisumTable table, int i) {
		if (table.containsAttribute("LADESTATIONEN")) {
			return table.getValue(i, "LADESTATIONEN");
		}
		return "0";
	}

	protected static double privateChargingProbability(VisumTable table, int i) {
		if (table.containsAttribute("ANTEIL_STE")) {
			String anteilSte = table.getValue(i, "ANTEIL_STE");
			return anteilSte.isEmpty() ? alwaysAllowed : normalizeProbability(anteilSte);
		}
		return alwaysAllowed;
	}

	private static double normalizeProbability(String anteilSte) {
		double value = Double.parseDouble(anteilSte);
		return value / 100.0d;
	}

	private Map<Integer, List<VisumConnector>> readConnectors(
		Map<String,VisumTable> tables,
		Map<Integer, VisumNode> nodes,
		Map<Integer, VisumZone> zones, 
		VisumTransportSystems allSystems
	) {

		VisumTable table = tables.get("ANBINDUNG");
		if (null == table) {
			System.out.println("Connectors are missing!");
			return emptyMap();
		}

		Map<Integer,List<VisumConnector>> data = new HashMap<>();

		for (int i=0; i<table.numberOfRows(); i++) {

			VisumZone zone = zones.get(Integer.valueOf(table.getValue(i,"BEZNR")));
			VisumNode node = nodes.get(Integer.valueOf(table.getValue(i,"KNOTNR")));

			String transportSystems = table.getValue(i, "VSYSSET");
			VisumTransportSystemSet systemSet = VisumTransportSystemSet.getByCode(transportSystems, allSystems);
			VisumConnector tmp = new VisumConnector(
															zone,
															node,
															table.getValue(i,"RICHTUNG"),
															Integer.valueOf(table.getValue(i,"TYPNR")),
															systemSet,
															parseDistance(table.getValue(i,"LAENGE")),
															parseTime(table.getValue(i, travelTimeCarAttribute()))
											);

			if (!data.containsKey(zone.id)) {
				data.put(zone.id, new ArrayList<VisumConnector>());
			}
			data.get(zone.id).add(tmp);
		}

		return data;
	}

	protected String travelTimeCarAttribute() {
		return "T0-VSYS(P)";
	}

	static Integer parseSpeed(String value) {

		return Integer.valueOf(value.replace("km/h",""));
	}

	private Float parseDistance(String value) {

		return Float.valueOf(value.replace("km",""));
	}

	private Integer parseTime(String value) {

		return Integer.valueOf(value.replace("s",""));
	}


	private Map<Integer, VisumVehicleUnit> readVehicleUnits(
		Map<String,VisumTable> tables, VisumTransportSystems allSystems
	) {

		VisumTable table = tables.get("FZGEINHEIT");
		if (null == table) {
			System.out.println("Vehicle units are missing!");
			return emptyMap();
		}

		Map<Integer, VisumVehicleUnit> data = new HashMap<>();

		for (int i=0; i<table.numberOfRows(); i++) {

			Integer id = Integer.valueOf(table.getValue(i,"NR"));

			String transportSystems = table.getValue(i, "VSYSSET");
			VisumTransportSystemSet systemSet = VisumTransportSystemSet.getByCode(transportSystems, allSystems);
			VisumVehicleUnit tmp = new VisumVehicleUnit(
																		id,
																		table.getValue(i,"CODE"),
																		table.getValue(i,"NAME"),
																		systemSet,
																		Integer.valueOf(table.getValue(i,"GESAMTPL")),
																		Integer.valueOf(table.getValue(i,"SITZPL"))
													);

			data.put(id, tmp);
		}

		return data;
	}

	private Map<Integer, VisumVehicleCombination> readVehicleCombinations(
		Map<String,VisumTable> tables,
		Map<Integer, VisumVehicleUnit> vehicleUnits
	) {

		VisumTable vehicleCombinations = tables.get("FZGKOMB");
		VisumTable vehicleUnits2Combinations = tables.get("FZGEINHEITZUFZGKOMB");
		if (null == vehicleCombinations || null == vehicleUnits2Combinations) {
			System.out.println("Vehicle combinations are missing!");
			return emptyMap();
		}

		Map<Integer, VisumVehicleCombination> data = new HashMap<>();

		Map<Integer,Map<Integer,Integer>> units2combinations = new HashMap<>();

		for (int i=0; i<vehicleUnits2Combinations.numberOfRows(); i++) {

			Integer combinationId = Integer.valueOf(vehicleUnits2Combinations.getValue(i,"FZGKOMBNR"));
			Integer unitId = Integer.valueOf(vehicleUnits2Combinations.getValue(i,"FZGEINHEITNR"));
			Integer quantity = Integer.valueOf(vehicleUnits2Combinations.getValue(i,"ANZFZGEINH"));

			if (!units2combinations.containsKey(combinationId)) {
				units2combinations.put(combinationId, new HashMap<Integer,Integer>());
			}

			units2combinations.get(combinationId).put(unitId,quantity);
		}

		for (int i=0; i<vehicleCombinations.numberOfRows(); i++) {

			Integer id = Integer.valueOf(vehicleCombinations.getValue(i,"NR"));

			Map<VisumVehicleUnit,Integer> units = new HashMap<>();

			for(Integer unitId : units2combinations.get(id).keySet()) {

				VisumVehicleUnit unit = vehicleUnits.get(unitId);
				Integer quantity = units2combinations.get(id).get(unitId);

				units.put(unit, quantity);
			}

			VisumVehicleCombination combination = new VisumVehicleCombination(
																								id,
																								vehicleCombinations.getValue(i, "CODE"),
																								vehicleCombinations.getValue(i, "NAME"),
																								units
																						);

			data.put(id, combination);
		}

		return data;
	}

	private Map<Integer, VisumPtStop> readPtStops(
		Map<String,VisumTable> tables
	) {

		VisumTable table = tables.get("HALTESTELLE");

		Map<Integer, VisumPtStop> data = new HashMap<>();

		for (int i=0; i<table.numberOfRows(); i++) {

			Integer id = Integer.valueOf(table.getValue(i,"NR"));

			VisumPtStop tmp = new VisumPtStop(
																		id,
																		table.getValue(i,"CODE"),
																		table.getValue(i,"NAME"),
																		Integer.valueOf(table.getValue(i,"TYPNR")),
																		Float.parseFloat(table.getValue(i,"XKOORD")),
																		Float.parseFloat(table.getValue(i,"YKOORD"))
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

		VisumTable table = tables.get("HALTESTELLENBEREICH");

		Map<Integer, VisumPtStopArea> data = new HashMap<>();

		for (int i=0; i<table.numberOfRows(); i++) {

			Integer id = Integer.valueOf(table.getValue(i,"NR"));
			Integer stopId = Integer.valueOf(table.getValue(i,"HSTNR"));
			Integer nodeId = Integer.valueOf(table.getValue(i,"KNOTNR"));

			VisumPtStopArea tmp = new VisumPtStopArea(
																		id,
																		ptStops.get(stopId),
																		table.getValue(i,"CODE"),
																		table.getValue(i,"NAME"),
																		nodes.get(nodeId),
																		Integer.valueOf(table.getValue(i,"TYPNR")),
																		Float.parseFloat(table.getValue(i,"XKOORD")),
																		Float.parseFloat(table.getValue(i,"YKOORD"))
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

		VisumTable table = tables.get("HALTEPUNKT");

		Map<Integer, VisumPtStopPoint> data = new HashMap<>();

		for (int i=0; i<table.numberOfRows(); i++) {

			Integer id = Integer.valueOf(table.getValue(i,"NR"));
			Integer areaId = Integer.valueOf(table.getValue(i,"HSTBERNR"));


			if (!table.getValue(i,"KNOTNR").isEmpty()) {

				Integer nodeId = Integer.valueOf(table.getValue(i,"KNOTNR"));

				String transportSystems = table.getValue(i, "VSYSSET");
				VisumTransportSystemSet systemSet = VisumTransportSystemSet.getByCode(transportSystems, allSystems);
				VisumPtStopPoint tmp = new VisumPtStopPoint.Node(
																		id,
																		ptStopAreas.get(areaId),
																		table.getValue(i,"CODE"),
																		table.getValue(i,"NAME"),
																		Integer.valueOf(table.getValue(i,"TYPNR")),
																		systemSet,
																		Integer.valueOf(table.getValue(i,"GERICHTET")) == 1,
																		nodes.get(nodeId)
															);
				data.put(id, tmp);
			} else {
				Integer nodeId = Integer.valueOf(table.getValue(i,"VONKNOTNR"));
				Integer linkId = Integer.valueOf(table.getValue(i,"STRNR"));

				String transportSystems = table.getValue(i, "VSYSSET");
				VisumTransportSystemSet systemSet = VisumTransportSystemSet.getByCode(transportSystems, allSystems);
				VisumPtStopPoint tmp = new VisumPtStopPoint.Link(
																		id,
																		ptStopAreas.get(areaId),
																		table.getValue(i,"CODE"),
																		table.getValue(i,"NAME"),
																		Integer.valueOf(table.getValue(i,"TYPNR")),
																		systemSet,
																		table.getValue(i,"GERICHTET") == "1",
																		nodes.get(nodeId),
																		links.get(linkId),
																		Float.valueOf(table.getValue(i,"RELPOS"))
															);
				data.put(id, tmp);
			}

		}

		return data;
	}

	private Map<StopAreaPair, VisumPtTransferWalkTimes> readTransferWalkTimesMatrix(
			Map<String, VisumTable> tables, Map<Integer, VisumPtStopArea> ptStopAreas) {
		VisumTable table = tables.get(transferWalkTimes);
		Map<StopAreaPair, VisumPtTransferWalkTimes> data = new HashMap<>();
		for (int i = 0; i < table.numberOfRows(); i++) {
			Integer fromAreaId = Integer.valueOf(table.getValue(i, "VONHSTBERNR"));
			Integer toAreaId = Integer.valueOf(table.getValue(i, "NACHHSTBERNR"));
			String vsysCode = table.getValue(i, "VSYSCODE");
			Integer time = parseTime(table.getValue(i, "ZEIT"));

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

		VisumTable table = tables.get("LINIE");

		Map<String, VisumPtLine> data = new HashMap<>();

		for (int i=0; i<table.numberOfRows(); i++) {

			String name = table.getValue(i,"NAME");

			String systemCode = table.getValue(i,"VSYSCODE");
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

		VisumTable table = tables.get("LINIENROUTE");

		Map<String, Map<String, VisumPtLineRoute>> data
		 	= new HashMap<>();

		Map<String, VisumPtLineRoute> result = new HashMap<>();

		for (int i=0; i<table.numberOfRows(); i++) {

			String lineName = table.getValue(i,"LINNAME");
			String lineRouteName = table.getValue(i,"NAME");
			String lineRouteDirection = table.getValue(i,"RICHTUNGCODE");

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

		VisumTable table = tables.get("LINIENROUTENELEMENT");

		Map<VisumPtLineRoute, SortedMap<Integer, VisumPtLineRouteElement>> data
			= new HashMap<>();

		for (int i=0; i<table.numberOfRows(); i++) {

			String lineName = table.getValue(i,"LINNAME");
			String lineRouteName = table.getValue(i,"LINROUTENAME");
			String lineRouteDirection = table.getValue(i,"RICHTUNGCODE");

			String knotNr = table.getValue(i,"KNOTNR");
			String hpunktNr = table.getValue(i,"HPUNKTNR");
			Integer index =	Integer.valueOf(table.getValue(i,"INDEX"));

			String id = lineName + ";" + lineRouteName + ";" + lineRouteDirection;

			VisumPtLineRoute route = ptLineRoutes.get(id);

			VisumPtLineRouteElement element = new VisumPtLineRouteElement(
																					route,
																					index,
																					table.getValue(i,"ISTROUTENPUNKT").equals("1"),
																					knotNr.isEmpty() ? null : nodes.get(Integer.valueOf(knotNr)),
																					hpunktNr.isEmpty() ? null : ptStopPoints.get(Integer.valueOf(hpunktNr)),
																					parseDistance(table.getValue(i,"NACHLAENGE"))
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

		VisumTable table = tables.get("FAHRZEITPROFILELEMENT");

		Map<String,Map<Integer,VisumPtTimeProfileElement>> elements
			= new HashMap<>();

		for (int i=0; i<table.numberOfRows(); i++) {

			String lineName = table.getValue(i,"LINNAME");
			String lineRouteName = table.getValue(i,"LINROUTENAME");
			String lineRouteDirection = table.getValue(i,"RICHTUNGCODE");
			String profileName = table.getValue(i,"FZPROFILNAME");

			String lineId = lineName + ";" + lineRouteName + ";" + lineRouteDirection;
			String profileId = lineId + ";" + profileName;

			VisumPtLineRoute route = ptLineRoutes.get(lineId);

			int index = Integer.valueOf(table.getValue(i,"INDEX"));
			int lrelemindex = Integer.valueOf(table.getValue(i,"LRELEMINDEX"));
			boolean leave = table.getValue(i,"AUS").equals("1");
			boolean enter = table.getValue(i,"EIN").equals("1");
			int arrival = parseTimeAsSeconds(table.getValue(i,"ANKUNFT"));
			int departure = parseTimeAsSeconds(table.getValue(i,"ABFAHRT"));

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



		table = tables.get("FAHRZEITPROFIL");

		Map<String,VisumPtTimeProfile> data = new HashMap<>();


		for (int i=0; i<table.numberOfRows(); i++) {

			String lineName = table.getValue(i,"LINNAME");
			String lineRouteName = table.getValue(i,"LINROUTENAME");
			String lineRouteDirection = table.getValue(i,"RICHTUNGCODE");
			String profileName = table.getValue(i,"NAME");

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

		VisumTable table = tables.get("FAHRPLANFAHRTABSCHNITT");
		if (null == table) {
			System.out.println("Vehicle journey parts are missing!");
			return emptyList();
		}

		Map<Integer,List<VisumPtVehicleJourneySection>> sections
			= new HashMap<>();

		for (int i=0; i<table.numberOfRows(); i++) {

			int number = Integer.valueOf(table.getValue(i,"NR"));
			int journeyNumber = Integer.valueOf(table.getValue(i,"FPLFAHRTNR"));
			int fromIndex = Integer.valueOf(table.getValue(i,"VONFZPELEMINDEX"));
			int toIndex = Integer.valueOf(table.getValue(i,"NACHFZPELEMINDEX"));
			int day = Integer.valueOf(table.getValue(i,"VTAGNR"));
			int vehicleCombinationId = Integer.valueOf(table.getValue(i,"FZGKOMBNR"));

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



		table = tables.get("FAHRPLANFAHRT");

		List<VisumPtVehicleJourney> data = new ArrayList<>();

		for (int i=0; i<table.numberOfRows(); i++) {

			String lineName = table.getValue(i,"LINNAME");
			String lineRouteName = table.getValue(i,"LINROUTENAME");
			String lineRouteDirection = table.getValue(i,"RICHTUNGCODE");
			String profileName = table.getValue(i,"FZPROFILNAME");

			String lineId = lineName + ";" + lineRouteName + ";" + lineRouteDirection;
			String profileId = lineId + ";" + profileName;

			VisumPtLineRoute route = ptLineRoutes.get(lineId);
			VisumPtTimeProfile profile = ptTimeProfiles.get(profileId);

			int number = Integer.valueOf(table.getValue(i,"NR"));
			String name = table.getValue(i,"NAME");
			int departure = parseTimeAsSeconds(table.getValue(i,"ABFAHRT"));
			int fromIndex = Integer.valueOf(table.getValue(i,"VONFZPELEMINDEX"));
			int toIndex = Integer.valueOf(table.getValue(i,"NACHFZPELEMINDEX"));

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

	private class RingInfo {

		public final int ring_id;
		public final int enclave;

		public RingInfo(int ring_id, int enclave) {

			this.ring_id=ring_id;
			this.enclave=enclave;
		}

	}


	private SortedMap<Integer,VisumSurface> readSurfaces(
		Map<String,VisumTable> tables
	) {

		Map<Integer,VisumPoint> points = readPoints(tables);
		Map<Integer,SortedMap<Integer,VisumPoint>> intermediatePoints = readIntermediatePoints(tables);
		Map<Integer,VisumEdge> lines = readLines(points, intermediatePoints, tables);
		Map<Integer,VisumFace> rings = readFaces(lines, tables);


		SortedMap<Integer,VisumSurface> polys = new TreeMap<>();

		VisumTable table = tables.get("FLAECHENELEMENT");

		Map<Integer,List<RingInfo>> tmp = new TreeMap<>();

		for (int i=0; i<table.numberOfRows(); i++) {

			int id = Integer.valueOf(table.getValue(i,"FLAECHEID"));
			int ring_id = Integer.valueOf(table.getValue(i,"TFLAECHEID"));
			int enclave = Integer.valueOf(table.getValue(i,"ENKLAVE"));

			if (!tmp.containsKey(id)) {
				tmp.put(id, new ArrayList<RingInfo>());
			}

			tmp.get(id).add(new RingInfo(ring_id, enclave));
		}
		for (Integer id: tmp.keySet()) {

			List<VisumFace> faces = new ArrayList<>();
			List<Integer> enclave = new ArrayList<>();

			for (RingInfo ri : tmp.get(id)) {

				VisumFace f = rings.get(ri.ring_id);

				faces.add(f);
				enclave.add(ri.enclave);
			}

			VisumSurface surface = new VisumSurface(id, faces, enclave);
			polys.put(id, surface);
		}

		return polys;
	}


	private Map<Integer,VisumPoint> readPoints(
		Map<String,VisumTable> tables
	) {

		VisumTable table = tables.get("PUNKT");

		Map<Integer,VisumPoint> result = new HashMap<>();

		for (int i=0; i<table.numberOfRows(); i++) {

			int id = Integer.valueOf(table.getValue(i,"ID"));
			float x = Float.parseFloat(table.getValue(i,"XKOORD"));
			float y = Float.parseFloat(table.getValue(i,"YKOORD"));

			VisumPoint p = new VisumPoint(x,y);

			result.put(id,p);
		}

		return result;
	}


	private Map<Integer,SortedMap<Integer,VisumPoint>> readIntermediatePoints(
		Map<String,VisumTable> tables
	) {

		VisumTable table = tables.get("ZWISCHENPUNKT");
		if (table == null) {
			return new HashMap<>();
		}

		Map<Integer,SortedMap<Integer,VisumPoint>> result = new HashMap<>();

		for (int i=0; i<table.numberOfRows(); i++) {

			int edgeId = Integer.valueOf(table.getValue(i,"KANTEID"));
			int index = Integer.valueOf(table.getValue(i,"INDEX"));
			float x = Float.parseFloat(table.getValue(i,"XKOORD"));
			float y = Float.parseFloat(table.getValue(i,"YKOORD"));

			VisumPoint p = new VisumPoint(x,y);

			if (!result.containsKey(edgeId)) {
				result.put(edgeId, new TreeMap<Integer,VisumPoint>());
			}

			result.get(edgeId).put(index,p);
		}

		return result;
	}


	private Map<Integer,VisumEdge> readLines(
		Map<Integer,VisumPoint> points,
		Map<Integer,SortedMap<Integer,VisumPoint>> intermediatePoints,
		Map<String,VisumTable> tables
	) {

		VisumTable table = tables.get("KANTE");

		Map<Integer,VisumEdge> result = new HashMap<>();

		for (int i=0; i<table.numberOfRows(); i++) {

			int id = Integer.valueOf(table.getValue(i,"ID"));
			int from_id = Integer.valueOf(table.getValue(i,"VONPUNKTID"));
			int to_id = Integer.valueOf(table.getValue(i,"NACHPUNKTID"));

			VisumPoint from = points.get(from_id);
			VisumPoint to = points.get(to_id);

			List<VisumPoint> intermediate = intermediatePoints.containsKey(id)
																				? new ArrayList<>(intermediatePoints.get(id).values())
																				: new ArrayList<>();

			assert from != null;
			assert to != null;

			VisumEdge p = new VisumEdge(id, from, to, intermediate);

			result.put(id,p);
		}

		return result;
	}

	private class RingElement {

		private final int line_id;
		private final int direction;

		public RingElement(int line_id, int direction) {
			this.line_id=line_id;
			this.direction=direction;
		}
	}


	private Map<Integer,VisumFace> readFaces(
		Map<Integer,VisumEdge> lines,
		Map<String,VisumTable> tables
	) {

		VisumTable table = tables.get("TEILFLAECHENELEMENT");

		Map<Integer,VisumFace> result = new HashMap<>();

		TreeMap<Integer,Map<Integer,RingElement>> tmp = new TreeMap<>();


		for (int i=0; i<table.numberOfRows(); i++) {

			int id = Integer.valueOf(table.getValue(i,"TFLAECHEID"));
			int idx = Integer.valueOf(table.getValue(i,"INDEX"));
			int line_id = Integer.valueOf(table.getValue(i,"KANTEID"));
			int direction = Integer.valueOf(table.getValue(i,"RICHTUNG"));

			if (!tmp.containsKey(id)) {
				tmp.put(id, new TreeMap<Integer,RingElement>());
			}

			tmp.get(id).put(idx, new RingElement(line_id, direction));
		}


		for (Integer id : tmp.keySet()) {

			List<VisumEdge> edges = new ArrayList<>();

			List<Integer> direction = new ArrayList<>();


			for(Integer idx: tmp.get(id).keySet()) {

				RingElement re = tmp.get(id).get(idx);

				VisumEdge line = lines.get(re.line_id);

				edges.add(line);
				direction.add(re.direction);
			}


			VisumFace face = new VisumFace(id, edges, direction);

			result.put(id, face);
		}

		return result;
	}


	Map<Integer, VisumChargingFacility> readChargingFacilities(
		Map<String,VisumTable> tables
	) {
		if (!tables.containsKey("POIKATEGORIE")) {
			return Collections.emptyMap();
		}
		POICategories categories = new POICategories(tables.get("POIKATEGORIE"));
		if (categories.containsCode("Ladestationen")) {
			return readChargingStations(tables, categories);
		}
		return Collections.emptyMap();
	}

	private Map<Integer, VisumChargingFacility> readChargingStations(
			Map<String, VisumTable> tables, POICategories categories) {
		int nr = categories.numberByCode("Ladestationen");
		VisumTable table = tables.get("POIOFCAT_" + nr);
		Map<Integer, VisumChargingFacility> data = new HashMap<>();
		for (int i=0; i<table.numberOfRows(); i++) {

			String lsId = table.getValue(i,"LS_ID");
			Integer id = Integer.valueOf(lsId);

			String visumLatitude = table.getValue(i,"LATITUDE");
			String visumLongitude = table.getValue(i,"LONGITUDE");
			Double latitude = Double.valueOf(visumLatitude);
			Double longitude = Double.valueOf(visumLongitude);
			VisumChargingFacility tmp = new VisumChargingFacility(
																		id,
																		Float.parseFloat(table.getValue(i,"XKOORD")),
																		Float.parseFloat(table.getValue(i,"YKOORD")),
																		table.getValue(i,"ART"),
																		table.getValue(i,"FZ"),
																		table.getValue(i,"PUB"),
																		latitude,
																		longitude,
																		table.getValue(i,"ORT") + ", " +
																		table.getValue(i,"PLZ") + ", " +
																		table.getValue(i,"STRASSE")
													);

			data.put(id, tmp);
		}
		return data;
	}

	Map<Integer, VisumChargingPoint> readChargingPoints(
			Map<String,VisumTable> tables
		) {
		if (!tables.containsKey("POIKATEGORIE")) {
			return Collections.emptyMap();
		}
		POICategories categories = new POICategories(tables.get("POIKATEGORIE"));
		if (categories.containsCode("Ladepunkte")) {
			return readChargingPoints(tables, categories);
		}
		return emptyMap();
	}

	private Map<Integer, VisumChargingPoint> readChargingPoints(
			Map<String, VisumTable> tables, POICategories categories) {
		int nr = categories.numberByCode("Ladepunkte");
		VisumTable table = tables.get("POIOFCAT_" + nr);
		Map<Integer, VisumChargingPoint> data = new HashMap<>();
		for (int i = 0; i < table.numberOfRows(); i++) {
			int id = Integer.parseInt(table.getValue(i, "NR"));
			if (elementHasNoPower(table, i)) {
				continue;
			}
			VisumChargingPoint chargingPoint = createChargingPointWith(id, table, i);
			data.put(id, chargingPoint);
		}
		return data;
	}

	private boolean elementHasNoPower(VisumTable table, int i) {
		return table.getValue(i, "LEISTUNG").isEmpty();
	}

	private VisumChargingPoint createChargingPointWith(int id, VisumTable table, int i) {
		float xCoord = Float.parseFloat(table.getValue(i, "XKOORD"));
		float yCoord = Float.parseFloat(table.getValue(i, "YKOORD"));
		String station = table.getValue(i, "ID");
		int stationId = (int) Double.parseDouble(station.isEmpty() ? "0" : station);
		float power = Float.parseFloat(table.getValue(i, "LEISTUNG"));
		return new VisumChargingPoint(id, xCoord, yCoord, stationId, power);
	}

	private  Map<Integer, VisumCarSharingStation> readCarSharingStadtmobil(
		Map<String,VisumTable> tables
	) {
		if (!tables.containsKey("POIKATEGORIE")) {
			return Collections.emptyMap();
		}
		final String categoryName = "CS_SM";

		Map<Integer, VisumCarSharingStation> data = new HashMap<>();

		POICategories categories = new POICategories(tables.get("POIKATEGORIE"));

		if (!categories.containsCode(categoryName)) { return data; }

		int nr = categories.numberByCode(categoryName);

		VisumTable table = tables.get("POIOFCAT_" + nr);

		for (int i=0; i<table.numberOfRows(); i++) {

			Integer id = Double.valueOf(table.getValue(i,"OBJECTID")).intValue();

			VisumCarSharingStation tmp = new VisumCarSharingStation(
																		id,
																		table.getValue(i,"NAME"),
																		Float.parseFloat(table.getValue(i,"XKOORD")),
																		Float.parseFloat(table.getValue(i,"YKOORD")),
																		Double.valueOf(table.getValue(i,"ANZAHL_FZ")).intValue(),
																		table.getValue(i,"STADT") + " / " + table.getValue(i,"STRA\u00DFE"),
																		table.getValue(i,"TYP")
													);

			data.put(id, tmp);
		}

		return data;
	}

	private  Map<Integer, VisumCarSharingStation> readCarSharingFlinkster(
		Map<String,VisumTable> tables
	) {
		if (!tables.containsKey("POIKATEGORIE")) {
			return Collections.emptyMap();
		}
		final String categoryName = "CS_FL";

		Map<Integer, VisumCarSharingStation> data = new HashMap<>();

		POICategories categories = new POICategories(tables.get("POIKATEGORIE"));

		if (!categories.containsCode(categoryName)) { return data; }

		int nr = categories.numberByCode(categoryName);

		VisumTable table = tables.get("POIOFCAT_" + nr);

		for (int i=0; i<table.numberOfRows(); i++) {

			Integer id = Double.valueOf(table.getValue(i,"ID")).intValue();

			VisumCarSharingStation tmp = new VisumCarSharingStation(
																		id,
																		table.getValue(i,"NAME"),
																		Float.parseFloat(table.getValue(i,"XKOORD")),
																		Float.parseFloat(table.getValue(i,"YKOORD")),
																		Double.valueOf(table.getValue(i,"ANZAHL_FZ")).intValue(),
																		table.getValue(i,"STADT") + " / " + table.getValue(i,"STRA\u00DFE"),
																		table.getValue(i,"TYP")
													);

			data.put(id, tmp);
		}

		return data;
	}

	private Map<Integer,VisumTerritory> readTerritories(
		Map<String,VisumTable> tables,
		SortedMap<Integer,VisumSurface> polygons
	) {

		VisumTable table = tables.get("GEBIET");
		if (table == null) {
			return new HashMap<>();
		}

		Map<Integer,VisumTerritory> result = new HashMap<>();

		for (int i=0; i<table.numberOfRows(); i++) {
System.out.println("reading Territorry " + i);

			int id = Integer.valueOf(table.getValue(i,"NR"));
			String code = code(table, i);
			String name = nameOf(table, i);
			int areaId = Integer.valueOf(table.getValue(i,"FLAECHEID"));
			VisumSurface area = polygons.get(areaId);

			VisumTerritory t = new VisumTerritory(id, code, name, areaId, area);

			result.put(id,t);
		}

		return result;
	}

	private String nameOf(VisumTable table, int i) {
		if (table.containsAttribute("NAME")) {
			return table.getValue(i, "NAME");
		}
		return table.getValue(i, "ITEM");
	}

	private String code(VisumTable table, int i) {
		if (table.containsAttribute("CODE_LC")) {
			return table.getValue(i, "CODE_LC");
		}
		return "";
	}

}
