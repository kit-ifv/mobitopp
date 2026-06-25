package edu.kit.ifv.domain.shared.location

import com.graphhopper.GHRequest
import com.graphhopper.GHResponse
import com.graphhopper.GraphHopper
import com.graphhopper.config.CHProfile
import com.graphhopper.config.Profile
import com.graphhopper.json.Statement
import com.graphhopper.storage.BaseGraph
import com.graphhopper.util.CustomModel
import com.graphhopper.util.TurnCostsConfig
import com.graphhopper.util.shapes.GHPoint
import domain.LinkInfo
import domain.NetworkElements
import domain.NodeId
import domain.VisumNode
import edu.kit.ifv.NetfileParser
import edu.kit.ifv.VisumLocale
import edu.kit.ifv.units.Hemisphere
import edu.kit.ifv.units.asMinutes
import edu.kit.ifv.units.asSeconds
import org.apache.commons.math3.ml.neuralnet.Network
import org.geotools.api.geometry.BoundingBox
import org.jgrapht.Graph
import org.jgrapht.GraphPath
import org.jgrapht.Graphs
import org.jgrapht.alg.shortestpath.BidirectionalDijkstraShortestPath
import org.jgrapht.alg.shortestpath.ContractionHierarchyBidirectionalDijkstra
import org.jgrapht.alg.shortestpath.ContractionHierarchyPrecomputation
import org.jgrapht.graph.SimpleDirectedWeightedGraph
import org.jgrapht.util.ConcurrencyUtil
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.MultiPoint
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel
import java.nio.file.Path
import java.util.concurrent.ThreadPoolExecutor
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.random.Random
import kotlin.system.measureNanoTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTimedValue

fun interface ActualLocationMetric<R> {
    fun evaluate(origin: Location<*>, destination: Location<*>): R
}


class GraphRouter<E>(originalGraph: Graph<VisumNode, E>): ActualLocationMetric<Duration>, AutoCloseable {

    val executor: ThreadPoolExecutor  =
        ConcurrencyUtil.createThreadPoolExecutor(Runtime.getRuntime().availableProcessors())
    private val chGraph = ContractionHierarchyPrecomputation(originalGraph, executor)
    private val hierarchy = run {
        val executor = ConcurrencyUtil.createThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
        )

        try {
            ContractionHierarchyPrecomputation(originalGraph, executor)
                .computeContractionHierarchy()
        } finally {
            executor.shutdown()
        }
    }
    private val router = ContractionHierarchyBidirectionalDijkstra(hierarchy)

    override fun evaluate(
        origin: Location<*>,
        destination: Location<*>,
    ): Duration {
        TODO()
//        router.getPath(origin.position, destination.position)
    }

    fun route(origin: VisumNode, destination: VisumNode): GraphPath<VisumNode, E> {
        return router.getPath(origin, destination)
    }

    fun travelTime(original: VisumNode, destination: VisumNode): Duration {
        return router.getPathWeight(original, destination).seconds
    }
    override fun close() {
        executor.shutdownNow()
    }
}

fun GraphPath<VisumNode, *>.toMultipoint(): MultiPoint {
    val srid = vertexList.first().coordinate.srid
    return GeometryFactory(PrecisionModel(), srid).createMultiPoint(vertexList.map{it.coordinate }.toTypedArray())
}


fun <V, E> Graph<V, E>.getUV(edge: E): Pair<V, V> {
    return getEdgeSource(edge) to getEdgeTarget(edge)
}

val LinkInfo.freeTravelTime get() = length / freeSpeed

fun initialize(osmFilee: Path): GraphHopper {
    val carModel = CustomModel().apply {
        distanceInfluence = 0.0

        speed.add(
            Statement.If(
                "true",
                Statement.Op.LIMIT,
                "car_average_speed",
            ),
        )

        priority.add(
            Statement.If(
                "road_access == DESTINATION",
                Statement.Op.MULTIPLY,
                "0",
            ),
        )
    }
    val g = GraphHopper().apply {
        osmFile = osmFilee.absolutePathString()
        setEncodedValuesString("road_access,car_average_speed")
        graphHopperLocation = "out/hoppercache"
        setProfiles(
            Profile("car").setWeighting("custom").setCustomModel(carModel).setTurnCostsConfig(TurnCostsConfig.car())
        )
        chPreparationHandler.setCHProfiles(CHProfile("car"))

        importOrLoad()
    }
    return g
}

class GraphHopperRouter(
    osmPbf: Path,
    graphCache: Path,
) : AutoCloseable {

    private val hopper: GraphHopper = GraphHopper().apply {
        osmFile = osmPbf.toString()
        graphHopperLocation = graphCache.toString()

        setProfiles(
            Profile("car")
                .setWeighting("fastest")
        )

        chPreparationHandler.setCHProfiles(
            CHProfile("car"),
        )

        importOrLoad()
    }

    fun travelTime(
        fromLat: Double,
        fromLon: Double,
        toLat: Double,
        toLon: Double,
    ): Duration {
        val response = route(fromLat, fromLon, toLat, toLon)
        return (response.best.time / 1000.0).seconds
    }

    fun route(
        fromLat: Double,
        fromLon: Double,
        toLat: Double,
        toLon: Double,
    ): GHResponse {
        val request = GHRequest(
            GHPoint(fromLat, fromLon),
            GHPoint(toLat, toLon),
        ).apply {
            profile = "car"

            // Explicitly keep CH enabled.
            hints.putObject("ch.disable", false)

        }

        val response = hopper.route(request)

        if (response.hasErrors()) {
            throw IllegalStateException(
                response.errors.joinToString("\n") { it.toString() },
            )
        }

        return response
    }

    override fun close() {
        hopper.close()
    }
}

fun createRequest(origin: Point, destination: Point): GHRequest {
    return GHRequest(origin.x, origin.y, destination.x, destination.y)
}
val random: Random = Random(4)
fun ClosedFloatingPointRange<Double>.random(): Double {

    return random.nextDouble(start, endInclusive)
}

fun graphHop() {

    val gh = initialize(Path("\\\\ifv-fs.ifv.kit" +
            ".edu\\User\\Mitarbeiter\\Robin\\Data\\OpenStreetMap\\baden-wuerttemberg" +
            "-260614.osm.pbf"))
    val lonRange = 7.728882..9.711914
    val latRange = 47.776252..49.088258
    val n = 10_000

    var ok = 0
    var failed = 0
    var totalTimeMillis = 0L
    var totalDistanceMeters = 0.0

// Pre-generate points so random generation is not part of the routing measurement.
    val requests = Array(n) {
        GHRequest(
            latRange.random(),
            lonRange.random(),
            latRange.random(),
            lonRange.random(),
        ).apply {
            profile = "car"
            hints.putObject("ch.disable", false)
        }
    }

// Optional warmup
    repeat(n / 100) {
        gh.route(requests[it])
    }

    val elapsedNs = measureNanoTime {
        for (request in requests) {
            val response = gh.route(request)

            if (response.hasErrors()) {
                failed++
                continue
            }

            val path = response.best
            ok++
            totalTimeMillis += path.time
            totalDistanceMeters += path.distance
        }
    }

    println("ok=$ok")
    println("failed=$failed")
    println("total route time min=${totalTimeMillis / 1000.0 / 60.0}")
    println("total distance km=${totalDistanceMeters / 1000.0}")
    println("wall ms=${elapsedNs / 1_000_000.0}")
    println("ns/query=${elapsedNs.toDouble() / n}")
    println("queries/s=${n * 1_000_000_000.0 / elapsedNs}")
}

fun main() {
//    graphHop()
//    return
    val gh = initialize(Path("\\\\ifv-fs.ifv.kit" +
            ".edu\\User\\Mitarbeiter\\Robin\\Data\\OpenStreetMap\\baden-wuerttemberg" +
            "-260614.osm.pbf"))

    val factory = GeometryFactory(PrecisionModel(), 4326)

    val firstRange = 7.728882..9.711914
    val secondRange = 47.776252..49.088258

    val ch = GHRequest(secondRange.random(), firstRange.random(), secondRange.random(), firstRange.random()).apply {
        profile = "car"
        hints.putObject("ch.disable", false)
    }
    val out = gh.route(ch)

    return
    val network = NetfileParser(
        file = Path("src/test/resources/rastatt.net"),
        locale = VisumLocale(),
        utmZone = 32,
        utmHemisphere = Hemisphere.NORTHERN
    ).parse().getConnectedRoadNetwork()
    val weightedGraph = SimpleDirectedWeightedGraph<VisumNode, LinkInfo>(
        LinkInfo::class.java,
    )
    Graphs.addAllVertices(weightedGraph, network.vertexSet())
    for (edge in network.edgeSet()) {
        val (u, v) = network.getUV(edge)
        weightedGraph.addEdge(u, v, edge)
        weightedGraph.setEdgeWeight(edge, edge.freeTravelTime.asSeconds)
    }

    val router = GraphRouter(weightedGraph)
    val dijkstra = BidirectionalDijkstraShortestPath(weightedGraph)
    var i = .0
    val vertexSet = network.vertexSet().toTypedArray()
    repeat(100000) {

        val a = vertexSet.random()
        val b = vertexSet.random()
        val (m1, duration) = measureTimedValue {
            router.travelTime(a, b)
        }
        i += duration.asMinutes

        val (m2, x) =  measureTimedValue {
            dijkstra.getPathWeight(a, b)
        }

        println("CH: ${duration.inWholeNanoseconds} DJ: ${x.inWholeNanoseconds}")
    }
    println(i)

    println("Döner")
//    val a = network.vertexSet().random()
//    val b = network.vertexSet().random()
//    val path = router.route(a, b)
//    val duration = router.travelTime(a, b)
//    val mp = path.toMultipoint()
//    println(path)
}