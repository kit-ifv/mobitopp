package edu.kit.ifv.mobitopp.publictransport.serializer;

import static java.time.temporal.ChronoUnit.SECONDS;

import java.awt.geom.Point2D;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.kit.ifv.mobitopp.publictransport.model.Station;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.RelativeTime;

public class CsvStopFormat extends CsvFormat implements StopFormat {

	static final String quotationMark = "\"";
	private static final int quoteLength = quotationMark.length();
	private static final int idGroup = 1;
	private static final int changeTimeGroup = 2;
	private static final int coordinateGroup = 3;
	private static final int nameGroup = 4;
	private static final int externalIdGroup = 5;
	private static final int stationGroup = 6;
	private static final Pattern pattern = Pattern
			.compile("^(\\d+);(\\d+);([\\-0-9" + coordinateSeparator + "\\.]*);(.*);(\\d+);(\\d+)$");

	@Override
	public String header() {
		return "id;change_time;longitude;latitude;name;visum_id;station_id";
	}

	@Override
	public String serialize(Stop stop) {
		StringBuilder serialized = new StringBuilder();
		Point2D coordinate = stop.coordinate();
		serialized.append(stop.id());
		serialized.append(separator);
		serialized.append(asSeconds(stop.changeTime()));
		serialized.append(separator);
		serialized.append(serialized(coordinate));
		serialized.append(separator);
		serialized.append(quotationMark);
		serialized.append(stop.name());
		serialized.append(quotationMark);
		serialized.append(separator);
		serialized.append(stop.externalId());
		serialized.append(separator);
		serialized.append(stop.station().id());
		return serialized.toString();
	}

	@Override
	public Stop deserialize(String serialized, StationResolver stationResolver) {
		Matcher matcher = pattern.matcher(serialized);
		if (matcher.matches()) {
			int id = idOf(matcher);
			String name = nameOf(matcher);
			Point2D coordinate = coordinateOf(matcher);
			RelativeTime minimumChangeTime = changeTimeOf(matcher);
			Station station = stationOf(matcher, stationResolver);
			int externalId = externalIdOf(matcher);
			Stop stop = new Stop(id, name, coordinate, minimumChangeTime, station, externalId);
			station.add(stop);
			return stop;
		}
		throw new IllegalArgumentException("Stop is not correct serialized: " + serialized);
	}

	private Station stationOf(Matcher matcher, StationResolver stationResolver) {
		int stationId = Integer.parseInt(matcher.group(stationGroup));
		return stationResolver.getStation(stationId);
	}

	private static RelativeTime changeTimeOf(Matcher matcher) {
		Long seconds = Long.parseLong(matcher.group(changeTimeGroup));
		return RelativeTime.of(seconds, SECONDS);
	}

	private Point2D coordinateOf(Matcher matcher) {
		return pointOf(matcher.group(coordinateGroup));
	}

	private static String nameOf(Matcher matcher) {
		return unquote(matcher.group(nameGroup));
	}

	private static String unquote(String quoted) {
		return quoted.substring(quoteLength, quoted.length() - quoteLength);
	}

	private static int idOf(Matcher matcher) {
		return Integer.parseInt(matcher.group(idGroup));
	}

	private static int externalIdOf(Matcher matcher) {
		return Integer.parseInt(matcher.group(externalIdGroup));
	}

}
