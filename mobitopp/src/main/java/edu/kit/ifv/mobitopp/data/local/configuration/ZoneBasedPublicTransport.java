package edu.kit.ifv.mobitopp.data.local.configuration;

import java.util.Collection;

import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.result.ResultWriter;
import edu.kit.ifv.mobitopp.simulation.ImpedanceCarSharing;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.PublicTransportData;
import edu.kit.ifv.mobitopp.simulation.VehicleBehaviour;
import edu.kit.ifv.mobitopp.simulation.publictransport.PublicTransportTimetable;
import edu.kit.ifv.mobitopp.simulation.publictransport.matrix.ZonesToStops;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.ProfileBuilder;

public class ZoneBasedPublicTransport implements PublicTransportData {

	private final PublicTransportTimetable timetable;

	public ZoneBasedPublicTransport(PublicTransportTimetable timetable) {
		super();
		this.timetable = timetable;
	}

	@Override
	public VehicleBehaviour vehicleBehaviour(ResultWriter results) {
		return VehicleBehaviour.empty;
	}

	@Override
	public ImpedanceIfc impedance(ImpedanceIfc impedance, ZoneRepository zoneRepository) {
		return publicTransport(new ImpedanceCarSharing(impedance), timetable, zoneRepository);
	}

	private ImpedanceIfc publicTransport(
			ImpedanceCarSharing impedance, PublicTransportTimetable publicTransport,
			ZoneRepository zoneRepository) {
		ZonesToStops zonesToStops = assignStopsToZones(zoneRepository);
		Collection<Connection> connections = publicTransport.createConnections();
		ProfileBuilder builder = ProfileBuilder.from(connections);
		ZoneProfiles zoneProfiles = zonesToStops.calculateProfiles(builder);
		return new ZoneProfileImpedance(impedance, zoneProfiles);
	}

	private ZonesToStops assignStopsToZones(ZoneRepository zoneRepository) {
		ZonesToStops zonesToStops = zonesToStops(zoneRepository);
		Collection<Stop> stops = stops();
		for (Stop stop : stops) {
			zonesToStops.assign(stop);
		}
		return zonesToStops;
	}

	private Collection<Stop> stops() {
		return timetable.stops();
	}

	private ZonesToStops zonesToStops(ZoneRepository zoneRepository) {
		return new ZonesToStops(zoneRepository.getZones());
	}

}
