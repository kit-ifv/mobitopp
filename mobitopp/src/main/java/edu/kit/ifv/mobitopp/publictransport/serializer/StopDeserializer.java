package edu.kit.ifv.mobitopp.publictransport.serializer;

import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;

interface StopDeserializer {

	List<String> stops();

	Stop deserializeStop(String serialized, StationResolver stationResolver);

	NeighbourhoodCoupler neighbourhoodCoupler(StopResolver stopResolver);

}