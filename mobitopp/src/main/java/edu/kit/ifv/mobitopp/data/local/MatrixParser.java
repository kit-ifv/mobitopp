package edu.kit.ifv.mobitopp.data.local;

import java.io.IOException;

import edu.kit.ifv.mobitopp.data.CostMatrix;
import edu.kit.ifv.mobitopp.data.FixedDistributionMatrix;
import edu.kit.ifv.mobitopp.data.FloatMatrix;
import edu.kit.ifv.mobitopp.data.TravelTimeMatrix;

public interface MatrixParser {

	FloatMatrix parseMatrix() throws IOException;
	
	CostMatrix parseCostMatrix() throws IOException;

	TravelTimeMatrix parseTravelTimeMatrix() throws IOException;

	FixedDistributionMatrix parseFixedDistributionMatrix() throws IOException;

}