package edu.kit.ifv.mobitopp.data.local.configuration;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;

import edu.kit.ifv.mobitopp.data.Network;
import edu.kit.ifv.mobitopp.data.local.Convert;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.RouteSearch;
import edu.kit.ifv.mobitopp.publictransport.serializer.TimetableFiles;
import edu.kit.ifv.mobitopp.simulation.Hook;
import edu.kit.ifv.mobitopp.simulation.PublicTransport;
import edu.kit.ifv.mobitopp.simulation.PublicTransportData;
import edu.kit.ifv.mobitopp.simulation.SimulationDays;
import edu.kit.ifv.mobitopp.simulation.person.PersonState;
import edu.kit.ifv.mobitopp.simulation.person.PersonStatePublicTransport;
import edu.kit.ifv.mobitopp.simulation.publictransport.PublicTransportTimetable;
import edu.kit.ifv.mobitopp.simulation.publictransport.TimetableVerifier;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;

public class UsePublicTransport extends BasePublicTransport implements PublicTransport {

	private File timetableFolder;
	private RouteSearchAlgorithm routeSearch;
	private Capacity capacity;

	public UsePublicTransport() {
		super();
		routeSearch = new ConnectionScanAlgorithm();
		capacity = Capacity.unlimited;
	}

	public String getTimetableFolder() {
		return Convert.asString(timetableFolder);
	}

	public void setTimetableFolder(String timetableFolder) {
		this.timetableFolder = Convert.asFile(timetableFolder);
	}

	public RouteSearchAlgorithm getRouteSearch() {
		return routeSearch;
	}

	public void setRouteSearch(RouteSearchAlgorithm routeSearch) {
		this.routeSearch = routeSearch;
	}

	public Capacity getCapacity() {
		return capacity;
	}

	public void setCapacity(Capacity capacity) {
		this.capacity = capacity;
	}

	@Override
	public PersonState initialState(PersonState defaultState) {
		return PersonStatePublicTransport.UNINITIALIZED;
	}

	@Override
	public PublicTransportData loadData(Supplier<Network> network, SimulationDays simulationDays,
		TimetableVerifier timetableVerifier) throws IOException {
		VisumNetwork visumNetwork = network.get().visumNetwork;
		PublicTransportTimetable timetable = loadTimetable(visumNetwork, simulationDays, timetableVerifier);
		RouteSearch finder = createFinder(timetable, simulationDays.startDate());
		return new ExistingPublicTransportData(timetable, capacity, finder);
	}

	private RouteSearch createFinder(
			PublicTransportTimetable publicTransport, Time simulationStart) {
		return routeSearch.createRouteScan(publicTransport, simulationStart);
	}

	@Override
	protected TimetableFiles timetableFiles() {
		return TimetableFiles.at(timetableFolder);
	}

	@Override
	public Optional<Hook> cleanCacheHook() {
		return routeSearch.cleanCacheHook();
	}

	@Override
	public String toString() {
		return getClass().getName() + " [timetableFolder=" + timetableFolder + ", routeSearch="
				+ routeSearch + ", capacity=" + capacity + "]";
	}

}
