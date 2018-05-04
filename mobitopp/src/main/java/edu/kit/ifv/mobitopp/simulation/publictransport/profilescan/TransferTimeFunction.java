package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import static edu.kit.ifv.mobitopp.time.Time.future;
import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.time.Time;

public class TransferTimeFunction {

	private final List<TransferEntry> entries;

	public TransferTimeFunction() {
		super();
		entries = new ArrayList<>();
		entries.add(new TransferEntry(future, future));
	}

	public void update(TransferEntry entry) {
		LinkedList<TransferEntry> before = new LinkedList<>();
		TransferEntry earlyEntry = entries.get(0);
		while (earlyEntry.departure().isBefore(entry.departure())) {
			if (earlyEntry.arrival().isBefore(entry.arrival())) {
				before.push(earlyEntry);
			}
			entries.remove(0);
			earlyEntry = entries.get(0);
		}
		if (entry.arrival().isBefore(earlyEntry.arrival())) {
			if (entry.departure().equals(earlyEntry.departure())) {
				entries.set(0, entry);
			} else {
				entries.add(0, entry);
			}
		}
		for (TransferEntry old : before) {
			entries.add(0, old);
		}
	}

	public Optional<Time> arrivalFor(Time departure) {
		for (TransferEntry entry : entries) {
			if (departure.isBeforeOrEqualTo(entry.departure())) {
				return of(entry.arrival());
			}
		}
		return empty();
	}

	@Override
	public String toString() {
		return "TransferTimeFunction [entries=" + entries + "]";
	}

}
