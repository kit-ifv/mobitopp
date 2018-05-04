package edu.kit.ifv.mobitopp.dataimport;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ifv.mobitopp.data.ZonePolygon;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingDataForZone;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingFacility;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public class ChargingDataBuilder {

	private final IdSequence idSequenz;
	private final ZoneBased zoneBased;
	private final PoiBased poiBased;
	private final ChargingDataFactory factory;

	public ChargingDataBuilder(
			VisumNetwork visumNetwork, ZoneLocationSelector locationSelector, ChargingDataFactory factory,
			DefaultPower defaultPower) {
		super();
		idSequenz = new IdSequence();
		zoneBased = new ZoneBased(visumNetwork, locationSelector, idSequenz, defaultPower);
		poiBased = new PoiBased(visumNetwork, locationSelector, idSequenz);
		this.factory = factory;
	}

	public ChargingDataForZone chargingData(VisumZone visumZone, ZonePolygon polygon) {
		List<ChargingFacility> facilities = new ArrayList<>();
		facilities.addAll(zoneBased(visumZone, polygon));
		facilities.addAll(poiBased(visumZone, polygon));
		return factory.create(facilities);
	}

	private List<ChargingFacility> poiBased(VisumZone visumZone, ZonePolygon polygon) {
		return poiBased.create(visumZone, polygon);
	}

	private List<ChargingFacility> zoneBased(VisumZone visumZone, ZonePolygon polygon) {
		return zoneBased.create(visumZone, polygon);
	}

}
