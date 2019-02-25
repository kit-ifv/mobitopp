package edu.kit.ifv.mobitopp.data.local;

import edu.kit.ifv.mobitopp.data.local.configuration.TravelTimeMatrixType;
import edu.kit.ifv.mobitopp.simulation.Mode;

@FunctionalInterface
public interface TypeMapping {

  TravelTimeMatrixType resolve(Mode mode);

}