package edu.kit.ifv.mobitopp.publictransport.serializer;

import static java.lang.Integer.parseInt;
import static java.time.temporal.ChronoUnit.SECONDS;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.StringTokenizer;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.ConnectionId;
import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;
import edu.kit.ifv.mobitopp.publictransport.model.RoutePoints;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

class CsvConnectionFormat extends CsvFormat implements ConnectionFormat {

	private static final int idIndex = 0;
	private static final int startIndex = 1;
	private static final int endIndex = 2;
	private static final int departureIndex = 3;
	private static final int arrivalIndex = 4;
	private static final int journeyIndex = 5;
	private static final int pointsIndex = 6;
	private static final String pointDelimiter = "#";

	@Override
	public String serialize(Connection connection, Time day) {
		StringBuilder serialized = new StringBuilder();
		serialized.append(connection.id());
		serialized.append(separator);
		serialized.append(connection.start().id());
		serialized.append(separator);
		serialized.append(connection.end().id());
		serialized.append(separator);
		serialized.append(connection.departure().differenceTo(day).seconds());
		serialized.append(separator);
		serialized.append(connection.arrival().differenceTo(day).seconds());
		serialized.append(separator);
		serialized.append(connection.journey().id());
		serialized.append(separator);
		serialized.append(pointsOf(connection));
		return serialized.toString();
	}

	private String pointsOf(Connection connection) {
		RoutePoints points = connection.points();
		StringJoiner joiner = new StringJoiner(pointDelimiter);
		points.forEach(point -> joiner.add(serialized(point)));
		return joiner.toString();
	}

	@Override
	public Connection deserialize(
			String serialized, StopResolver stopResolver, JourneyProvider journeyProvider, Time day) {
		String[] fields = serialized.split(separator);
		ConnectionId id = idOf(fields);
		Stop start = startOf(fields, stopResolver);
		Stop end = endOf(fields, stopResolver);
		Time departure = departureOf(fields, day);
		Time arrival = arrivalOf(fields, day);
		ModifiableJourney journey = journeyOf(fields, journeyProvider);
		RoutePoints routePoints = pointsOf(fields, start, end);
		Connection connection = Connection.from(id, start, end, departure, arrival, journey, routePoints);
		journey.add(connection);
		return connection;
	}

	private ModifiableJourney journeyOf(String[] fields, JourneyProvider journeyProvider) {
		return journeyProvider.get(Integer.parseInt(fields[journeyIndex]));
	}

	private Time arrivalOf(String[] fields, Time day) {
		return day.plus(RelativeTime.of(Integer.parseInt(fields[arrivalIndex]), SECONDS));
	}

	private Time departureOf(String[] fields, Time day) {
		return day.plus(RelativeTime.of(Integer.parseInt(fields[departureIndex]), SECONDS));
	}

	private Stop endOf(String[] fields, StopResolver stopResolver) {
		return stopResolver.resolve(Integer.parseInt(fields[endIndex]));
	}

	private Stop startOf(String[] fields, StopResolver stopResolver) {
		return stopResolver.resolve(Integer.parseInt(fields[startIndex]));
	}

	private ConnectionId idOf(String[] fields) {
		return ConnectionId.of(parseInt(fields[idIndex]));
	}

	private RoutePoints pointsOf(String[] fields, Stop start, Stop end) {
		List<Point2D> points = new ArrayList<>();
		StringTokenizer tokenizer = new StringTokenizer(fields[pointsIndex], pointDelimiter);
		while(tokenizer.hasMoreTokens()) {
			points.add(pointOf(tokenizer.nextToken()));
		}
		return RoutePoints.from(start, end, points);
	}
}
