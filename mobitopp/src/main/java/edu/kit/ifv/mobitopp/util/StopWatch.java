package edu.kit.ifv.mobitopp.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class StopWatch {

	static final String overall = "overall";
  private final Supplier<LocalDateTime> timer;
	private final LinkedHashMap<String, LocalDateTime> points;
	private LocalDateTime start;

	public StopWatch(Supplier<LocalDateTime> timer) {
		super();
		this.timer = timer;
		points = new LinkedHashMap<>();
	}

	public void measurePoint(String label) {
		verifyStarted();
		points.put(label, currentTime());
	}

	private void verifyStarted() {
		if (null == start) {
			throw new IllegalStateException("Stop watch has not been started!");
		}
	}

	private LocalDateTime currentTime() {
		return timer.get();
	}

	public void forEach(BiConsumer<String, Duration> consumer) {
		LocalDateTime last = start;
		for (Entry<String, LocalDateTime> current : points.entrySet()) {
			Duration runtime = Duration.between(last, current.getValue());
			String label = current.getKey();
			consumer.accept(label, runtime);
			last = current.getValue();
		}
		consumer.accept(overall, Duration.between(start, last));
	}

	public void start() {
		start = currentTime();
	}

}
