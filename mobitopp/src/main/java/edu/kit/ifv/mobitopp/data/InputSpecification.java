package edu.kit.ifv.mobitopp.data;

import java.util.List;

import edu.kit.ifv.mobitopp.time.Time;

public interface InputSpecification extends StartDateSpecification {

	List<Time> simulationDates();

}