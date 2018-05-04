package edu.kit.ifv.mobitopp.simulation.publictransport;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumNetwork;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumNode;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertThat;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.kit.ifv.mobitopp.network.Node;
import edu.kit.ifv.mobitopp.routing.VisumLinkFactory;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumLinkBuilder;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.visum.VisumLink;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.VisumNode;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystem;

public class DijkstraSearchTest {

	private static final int secondsInMinute = 60;
	private static final int minuteToSecond = 60;
	private static final int hourToMinute = 60;
	private static final int walkSpeed = 4;
	private static final int allTargets = Integer.MAX_VALUE;
	private static VisumTransportSystem notWalking;
	private static VisumTransportSystem walking;

	@BeforeClass
	public static void initialiseTransportSystems() {
		walking = new VisumTransportSystem("walking", "walking", "walking");
		notWalking = new VisumTransportSystem("not walking", "not walking", "not walking");
	}

	@Test
	public void doesNotFindPathInEmptyNetwork() throws Exception {
		VisumNode someNode = visumNode().build();
		VisumNode anotherNode = visumNode().build();

		ShortestPathSearch dijkstra = searchIn(emptyNetwork());
		ShortestPathsToStations result = dijkstra.search(someNode, asList(anotherNode));

		Optional<RelativeTime> walkTime = result.durationTo(anotherNode);
		assertThat(walkTime, isEmpty());
	}

	private VisumNetwork emptyNetwork() {
		return visumNetwork().build();
	}

	@Test
	public void findsPathInNetworkWithOneLink() throws Exception {
		VisumNode someNode = visumNode().withId(1).build();
		VisumNode anotherNode = visumNode().withId(2).build();
		float fourKilometers = 4f;
		VisumLink oneLink = visumLink()
				.from(someNode)
				.to(anotherNode)
				.withLength(fourKilometers)
				.build();
		VisumNetwork oneLinkNetwork = networkContaining(oneLink, someNode, anotherNode);

		ShortestPathSearch dijkstra = searchIn(oneLinkNetwork);
		ShortestPathsToStations result = dijkstra.search(someNode, asList(anotherNode));

		Optional<RelativeTime> walkTime = result.durationTo(anotherNode);

		assertThat(walkTime, isPresent());
		int hours = hoursTraveling(fourKilometers);
		assertThat(walkTime, hasValue(RelativeTime.of(hours, HOURS)));
	}

	@Test
	public void findsPathInNetworkWithFifteenMinutesWalkTime() throws Exception {
		VisumNode someNode = visumNode().withId(1).build();
		VisumNode anotherNode = visumNode().withId(2).build();
		float oneKilomenter = 1f;
		VisumLink oneLink = visumLink()
				.from(someNode)
				.to(anotherNode)
				.withLength(oneKilomenter)
				.build();
		VisumNetwork oneLinkNetwork = networkContaining(oneLink, someNode, anotherNode);

		ShortestPathSearch dijkstra = searchIn(oneLinkNetwork);
		ShortestPathsToStations result = dijkstra.search(someNode, asList(anotherNode));

		Optional<RelativeTime> walkTime = result.durationTo(anotherNode);

		assertThat(walkTime, isPresent());
		int duration = minutesTraveling(oneKilomenter);
		assertThat(walkTime, hasValue(RelativeTime.of(duration, MINUTES)));
	}

	@Test
	public void findsPathInNetworkWithMinutesAndSecondsWalkTime() throws Exception {
		VisumNode someNode = visumNode().withId(1).build();
		VisumNode anotherNode = visumNode().withId(2).build();
		float halfKilometer = 0.5f;
		VisumLink oneLink = visumLink()
				.from(someNode)
				.to(anotherNode)
				.withLength(halfKilometer)
				.build();
		VisumNetwork oneLinkNetwork = networkContaining(oneLink, someNode, anotherNode);

		ShortestPathSearch dijkstra = searchIn(oneLinkNetwork);
		ShortestPathsToStations result = dijkstra.search(someNode, asList(anotherNode));

		Optional<RelativeTime> walkTime = result.durationTo(anotherNode);

		assertThat(walkTime, isPresent());
		int minutes = minutesTraveling(halfKilometer);
		int seconds = secondsTraveling(halfKilometer);
		assertThat(walkTime, hasValue(RelativeTime.of(minutes, MINUTES).plus(seconds, SECONDS)));
	}

	@Test
	public void findsPathInNetworkWithTwoLinks() throws Exception {
		VisumNode someNode = visumNode().withId(1).build();
		VisumNode otherNode = visumNode().withId(2).build();
		VisumNode anotherNode = visumNode().withId(3).build();
		int fourKilometers = 4;
		VisumLink oneLink = visumLink()
				.withId(1)
				.from(someNode)
				.to(otherNode)
				.withLength(fourKilometers)
				.build();
		VisumLink anotherLink = visumLink()
				.withId(2)
				.from(otherNode)
				.to(anotherNode)
				.withLength(fourKilometers)
				.build();
		VisumNetwork twoLinkNetwork = networkContaining(oneLink, anotherLink, someNode, otherNode,
				anotherNode);

		ShortestPathSearch dijkstra = searchIn(twoLinkNetwork);
		ShortestPathsToStations result = dijkstra.search(someNode, asList(otherNode, anotherNode));

		Optional<RelativeTime> walkTimeToOther = result.durationTo(otherNode);
		Optional<RelativeTime> walkTimeToAnother = result.durationTo(anotherNode);
		int hoursToOther = hoursTraveling(fourKilometers);
		assertThat(walkTimeToOther, isPresent());
		assertThat(walkTimeToOther, hasValue(RelativeTime.of(hoursToOther, HOURS)));
		float eightKilometers = 8f;
		int hoursToAnother = hoursTraveling(eightKilometers);
		assertThat(walkTimeToAnother, isPresent());
		assertThat(walkTimeToAnother, hasValue(RelativeTime.of(hoursToAnother, HOURS)));
	}

	@Test
	public void doesNotFindPathInNetworkWithNoWalkingLink() throws Exception {
		VisumNode someNode = visumNode().withId(1).build();
		VisumNode anotherNode = visumNode().withId(2).build();
		float oneKilomenter = 1f;
		VisumLink oneLink = VisumBuilder
				.visumLink()
				.with(notWalking)
				.from(someNode)
				.to(anotherNode)
				.withLength(oneKilomenter)
				.withSpeed(walkSpeed)
				.build();
		VisumNetwork oneLinkNetwork = networkContaining(oneLink, someNode, anotherNode);

		ShortestPathSearch dijkstra = searchIn(oneLinkNetwork);
		ShortestPathsToStations result = dijkstra.search(someNode, asList(anotherNode));

		Optional<RelativeTime> walkTime = result.durationTo(anotherNode);

		assertThat(walkTime, isEmpty());
	}

	@Test
	public void doesNotFindPathWhenDistanceIsLongerThanGivenMaximumDistance() throws Exception {
		VisumNode someNode = visumNode().withId(1).build();
		VisumNode anotherNode = visumNode().withId(2).build();
		float fourKilometers = 4f;
		VisumLink oneLink = visumLink()
				.from(someNode)
				.to(anotherNode)
				.withLength(fourKilometers)
				.build();
		VisumNetwork oneLinkNetwork = networkContaining(oneLink, someNode, anotherNode);

		RelativeTime maximumDuration = RelativeTime.of(15, MINUTES);
		ShortestPathSearch dijkstra = searchIn(oneLinkNetwork, maximumDuration);
		ShortestPathsToStations result = dijkstra.search(someNode, asList(anotherNode));

		Optional<RelativeTime> walkTime = result.durationTo(anotherNode);

		assertThat(walkTime, isEmpty());
	}

	@Test
	public void findsPathFromLocationToCollectionOfNodes() throws Exception {
		VisumNode someNode = visumNode().withId(1).build();
		VisumNode anotherNode = visumNode().withId(2).build();
		Collection<Node> nodes = asList(someNode);
		float fourKilometers = 4f;
		VisumLink singleLink = visumLink()
				.withId(1)
				.from(anotherNode)
				.to(someNode)
				.withLength(fourKilometers)
				.build();
		VisumNetwork linkToStation = networkContaining(singleLink, someNode, anotherNode);
		ShortestPathSearch search = searchIn(linkToStation, nodes);

		Location location = new Location(new Point2D.Double(), 1, 0.0);
		ShortestPathsToStations result = search.search(location, nodes);
		Optional<RelativeTime> walkTime = result.durationTo(someNode);

		assertThat(walkTime, isPresent());
		assertThat(walkTime, hasValue(RelativeTime.of(1, HOURS)));
	}

	private VisumLinkBuilder visumLink() {
		return VisumBuilder.visumLink().withSpeed(walkSpeed).with(walking);
	}

	private static VisumNetwork networkContaining(
			VisumLink oneLink, VisumNode someNode, VisumNode anotherNode) {
		return visumNetwork().with(oneLink).with(someNode).with(anotherNode).build();
	}

	private static VisumNetwork networkContaining(
			VisumLink oneLink, VisumLink anotherLink, VisumNode someNode, VisumNode otherNode,
			VisumNode anotherNode) {
		return visumNetwork()
				.with(oneLink)
				.with(anotherLink)
				.with(someNode)
				.with(otherNode)
				.with(anotherNode)
				.build();
	}

	private static ShortestPathSearch searchIn(VisumNetwork network) {
		return DijkstraSearch.from(network, linkFactory(), asList(walking));
	}

	private static ShortestPathSearch searchIn(VisumNetwork network, Collection<Node> targets) {
		return DijkstraSearch.from(network, linkFactory(), asList(walking), allTargets, targets);
	}

	private static ShortestPathSearch searchIn(VisumNetwork network, RelativeTime maximumDuration) {
		return DijkstraSearch.from(network, linkFactory(), maximumDuration, asList(walking));
	}

	private static VisumLinkFactory linkFactory() {
		return new VisumWalkLinkFactory();
	}

	private static int secondsTraveling(float length) {
		return (int) (travelTimeInMinutes(length) * minuteToSecond % secondsInMinute);
	}

	private static int minutesTraveling(float length) {
		return (int) travelTimeInMinutes(length);
	}

	private static int hoursTraveling(float length) {
		return (int) travelTime(length);
	}

	private static float travelTimeInMinutes(float length) {
		return travelTime(length) * hourToMinute;
	}

	private static float travelTime(float length) {
		return length / walkSpeed;
	}

}
