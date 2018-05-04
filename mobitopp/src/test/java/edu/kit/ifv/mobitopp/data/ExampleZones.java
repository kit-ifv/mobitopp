package edu.kit.ifv.mobitopp.data;

import static java.util.Collections.emptyList;

import java.awt.geom.Point2D;

import edu.kit.ifv.mobitopp.dataimport.DefaultPower;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingDataForZone;
import edu.kit.ifv.mobitopp.simulation.emobility.LimitedChargingDataForZone;
import edu.kit.ifv.mobitopp.visum.VisumSurface;

public class ExampleZones {

	private final Zone someZone;
	private final Zone otherZone;
	
	public ExampleZones(Zone someZone, Zone otherZone) {
		super();
		this.someZone = someZone;
		this.otherZone = otherZone;
	}

	public static ExampleZones create() {
		Zone someZone = zoneWithId("1");
		Zone otherZone = zoneWithId("2");
		return new ExampleZones(someZone, otherZone);
	}

	private static Zone zoneWithId(String id) {
		String name = "zone " + id;
		ZoneAreaType areaType = ZoneAreaType.METROPOLITAN;
		ZoneClassificationType classification = ZoneClassificationType.areaOfInvestigation;
		ZonePolygon polygon = createZonePolygon();
		Attractivities attractivities = new Attractivities();
		ChargingDataForZone charging = createChargingData();
		return new Zone(id, name, areaType, classification, polygon, attractivities, charging);
	}

	private static LimitedChargingDataForZone createChargingData() {
		return new LimitedChargingDataForZone(emptyList(), DefaultPower.zero);
	}

	private static ZonePolygon createZonePolygon() {
		VisumSurface surface = new VisumSurface(emptyList(), emptyList());
		Point2D location = new Point2D.Double(0.0d, 0.0d);
		return new ZonePolygon(surface, location);
	}

	public Zone someZone() {
		return someZone;
	}

	public Zone otherZone() {
		return otherZone;
	}

}
