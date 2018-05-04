package edu.kit.ifv.mobitopp.simulation;

import java.io.File;

import edu.kit.ifv.mobitopp.data.StartUpConfiguration;

public interface StartUpSimulation extends StartUpConfiguration {

	float fractionOfPopulation();

	int days();

	File visumNetwork();

	PublicTransport publicTransport();

	WrittenConfiguration configuration();
}