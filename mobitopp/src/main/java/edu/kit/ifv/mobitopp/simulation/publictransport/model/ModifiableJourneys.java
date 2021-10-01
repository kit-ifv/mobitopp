package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static edu.kit.ifv.mobitopp.simulation.publictransport.model.NoJourney.noJourney;
import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.publictransport.model.Connections;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;
import edu.kit.ifv.mobitopp.publictransport.serializer.JourneyProvider;
import edu.kit.ifv.mobitopp.publictransport.serializer.JourneySerializer;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.visum.VisumPtVehicleJourney;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModifiableJourneys implements JourneyProvider {

	private static final int minimumCapacity = 1;

	private final HashMap<Integer, ModifiableJourney> journeys;

	public ModifiableJourneys() {
		super();
		journeys = new HashMap<>();
	}

	public Optional<? extends Journey> add(
			VisumPtVehicleJourney visum, JourneyTemplates timeProfiles, PublicTransportFactory factory,
			Time day) {
		JourneyTemplate profile = timeProfiles.from(visum);
		Optional<ModifiableJourney> journey = profile.createJourney(visum, factory, day);
		journey.ifPresent(j -> journeys.put(j.id(), j));
		return journey;
	}

	@Override
	public Stream<ModifiableJourney> stream() {
		return journeys.values().stream();
	}

	public ModifiableJourney get(int journeyId) {
		return journeys.getOrDefault(journeyId, warn(journeyId, "modifiable journey", noJourney, log));
	}

	public void add(ModifiableJourney journey) {
		journeys.put(journey.id(), journey);
	}

	public Connections connections() {
		Connections connections = new Connections();
		for (Journey journey : journeys.values()) {
			connections.addAll(journey.connections());
		}
		return connections;
	}

	public Function<Integer, Float> vehicleScaler() {
		int maximumCapacity = journeys
				.values()
				.stream()
				.mapToInt(Journey::capacity)
				.max()
				.orElse(minimumCapacity);
		return capacity -> capacity / (float) maximumCapacity;
	}

	public void serializeJourneysTo(JourneySerializer serializer) {
		journeys.values().forEach(serializer::serialize);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((journeys == null) ? 0 : journeys.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ModifiableJourneys other = (ModifiableJourneys) obj;
		if (journeys == null) {
			if (other.journeys != null) {
				return false;
			}
		} else if (!journeys.equals(other.journeys)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Journeys [journeys=" + journeys + "]";
	}

}
