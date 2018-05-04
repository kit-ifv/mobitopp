package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

public class ArrivalTimeFunction {

	private final List<FunctionEntry> entries;

	public ArrivalTimeFunction() {
		super();
		entries = new ArrayList<>();
	}

	private FunctionEntry earliestEntry() {
		return entries.get(0);
	}

	public boolean update(FunctionEntry newEntry) {
		if (entries.isEmpty()) {
			addAsEarliest(newEntry);
			return true;
		}
		if (willImprove(newEntry)) {
			updateEarliestEntry(newEntry);
			return true;
		}
		return false;
	}

	private boolean willImprove(FunctionEntry newEntry) {
		return hasLaterArrivalThan(newEntry.arrivalAtTarget()) || hasEarlierDepartureThan(newEntry);
	}

	boolean hasLaterArrivalThan(Time arrivalAtTarget) {
		if (entries.isEmpty()) {
			return true;
		}
		return arrivalAtTarget.isBefore(earliestEntry().arrivalAtTarget());
	}

	private boolean hasEarlierDepartureThan(FunctionEntry newEntry) {
		return earliestEntry().departure().isBefore(newEntry.departure());
	}

	private void updateEarliestEntry(FunctionEntry newEntry) {
		if (newEntry.departure().equals(earliestEntry().departure())) {
			setAsEarliestEntry(newEntry);
			return;
		}
		addAsEarliest(newEntry);
	}

	private void setAsEarliestEntry(FunctionEntry newEntry) {
		entries.set(0, newEntry);
	}

	private void addAsEarliest(FunctionEntry newEntry) {
		entries.add(0, newEntry);
	}

	public Optional<Time> arrivalFor(Time departure) {
		if (noEntryMatches(departure)) {
			return Optional.empty();
		}
		FunctionEntry entry = findEntryFor(departure);
		return Optional.of(entry.arrivalAtTarget());
	}

	private boolean noEntryMatches(Time departure) {
		return entries.isEmpty() || departure.isAfter(latestEntry().departure());
	}

	private FunctionEntry latestEntry() {
		return entries.get(entries.size() - 1);
	}

	private FunctionEntry findEntryFor(Time departure) {
		for (FunctionEntry profileEntry : entries) {
			if (departure.isBeforeOrEqualTo(profileEntry.departure())) {
				return profileEntry;
			}
		}
		return latestEntry();
	}

	ArrivalTimeFunction removeChangeTimeAt(Stop start) {
		ArrivalTimeFunction clearedChangeTimes = new ArrivalTimeFunction();
		for (FunctionEntry entry : entries) {
			FunctionEntry entryWithoutChangeTime = entry.clearChangeTimeAt(start);
			clearedChangeTimes.entries.add(entryWithoutChangeTime);
		}
		return clearedChangeTimes;
	}

	public Optional<Connection> connectionFor(Time departure) {
		if (noEntryMatches(departure)) {
			return Optional.empty();
		}
		return Optional.of(findEntryFor(departure).connection());
	}

	public void forEach(ProfileWriter writer) {
		for (FunctionEntry entry : entries) {
			writer.write(entry);
		}
	}

	public int size() {
		return entries.size();
	}
	
	public void addDeserialized(FunctionEntry entry) {
		entries.add(entry);
	}
	
	public List<ArrivalTimeFunction> split(EntrySplitter splitter) {
		List<ArrivalTimeFunction> parts = new ArrayList<>();
		for (EntryAcceptor entryAcceptor : splitter.parts()) {
			parts.add(functionFor(entryAcceptor));
		}
		return parts;
	}

	private ArrivalTimeFunction functionFor(EntryAcceptor acceptor) {
		ArrivalTimeFunction part = new ArrivalTimeFunction();
		for (FunctionEntry entry : entries) {
			if (acceptor.isTooLate(entry)) {
				part.entries.add(entry);
				break;
			}
			boolean accept = acceptor.accept(entry);
			if (accept) {
				part.entries.add(entry);
			}
		}
		return part;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entries == null) ? 0 : entries.hashCode());
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
		ArrivalTimeFunction other = (ArrivalTimeFunction) obj;
		if (entries == null) {
			if (other.entries != null) {
				return false;
			}
		} else if (!entries.equals(other.entries)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Profile [entries=" + entries + "]";
	}

}
