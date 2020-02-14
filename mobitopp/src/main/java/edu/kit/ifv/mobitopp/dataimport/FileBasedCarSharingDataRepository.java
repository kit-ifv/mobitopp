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
	private final CsvFile properties;
	private final CsvFile stationData;
	private final CsvFile freeFloatingData;
	private final IdSequence carSharingCarIds;

	public FileBasedCarSharingDataRepository(
			SimpleRoadNetwork roadNetwork, CsvFile properties, CsvFile stationData,
			CsvFile freeFloatingData, IdSequence carSharingCarIds) {
		super();
		assert null != stationData;
		this.roadNetwork = roadNetwork;
		this.stationData = stationData;
		this.properties = properties;
		this.freeFloatingData = freeFloatingData;
		this.carSharingCarIds = carSharingCarIds;
	}

	@Override
	public CarSharingDataForZone getData(VisumZone visumZone, ZonePolygon polygon, Zone zone) {
		return carSharingBuilder().carsharingIn(zone);
	}

	FileBasedCarSharingBuilder carSharingBuilder() {
		return new FileBasedCarSharingBuilder(roadNetwork, carSharingCarIds, properties, stationData,
				freeFloatingData);
	}

}
