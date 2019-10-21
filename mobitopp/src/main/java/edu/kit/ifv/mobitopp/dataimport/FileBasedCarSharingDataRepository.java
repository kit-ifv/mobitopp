package edu.kit.ifv.mobitopp.dataimport;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZonePolygon;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingDataForZone;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public class FileBasedCarSharingDataRepository implements CarSharingDataRepository {

	private final SimpleRoadNetwork roadNetwork;
	private final CsvFile carSharingStationData;
	private final IdSequence carSharingCarIds;

	public FileBasedCarSharingDataRepository(
			SimpleRoadNetwork roadNetwork, CsvFile carSharingStationData, IdSequence carSharingCarIds) {
		super();
		assert null != carSharingStationData;
		this.roadNetwork = roadNetwork;
		this.carSharingStationData = carSharingStationData;
		this.carSharingCarIds = carSharingCarIds;
	}

	@Override
	public CarSharingDataForZone getData(VisumZone visumZone, ZonePolygon polygon, Zone zone) {
		return carSharingBuilder().carsharingIn(zone);
	}

	FileBasedCarSharingBuilder carSharingBuilder() {
		return new FileBasedCarSharingBuilder(roadNetwork, carSharingCarIds, carSharingStationData);
	}

}
