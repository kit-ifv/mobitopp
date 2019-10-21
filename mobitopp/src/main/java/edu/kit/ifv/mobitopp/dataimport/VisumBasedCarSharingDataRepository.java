package edu.kit.ifv.mobitopp.dataimport;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZonePolygon;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingDataForZone;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public class VisumBasedCarSharingDataRepository implements CarSharingDataRepository {

	private final VisumNetwork visumNetwork;
	private final SimpleRoadNetwork roadNetwork;
	private final IdSequence carSharingCarIds;

	public VisumBasedCarSharingDataRepository(
			VisumNetwork visumNetwork, SimpleRoadNetwork roadNetwork, IdSequence carSharingCarIds) {
		super();
		this.visumNetwork = visumNetwork;
		this.roadNetwork = roadNetwork;
		this.carSharingCarIds = carSharingCarIds;
	}

	@Override
	public CarSharingDataForZone getData(VisumZone visumZone, ZonePolygon polygon, Zone zone) {
		return carSharingBuilder().carsharingIn(visumZone, polygon, zone);
	}

	CarSharingBuilder carSharingBuilder() {
		return new CarSharingBuilder(visumNetwork, roadNetwork, carSharingCarIds);
	}

}
