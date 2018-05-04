package edu.kit.ifv.mobitopp.dataimport;

import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingFacility;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingPower;
import edu.kit.ifv.mobitopp.visum.VisumPoint2;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public class BaseChargingDataBuilder {

	private final ZoneLocationSelector locationSelector;
	private final IdSequence nextId;

	public BaseChargingDataBuilder(ZoneLocationSelector locationSelector, IdSequence nextId) {
		super();
		this.locationSelector = locationSelector;
		this.nextId = nextId;
	}

	protected ChargingFacility createChargingFacility(
			int stationId, Location location, ChargingPower power) {
		int chargingPointId = nextId.nextId();
		return new ChargingFacility(chargingPointId, stationId, location, ChargingFacility.Type.PUBLIC,
				power);
	}

	protected Location makeLocation(VisumZone zone, VisumPoint2 coordinate) {
		return locationSelector.selectLocation(zone, coordinate);
	}

}