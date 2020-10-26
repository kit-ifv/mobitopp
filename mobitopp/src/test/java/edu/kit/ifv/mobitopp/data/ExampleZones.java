package edu.kit.ifv.mobitopp.data;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.data.areatype.ZoneAreaType;
import edu.kit.ifv.mobitopp.dataimport.DefaultPower;
import edu.kit.ifv.mobitopp.dataimport.DefaultRegionType;
import edu.kit.ifv.mobitopp.dataimport.RegionType;
import edu.kit.ifv.mobitopp.populationsynthesis.opportunities.OpportunityLocationSelector;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingDataForZone;
import edu.kit.ifv.mobitopp.simulation.emobility.LimitedChargingDataForZone;

public class ExampleZones {

	private static final Double dummyPoint = new Point2D.Double(0.0d, 0.0d);
	private static final int dummyAccessEdge = -1;
	private static final int parkingPlaces = 2;

	private final Zone someZone;
	private final Zone otherZone;
	private final Zone zoneWithoutLocations;

	public ExampleZones(Zone someZone, Zone otherZone, Zone zoneWithoutLocations) {
		super();
		this.someZone = someZone;
		this.otherZone = otherZone;
		this.zoneWithoutLocations = zoneWithoutLocations;
	}

	public static ExampleZones create() {
		Zone someZone = zoneWithId("1", 0);
		Zone otherZone = zoneWithId("2", 1);
		Zone zoneWithoutLocations = zoneWithAttractivities("3", 2, emptyAttractivities());
		return new ExampleZones(someZone, otherZone, zoneWithoutLocations);
	}

	private static Attractivities emptyAttractivities() {
		Attractivities attractivities = new Attractivities();
		EnumSet
				.allOf(ActivityType.class)
				.forEach(activityType -> attractivities.addAttractivity(activityType, 0));
		return attractivities;
	}

	public static Zone zoneWithId(String id, int oid) {
		Attractivities attractivities = new Attractivities();
		attractivities.addAttractivity(ActivityType.WORK, 1);
		Zone zone = zoneWithAttractivities(id, oid, attractivities);
		return zone;
	}

  public static Zone zoneWithAttractivities(String id, int oid, Attractivities attractivities) {
    ZoneId zoneId = new ZoneId(id, oid);
    ZoneProperties zoneProperties = buildProperties(id);
    ChargingDataForZone charging = createChargingData();
    Zone zone = new Zone(zoneId, zoneProperties, attractivities, charging);
    zone
        .opportunities()
        .createLocations(centroidAsLocation(attractivities, zoneProperties.getCentroidLocation()));
    return zone;
  }

  protected static ZoneProperties buildProperties(String id) {
    String name = "zone " + id;
		AreaType areaType = ZoneAreaType.METROPOLITAN;
		RegionType regionType = new DefaultRegionType(1);
		ZoneClassificationType classification = ZoneClassificationType.studyArea;
		Location centroid = new Location(dummyPoint, dummyAccessEdge, 0.0d);
		boolean isDestination = true;
		Map<String, Value> allProperties = new LinkedHashMap<>();
    allProperties.put("NAME", new Value(name));
    allProperties.put("areaType", new Value(String.valueOf(areaType)));
    allProperties.put("regionType", new Value(String.valueOf(regionType)));
    allProperties.put("classification", new Value(String.valueOf(classification)));
    allProperties.put("centroid", new Value(String.valueOf(centroid)));
    allProperties.put("isDestination", new Value(String.valueOf(isDestination)));
    allProperties.put("parkingPlaces", new Value(String.valueOf(parkingPlaces)));
    return ZoneProperties
        .builder()
        .name(name)
        .areaType(areaType)
        .regionType(regionType)
        .classification(classification)
        .parkingPlaces(parkingPlaces)
        .isDestination(isDestination)
        .centroidLocation(centroid)
        .zoneProperties(allProperties)
        .build();
  }

	private static LimitedChargingDataForZone createChargingData() {
		return new LimitedChargingDataForZone(emptyList(), DefaultPower.zero);
	}

	private static OpportunityLocationSelector centroidAsLocation(
			Attractivities attractivities, Location centroid) {
		return (zone, activityType, opportunities) -> {
			Map<ActivityType, Integer> items = attractivities.getItems();
			if (items.containsKey(activityType) && 0 < items.get(activityType)) {
				return Map.of(centroid, opportunities);
			}
			return emptyMap();
		};
	}

	public Zone someZone() {
		return someZone;
	}

	public Zone otherZone() {
		return otherZone;
	}

	public Zone zoneWithoutLocations() {
		return zoneWithoutLocations;
	}

}
