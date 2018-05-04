package edu.kit.ifv.mobitopp.simulation.publictransport;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;

public interface Timetable {

	Stop stopFor(int id);
	
	Stop stopByExternal(int id);

	Connection connectionFor(int id);

}
