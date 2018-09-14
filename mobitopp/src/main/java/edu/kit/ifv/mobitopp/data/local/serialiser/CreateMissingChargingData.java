package edu.kit.ifv.mobitopp.data.local.serialiser;

import static java.util.Collections.emptyList;

import java.util.HashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.dataimport.ChargingDataFactory;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingDataForZone;

public class CreateMissingChargingData implements ChargingDataResolver {

	private final Map<Integer, ChargingDataForZone> mapping;
	private final ChargingDataFactory factory;

	public CreateMissingChargingData(
			Map<Integer, ChargingDataForZone> mapping, ChargingDataFactory factory) {
		super();
		this.mapping = new HashMap<>(mapping);
		this.factory = factory;
	}

	@Override
	public ChargingDataForZone chargingDataFor(int oid) {
		ensureExistingData(oid);
		return mapping.get(oid);
	}

	private void ensureExistingData(int oid) {
		mapping.computeIfAbsent(oid, id -> factory.create(emptyList()));
	}

}
