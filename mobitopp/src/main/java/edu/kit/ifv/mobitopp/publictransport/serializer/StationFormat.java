package edu.kit.ifv.mobitopp.publictransport.serializer;

import edu.kit.ifv.mobitopp.publictransport.model.Station;

interface StationFormat {

	Station deserialize(String serializedStation, NodeResolver nodeResolver);

	String serialize(Station station);

}
