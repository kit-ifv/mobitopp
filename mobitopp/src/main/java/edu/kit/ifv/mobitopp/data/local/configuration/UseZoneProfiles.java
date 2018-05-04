package edu.kit.ifv.mobitopp.data.local.configuration;

import java.io.File;
import java.util.Optional;

import edu.kit.ifv.mobitopp.data.local.Convert;
import edu.kit.ifv.mobitopp.publictransport.serializer.TimetableFiles;
import edu.kit.ifv.mobitopp.simulation.Hook;
import edu.kit.ifv.mobitopp.simulation.PublicTransport;
import edu.kit.ifv.mobitopp.simulation.PublicTransportData;
import edu.kit.ifv.mobitopp.simulation.SimulationDays;
import edu.kit.ifv.mobitopp.simulation.person.PersonState;
import edu.kit.ifv.mobitopp.simulation.person.PersonStateSimple;
import edu.kit.ifv.mobitopp.simulation.publictransport.PublicTransportTimetable;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;

public class UseZoneProfiles extends BasePublicTransport implements PublicTransport {

	private File timetableFolder;

	public UseZoneProfiles() {
		super();
	}

	public String getTimetableFolder() {
		return Convert.asString(timetableFolder);
	}

	public void setTimetableFolder(String timetableFolder) {
		this.timetableFolder = Convert.asFile(timetableFolder);
	}

	@Override
	public PersonState initialState(PersonState defaultState) {
		return PersonStateSimple.UNINITIALIZED;
	}

	@Override
	public PublicTransportData loadData(VisumNetwork network, SimulationDays simulationDays) {
		PublicTransportTimetable timetable = loadTimetable(network, simulationDays);
		return new ZoneBasedPublicTransport(timetable);
	}

	@Override
	protected TimetableFiles timetableFiles() {
		return TimetableFiles.at(timetableFolder);
	}

	@Override
	public Optional<Hook> cleanCacheHook() {
		return Optional.empty();
	}

	@Override
	public String toString() {
		return getClass().getName() + " [timetableFolder=" + timetableFolder + "]";
	}

}
