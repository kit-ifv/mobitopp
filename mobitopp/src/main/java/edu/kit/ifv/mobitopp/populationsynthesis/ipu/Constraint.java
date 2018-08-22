package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.List;

public interface Constraint {

	List<Household> update(List<Household> households);

}