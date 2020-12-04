package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import java.util.concurrent.atomic.AtomicInteger;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Profiles {

	private final ProfileBuilder builder;
	private final Store store;
	private final AtomicInteger elements;

	public Profiles(ProfileBuilder builder, Store store) {
		super();
		this.builder = builder;
		this.store = store;
		elements = new AtomicInteger();
	}

	public void buildTo(Stop target) {
		Profile profile = buildProfileTo(target);
		save(profile);
		cleanup();
	}

	private Profile buildProfileTo(Stop target) {
		return builder.buildUpTo(target);
	}

	private void cleanup() {
		if (cleanupIsNecessary()) {
			log.debug("" + elements());
			System.gc();
		}
	}

	private boolean cleanupIsNecessary() {
		return elements() % 100 == 0;
	}

	private int elements() {
		return elements.get();
	}

	private void save(Profile profile) {
		if (null == profile) {
			return;
		}
		elements.incrementAndGet();
		store.save(profile);
	}

}
