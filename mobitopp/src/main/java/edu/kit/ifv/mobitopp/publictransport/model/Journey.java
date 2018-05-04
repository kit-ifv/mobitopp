package edu.kit.ifv.mobitopp.publictransport.model;

import edu.kit.ifv.mobitopp.time.Time;

public interface Journey {

	int id();

	Time day();

	Connections connections();

	int capacity();

	TransportSystem transportSystem();

}