package edu.kit.ifv.mobitopp.publictransport.serializer;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;

interface StopFormat {

	String serialize(Stop stop);

	Stop deserialize(String serialized, StationResolver stationResolver);

	String header();

}
