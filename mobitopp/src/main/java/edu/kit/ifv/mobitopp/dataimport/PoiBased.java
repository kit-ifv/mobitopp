package edu.kit.ifv.mobitopp.dataimport;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.kit.ifv.mobitopp.data.ZonePolygon;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingFacility;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingPower;
import edu.kit.ifv.mobitopp.visum.VisumChargingPoint;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.VisumSurface;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public class PoiBased extends BaseChargingDataBuilder {

	private final VisumNetwork visumNetwork;

	public PoiBased(VisumNetwork visumNetwork, ZoneLocationSelector locationSelector, IdSequence nextId) {
		super(locationSelector, nextId);
		this.visumNetwork = visumNetwork;
	}

	public List<ChargingFacility> create(VisumZone visumZone, ZonePolygon polygon) {
		List<ChargingFacility> facilities = new ArrayList<>();
		List<VisumChargingPoint> visumPoints = chargingPointsWithinZonePolygon(polygon.polygon(),
				visumNetwork.chargingPoints.values());
		for (VisumChargingPoint visumChargingPoint : visumPoints) {
			Location location = makeLocation(visumZone, visumChargingPoint.coord);
			int id = visumChargingPoint.stationId;
			ChargingPower power = ChargingPower.fromKw(visumChargingPoint.power);
			facilities.add(createChargingFacility(id, location, power));
		}
		return facilities;
	}

	private List<VisumChargingPoint> chargingPointsWithinZonePolygon(
			VisumSurface polygon, Collection<VisumChargingPoint> chargingPoints) {
		List<VisumChargingPoint> result = new ArrayList<>();
		for (VisumChargingPoint point : chargingPoints) {
			Point2D location = point.coord.asPoint2D();
			if (polygon.isPointInside(location)) {
				result.add(point);
			}
		}
		return result;
	}

}
