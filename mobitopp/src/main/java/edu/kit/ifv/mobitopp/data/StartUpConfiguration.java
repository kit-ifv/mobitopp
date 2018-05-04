package edu.kit.ifv.mobitopp.data;

import java.io.PrintStream;

public interface StartUpConfiguration {

	long seed();

	int numberOfZones();

	void printStartupInformationOn(PrintStream out);

}