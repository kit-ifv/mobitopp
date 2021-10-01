package edu.kit.ifv.mobitopp.simulation.publictransport;

import java.io.IOException;

public interface TimetableVerifier {

	void verify(PublicTransportTimetable timetable) throws IOException;

	static TimetableVerifier none() {
		return timetable -> {};
	}

}
