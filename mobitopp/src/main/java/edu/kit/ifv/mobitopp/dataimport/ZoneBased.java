package edu.kit.ifv.mobitopp.dataimport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.kit.ifv.mobitopp.data.ZonePolygon;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingFacility;
import edu.kit.ifv.mobitopp.visum.VisumChargingFacility;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.VisumSurface;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public class ZoneBased extends BaseChargingDataBuilder {

	static final int dummyStation = -1;
	private final VisumNetwork visumNetwork;
	private final DefaultPower defaultPower;

	public ZoneBased(
			VisumNetwork visumNetwork, ZoneLocationSelector locationSelector, IdSequence nextId,
			DefaultPower defaultPower) {
		super(locationSelector, nextId);
		this.visumNetwork = visumNetwork;
		this.defaultPower = defaultPower;
	}

	public List<ChargingFacility> create(VisumZone visumZone, ZonePolygon polygon) {
		List<VisumChargingFacility> visumFacilities = facilitiesIn(polygon);
		List<ChargingFacility> existingFacilities = processExistingFacilities(visumZone,
				visumFacilities);
		List<ChargingFacility> andMissingFacilities = fillUpMissingFacilities(visumZone,
				visumFacilities);
		return combine(existingFacilities, andMissingFacilities);
	}

	private List<ChargingFacility> combine(
			List<ChargingFacility> existingFacilities, List<ChargingFacility> missingFacilities) {
		List<ChargingFacility> allFacilities = new ArrayList<>(existingFacilities);
		allFacilities.addAll(missingFacilities);
		return Collections.unmodifiableList(allFacilities);
	}

	private List<VisumChargingFacility> facilitiesIn(ZonePolygon polygon) {
		return chargingFacilitiesWithinZonePolygon(polygon.polygon(),
				visumNetwork.chargingFacilities.values());
	}

	private List<ChargingFacility> processExistingFacilities(
			VisumZone visumZone, List<VisumChargingFacility> visumFacilities) {
		List<ChargingFacility> facilities = new ArrayList<>();
		for (VisumChargingFacility visumChargingFacility : visumFacilities) {
			Location location = makeLocation(visumZone, visumChargingFacility.coord);
			int id = visumChargingFacility.id;
			facilities.add(createChargingFacilityAt(location, id));
		}
		return facilities;
	}

	private List<ChargingFacility> fillUpMissingFacilities(
			VisumZone visumZone, List<VisumChargingFacility> visumFacilities) {
		List<ChargingFacility> facilities = new ArrayList<>();
		Location zoneCenter = makeLocation(visumZone, visumZone.coord);
		for (int i = visumFacilities.size(); i < visumZone.chargingFacilities; i++) {
			ChargingFacility charging = createChargingFacilityAt(zoneCenter,
					dummyStation);
			facilities.add(charging);
		}
		return facilities;
	}

	private List<VisumChargingFacility> chargingFacilitiesWithinZonePolygon(
			VisumSurface polygon, Collection<VisumChargingFacility> chargingFacilities) {
		List<VisumChargingFacility> result = new ArrayList<>();
		for (VisumChargingFacility facility : chargingFacilities) {
			if (polygon.isPointInside(facility.coord.asPoint2D())) {
				result.add(facility);
			}
		}
		return result;
	}

	private ChargingFacility createChargingFacilityAt(Location loc, int stationId) {
		return createChargingFacility(stationId, loc, defaultPower.publicFacility());
	}

}
