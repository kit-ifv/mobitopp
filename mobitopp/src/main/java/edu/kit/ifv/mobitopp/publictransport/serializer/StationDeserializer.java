package edu.kit.ifv.mobitopp.publictransport.serializer;

import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.model.Station;

interface StationDeserializer {

	Station deserializeStation(String serializedStation, NodeResolver nodeResolver);
	
	List<String> stations();

}