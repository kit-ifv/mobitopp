package edu.kit.ifv.mobitopp.data.local.configuration;

import java.io.File;
import java.util.Optional;

import edu.kit.ifv.mobitopp.data.local.Convert;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.RouteSearch;
import edu.kit.ifv.mobitopp.simulation.CachedStore;
import edu.kit.ifv.mobitopp.simulation.Hook;
import edu.kit.ifv.mobitopp.simulation.publictransport.PublicTransportTimetable;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.EntrySplitter;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.Split;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.Store;
import edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.StoreIn;
import edu.kit.ifv.mobitopp.time.Time;

public class ProfileScanAlgorithm implements RouteSearchAlgorithm {

	private File profileFolder;
	private Hook cleanCache;

	public ProfileScanAlgorithm() {
		super();
	}

	public String getProfileFolder() {
		return Convert.asString(profileFolder);
	}

	public void setProfileFolder(String profileFolder) {
		this.profileFolder = Convert.asFile(profileFolder);
	}

	public RouteSearch createRouteScan(
			PublicTransportTimetable publicTransport, Time simulationStart) {
		EntrySplitter splitter = Split.hourly(simulationStart);
		Store storedProfiles = StoreIn.folder(profileFolder, publicTransport, splitter);
		CachedStore cachedProfiles = new CachedStore(storedProfiles);
		cleanCache = currentTime -> cachedProfiles.cleanBefore(currentTime);
		if (profilesAreMissing()) {
			return publicTransport.createProfileScan(cachedProfiles);
		}
		return publicTransport.loadProfileScan(cachedProfiles);
	}

	private boolean profilesAreMissing() {
		return 0 == profileFolder.listFiles().length;
	}

	@Override
	public Optional<Hook> cleanCacheHook() {
		return Optional.of(cleanCache);
	}

}
