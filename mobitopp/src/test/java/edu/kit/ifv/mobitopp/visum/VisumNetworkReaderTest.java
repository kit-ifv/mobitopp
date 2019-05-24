package edu.kit.ifv.mobitopp.visum;

import static edu.kit.ifv.mobitopp.visum.VisumNetworkReader.alwaysAllowed;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VisumNetworkReaderTest {

  private NetfileLanguage language;
  private VisumNetwork network;

  @BeforeEach
  public void initialise() {
    language = StandardNetfileLanguages.german();
    network = Example.createVisumNetwork();
  }
  
  @Test
  void loadsNetwork() throws Exception {
    VisumNetwork loadedNetwork = new VisumNetworkReader(new VisumReader()).readNetwork(networkFile());
    
    assertAll(
        () -> assertThat(loadedNetwork.transportSystems, is(equalTo(network.transportSystems))),
        () -> assertThat(loadedNetwork.vehicleCombinations, is(equalTo(network.vehicleCombinations))),
        () -> assertThat(loadedNetwork.areas, is(equalTo(network.areas))));
  }
  
	private File networkFile() throws URISyntaxException {
    return new File(VisumNetworkReaderTest.class.getResource("simpleNetwork.net").toURI());
  }

  @Test
	public void ignorePointsWithoutPower() throws Exception {
		Map<Integer, VisumChargingPoint> points = chargingPointsWithoutPower();

		assertThat(points, is(emptyMap()));
	}

	private Map<Integer, VisumChargingPoint> chargingPointsWithoutPower() {
		return reader().readChargingPoints(chargingPointWithoutPower());
	}

	private Map<String, VisumTable> chargingPointWithoutPower() {
		List<String> attributes = asList("NR", "XKOORD", "YKOORD", "ID", "LEISTUNG");
		String power = "";
		List<String> chargingPoint = asList("1", "2.0", "3.0", "4.0", power);
		VisumTable chargingPoints = new VisumTable("LP", attributes);
		chargingPoints.addRow(chargingPoint);
		Map<String, VisumTable> tables = new HashMap<>();
		tables.put(poiCategory(), poiCategoryChargingPoint());
		tables.put("POIOFCAT_1", chargingPoints);
		return tables;
	}

  private String poiCategory() {
    return language.resolve(Table.poiCategory);
  }

	@Test
	public void parsesChargingPoint() throws Exception {
		Map<Integer, VisumChargingPoint> points = chargingPoints();

		VisumChargingPoint parsedPoint = points.get(1);

		assertThat(parsedPoint.id, is(1));
		assertThat(parsedPoint.coord.x, is(2.0f));
		assertThat(parsedPoint.coord.y, is(3.0f));
		assertThat(parsedPoint.stationId, is(4));
		assertThat(parsedPoint.power, is(5.0f));
	}

	private Map<Integer, VisumChargingPoint> chargingPoints() {
		return reader().readChargingPoints(singleChargingPoint());
	}

	private Map<String, VisumTable> singleChargingPoint() {
		List<String> attributes = asList("NR", "XKOORD", "YKOORD", "ID", "LEISTUNG");
		List<String> chargingPoint = asList("1", "2.0", "3.0", "4.0", "5.0");
		VisumTable chargingPoints = new VisumTable("Ladepunkte", attributes);
		chargingPoints.addRow(chargingPoint);
		Map<String, VisumTable> tables = new HashMap<>();
		tables.put(poiCategory(), poiCategoryChargingPoint());
		tables.put("POIOFCAT_1", chargingPoints);
		return tables;
	}

	private VisumTable poiCategoryChargingPoint() {
		VisumTable poiCategories = new VisumTable(poiCategory(), asList("NR", "CODE"));
		poiCategories.addRow(asList("1", "Ladepunkte"));
		return poiCategories;
	}

	@Test
	public void parseMissingChargingFacilities() throws Exception {
		Map<String, VisumTable> tables = new HashMap<>();
		tables.put(poiCategory(), emptyPoiCategories());

		Map<Integer, VisumChargingFacility> chargingFacilities = chargingFacilities(tables);

		assertThat(chargingFacilities, is(emptyMap()));
	}

	private VisumTable emptyPoiCategories() {
		return new VisumTable(poiCategory(), asList("NR", "CODE"));
	}

	@Test
	public void parsesChargingStation() throws Exception {
		Map<Integer, VisumChargingFacility> parsedFacilities = chargingFacilities();

		VisumChargingFacility parsedFacility = parsedFacilities.get(1);

		assertThat(parsedFacility.id, is(1));
		assertThat(parsedFacility.coord.x, is(4.0f));
		assertThat(parsedFacility.coord.y, is(5.0f));
		assertThat(parsedFacility.type, is("STANDSAEULE"));
		assertThat(parsedFacility.vehicleType, is("ZWEIRAD_UND_VIERRAD"));
		assertThat(parsedFacility.publicType, is("OEFF"));
		assertThat(parsedFacility.geo_lat, is(closeTo(48.779, 1e-6)));
		assertThat(parsedFacility.geo_long, is(closeTo(9.129, 1e-6)));
		assertThat(parsedFacility.address, is("Ort, 12345, Strasse Nr"));
	}

	private Map<Integer, VisumChargingFacility> chargingFacilities() {
		Map<String, VisumTable> tables = chargingFacility();

		return chargingFacilities(tables);
	}

	private static Map<Integer, VisumChargingFacility> chargingFacilities(
			Map<String, VisumTable> tables) {
		return reader().readChargingFacilities(tables);
	}

	private Map<String, VisumTable> chargingFacility() {
		List<String> attributes = asList("XKOORD", "YKOORD", "ART", "FZ", "LATITUDE", "LONGITUDE",
				"LS_ID", "ORT", "PLZ", "PUB", "STRASSE");
		List<String> facility = asList("4", "5", "STANDSAEULE", "ZWEIRAD_UND_VIERRAD", "48.779",
				"9.129", "1", "Ort", "12345", "OEFF", "Strasse Nr");
		VisumTable facilities = new VisumTable("POIOFACT_46", attributes);
		facilities.addRow(facility);

		Map<String, VisumTable> tables = new HashMap<>();
		tables.put(poiCategory(), poiCategories());
		tables.put("POIOFCAT_1", facilities);
		return tables;
	}

	private VisumTable poiCategories() {
		VisumTable poiCategories = new VisumTable(poiCategory(), asList("NR", "CODE"));
		poiCategories.addRow(asList("1", "Ladestationen"));
		return poiCategories;
	}

	@Test
	public void privateChargingIsAlwaysAllowedWhenNothingIsConfigured() throws Exception {
		Map<Integer, VisumZone> zones = zones(withoutChargingProbability());

		VisumZone zone = zones.get(1);

		assertThat(zone.privateChargingProbability, is(alwaysAllowed));
	}

	@Test
	public void privateChargingIsAlwaysAllowedWhenProbabilityIsEmpty() throws Exception {
		Map<Integer, VisumZone> zones = zones(withEmptyChargingProbability());

		VisumZone zone = zones.get(1);

		assertThat(zone.privateChargingProbability, is(alwaysAllowed));
	}

	@Test
	public void parsesZones() throws Exception {
		Map<Integer, VisumZone> zones = zones();

		VisumZone zone = zones.get(1);

		assertThat(zone.id, is(1));
		assertThat(zone.name, is("single"));
		assertThat(zone.mainZoneId, is(2));
		assertThat(zone.type, is(3));
		assertThat(zone.areaId, is(8));
		assertThat(zone.chargingFacilities, is(9));
		assertThat(zone.freeFloatingCarSharingCompany, is(""));
		assertThat(zone.freeFloatingCarSharingArea, is(0));
		assertThat(zone.freeFloatingCarSharingCars, is(11));
		assertThat(zone.carsharingcarDensities, is(densities()));
		assertThat(zone.privateChargingProbability, is(0.15));
	}

	private Map<String, Float> densities() {
		HashMap<String, Float> densities = new HashMap<>();
		densities.put("Stadtmobil", 12.0f);
		densities.put("Flinkster", 13.0f);
		densities.put("Car2Go", 14.0f);
		return densities;
	}

	private Map<Integer, VisumZone> zones() {
		return zones(singleZone());
	}

	private Map<Integer, VisumZone> zones(Map<String, VisumTable> zones) {
		return reader().readZones(zones);
	}

	private static VisumNetworkReader reader() {
		return new VisumNetworkReader(new VisumReader(), StandardNetfileLanguages.german());
	}

	private Map<String, VisumTable> singleZone() {
		List<String> attributes = asList("NR", "NAME", "OBEZNR", "TYPNR", "XKOORD", "YKOORD",
				"FLAECHEID", "LADESTATIONEN", "CAR2GO_GEBIET", "CAR2GO_AUSGANGSZUSTAND", "FZ_FL_SM",
				"FZ_FL_FL", "FZ_FL_C2G", "ANTEIL_STE");
		VisumTable table = new VisumTable("BEZIRK", attributes);
		List<String> singleZone = asList("1", "single", "2", "3", "45", "67", "8", "9", "0", "11", "12",
				"13", "14", "15.0");
		table.addRow(singleZone);
		return singletonMap("BEZIRK", table);
	}

	private Map<String, VisumTable> withEmptyChargingProbability() {
		List<String> attributes = asList("NR", "NAME", "OBEZNR", "TYPNR", "XKOORD", "YKOORD",
				"FLAECHEID", "LADESTATIONEN", "CAR2GO_GEBIET", "CAR2GO_AUSGANGSZUSTAND", "FZ_FL_SM",
				"FZ_FL_FL", "FZ_FL_C2G", "ANTEIL_STE");
		VisumTable table = new VisumTable("BEZIRK", attributes);
		List<String> singleZone = asList("1", "single", "2", "3", "45", "67", "8", "9", "0", "11", "12",
				"13", "14", "");
		table.addRow(singleZone);
		return singletonMap("BEZIRK", table);
	}

	private Map<String, VisumTable> withoutChargingProbability() {
		List<String> attributes = asList("NR", "NAME", "OBEZNR", "TYPNR", "XKOORD", "YKOORD",
				"FLAECHEID", "LADESTATIONEN", "CAR2GO_GEBIET", "CAR2GO_AUSGANGSZUSTAND", "FZ_FL_SM",
				"FZ_FL_FL", "FZ_FL_C2G");
		VisumTable table = new VisumTable("BEZIRK", attributes);
		List<String> singleZone = asList("1", "single", "2", "3", "45", "67", "8", "9", "0", "11", "12",
				"13", "14");
		table.addRow(singleZone);
		return singletonMap("BEZIRK", table);
	}
}
