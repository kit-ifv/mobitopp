package utils.binary

import datastructure.matrix.mapFileToMemory
import domain.data.ActivityId
import domain.data.CarEngineStatistics
import domain.data.CarId
import domain.data.CarSegment
import domain.data.ChargingInfluence
import domain.data.EconomicStatus
import domain.data.Employment
import domain.data.EngineType
import domain.data.Graduation
import domain.data.Household
import domain.data.HouseholdId
import domain.data.MutableHousehold
import domain.data.MutablePerson
import domain.data.MutablePlannedActivity
import domain.data.MutablePrivateCar
import domain.data.MutableZone
import domain.data.Person
import domain.data.PersonId
import domain.data.PlannedActivity
import domain.data.PrivateCar
import domain.data.Sex
import domain.data.Zone
import domain.data.ZoneId
import domain.data.buildEngine
import domain.enums.ActivityType
import domain.enums.AreaType
import domain.enums.ZoneClassification
import domain.location.LOCATIONUNKNOWN
import domain.location.Location
import domain.location.RoadAccess
import units.CurrencyUnit
import units.DistanceUnit
import units.GPSCoordinate
import units.UnitIntervalValue
import units.euros
import units.share
import units.toDistance
import usecases.steps.ActivityLocation
import utils.CodePlan
import utils.Decodable
import utils.units.sinceStart
import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.nio.MappedByteBuffer
import java.nio.file.Files
import java.nio.file.Path
import kotlin.time.DurationUnit
import kotlin.time.toDuration


fun MappedByteBuffer.getBoolean(index: Int): Boolean {
    val b = getInt(index)
    /* This magic number is the hexadecimal representation of an integer block in memory, such as 01 XX XX XX
    since the boolean flag is written as a byte of 01, the quickest way to check whether the integer at that location
    matches this mask 01-XX-XX-XX, because there is no convenient way to get a boolean from a bytebuffer, and no
    convenient way to apply mask operations on anything but an int.
    * */
    return (b and 0x01000000) != 0
}

fun interface BinaryReader<out MUTABLE> {
    fun fromBinary(path: Path): List<MUTABLE>
}

fun interface BinaryWriter<in READONLY> {
    fun toBinary(path: Path, elements: Collection<READONLY>) {
        Files.newOutputStream(path).use { fileStream ->
            BufferedOutputStream(fileStream).use { bufferedStream ->
                DataOutputStream(bufferedStream).use { outputStream ->
                    operateStream(outputStream, elements)

                }
            }
        }
    }

    fun operateStream(outStream: DataOutputStream, elements: Collection<READONLY>)
}

/**
 * To avoid issues with keeping track at which index an element is read, the tracking buffer remembers where
 * the read process is at the current time. Maybe this is slower than providing the correct byte offsets, but
 * it prevents counting errors, which may easily occur.
 */
class TrackingBuffer(private val buffer: MappedByteBuffer, initialOffset: Int) {
    var currentPosition = initialOffset

    val nextInt get() = buffer.getInt(currentPosition).also { currentPosition += 4 }
    val nextDouble get() = buffer.getDouble(currentPosition).also { currentPosition += 8 }
    val nextLong get() = buffer.getLong(currentPosition).also { currentPosition += 8 }
    val nextBoolean get() = buffer.getBoolean(currentPosition).also { currentPosition += 1 }
    fun readString(length: Int) : String {
        val byteArray = CharArray(length)
        (0..<length).forEach  {
            byteArray[it] = buffer.getChar(currentPosition)
            currentPosition += 2
        }
        return String(byteArray)
    }
}

class BinaryHouseholdReader(val zoneC: (ZoneId) -> Zone, private val contextSimulationSeed: Long) :
    BinaryReader<MutableHousehold> {

    private val idBitSize = 8
    override fun fromBinary(path: Path): List<MutableHousehold> {
        val mappedBuffer = mapFileToMemory(path)
        val size = mappedBuffer.getInt(0)
        val ids = Array(size) {
            HouseholdId(-1)
        }

        for (i in 0 until size) {
            ids[i] = HouseholdId(mappedBuffer.getLong(i * idBitSize + 4))
        }
        val households = ids.map {
            MutableHousehold(
                it, contextSimulationSeed
            )
        }
        for (i in 0 until size) {
            extractInfos(mappedBuffer, i * attributesBitSize + 4 + size * idBitSize, households[i])
        }
        return households
    }

    private val attributesBitSize = 72
    private fun extractInfos(buffer: MappedByteBuffer, at: Int, household: MutableHousehold) {
        TrackingBuffer(buffer, at).run {
            household.apply {
                householdNumber = nextLong
                surveyYear = nextInt
                domCode = nextInt
                type = nextInt
                incomePerMonth = nextDouble.euros
                val ecoStatusNumber = nextInt
                economicStatus = EconomicStatus.decode(ecoStatusNumber)
                location = nextLocation(converter = zoneC)
            }
        }
    }


}

class BinaryHouseholdWriter : BinaryWriter<Household> {
    override fun operateStream(outStream: DataOutputStream, elements: Collection<Household>) {
        outStream.writeInt(elements.size) // Write Size as Int in the beginning of the file
        // Separate the writing to mimic the construction of the object, so first write all the necessary constructor parameters
        elements.forEach { outStream.writeIDS(it) }
        // And then write all the secondary attributes that are set afterwards.
        elements.forEach { outStream.writeHouseholdAttributes(it) }
    }

    private fun DataOutputStream.writeIDS(element: Household) {
        writeLong(element.id.value)
    }

    private fun DataOutputStream.writeHouseholdAttributes(element: Household) {
        element.run {
            writeLong(householdNumber) // 8
            writeInt(surveyYear) // 12
            writeInt(domCode) // 16
            writeInt(type) // 20
            writeDouble(incomePerMonth.toDouble(CurrencyUnit.EUROS)) // 28
            writeInt(economicStatus.encode()) // 32
            writeLocation(location) // 72
        }
    }

}
// 40 bit
/**
 * Adds 40 bit to the write process
 */
fun DataOutputStream.writeLocation(location: Location) {
    writeLong(location.zone?.id?.value?: Long.MIN_VALUE)
    writeDouble(location.coordinate.latitudeDegrees)
    writeDouble(location.coordinate.longitudeDegrees)
    writeLong(location.roadAccess?.roadId?: Long.MIN_VALUE)
    writeDouble(location.roadAccess?.position?.toDouble()?: 0.5)
}

fun TrackingBuffer.nextLocation(converter: (ZoneId) -> Zone?): Location {
    val zoneId = ZoneId(nextLong)
    val coordinate = GPSCoordinate.decimalDegree(nextDouble, nextDouble)
    val roadAccess = RoadAccess(nextLong, nextDouble.share())
    return Location(coordinate, converter(zoneId), roadAccess)
}

class FixedDestinationReader(
    val personConverter: (PersonId) -> Person,
    private val activityTypeConverter: CodePlan<ActivityType>,
    val zoneConverter: (ZoneId) -> Zone
) : BinaryReader<ActivityLocation> {
    override fun fromBinary(path: Path): List<ActivityLocation> {
        val mappedBuffer = mapFileToMemory(path)
        val size = mappedBuffer.getInt(0)
        return (0 until size).map {
            extractContent(mappedBuffer, it * elementBitSize + 4)
        }
    }

    private fun extractContent(buffer: MappedByteBuffer, at: Int): ActivityLocation {
        return TrackingBuffer(buffer, at).run {
            val person = personConverter(PersonId(nextLong))
            val activityType = activityTypeConverter.decode(nextInt)
            val zone = zoneConverter(ZoneId(nextLong))
            val lat = nextDouble
            val lon = nextDouble
            val roadId = nextLong
            val roadShare = nextDouble

            ActivityLocation(
                person, activityType, Location(
                    GPSCoordinate.decimalDegree(lat, lon),
                    zone,
                    RoadAccess(roadId, roadShare.share())
                )
            )
        }
    }

    private val elementBitSize = 52
}

class FixedDestinationWriter : BinaryWriter<ActivityLocation> {
    override fun operateStream(outStream: DataOutputStream, elements: Collection<ActivityLocation>) {
        val size = elements.size
        outStream.writeInt(size) // Write the amount of agents that are expected to be found in this file
        elements.forEach { outStream.encodeElement(it) }
    }


    private fun DataOutputStream.encodeElement(act: ActivityLocation) {
        act.run {
            writeLong(person.id.value) // 8 Bit
            writeInt(activityType.encode()) // 12 Bit
            writeLong(location.zone?.id?.value ?: -1) // 20 Bit
            writeDouble(location.coordinate.latitudeDegrees) // 28 Bit
            writeDouble(location.coordinate.longitudeDegrees) // 36 Bit
            writeLong(location.roadAccess?.roadId ?: Long.MIN_VALUE) // 44 Bit
            writeDouble(location.roadAccess?.position?.toDouble() ?: 0.5) // 52 Bit
            // TODO maybe add lateral distance if needed.
        }
    }
}

class BinaryCarReader(
    val householdConverter: (HouseholdId) -> MutableHousehold,
    val personConverter: (PersonId) -> Person,
    val zoneConverter: (ZoneId) -> Zone? = {null},
    private val carEngineStatistics: CarEngineStatistics = CarEngineStatistics()
) : BinaryReader<MutablePrivateCar> {
    override fun fromBinary(path: Path): List<MutablePrivateCar> {
        val mappedBuffer = mapFileToMemory(path)
        val size = mappedBuffer.getInt(0)
        // Preallocate id array
        val idArray = Array(size) {
            CarId(-1L) to HouseholdId(-1L)
        }

        for (i in 0 until size) {
            idArray[i] = extractIds(mappedBuffer, i * idBitSize + 4)
        }
        val cars = idArray.map {
            MutablePrivateCar(
                it.first, householdConverter(it.second)
            )
        }
        for (i in 0 until size) {
            extractContent(mappedBuffer, i * attributeBitSize + 4 + size * idBitSize, cars[i])
        }

        return cars
    }


    private val idBitSize = 16
    private fun extractIds(buffer: MappedByteBuffer, at: Int): Pair<CarId, HouseholdId> {
        return TrackingBuffer(buffer, at).run {
            CarId(nextLong) to HouseholdId(nextLong)
        }
    }

    private fun extractContent(buffer: MappedByteBuffer, at: Int, car: MutablePrivateCar) {
        TrackingBuffer(buffer, at).run {
            car.apply {

                seats = nextInt

                val personId = PersonId(nextLong)
                mainUser = if(personId != PersonId(Long.MIN_VALUE)) personConverter(personId) else null
                segment = CarSegment.decode(nextInt)
                val engineType = EngineType.decode(nextInt)
                engine = carEngineStatistics.buildEngine(segment, engineType)
                location = nextLocation(zoneConverter)
            }
        }

    }

    private val attributeBitSize = 60
}

class BinaryCarWriter : BinaryWriter<PrivateCar> {
    override fun operateStream(outStream: DataOutputStream, elements: Collection<PrivateCar>) {
        val size = elements.size
        outStream.writeInt(size) // Write the amount of agents that are expected to be found in this file

        elements.forEach { outStream.encodeIDs(it) } // For each agent write the ID and the household ID
        elements.forEach { outStream.encodeAttributes(it) }
    }

    private fun DataOutputStream.encodeIDs(car: PrivateCar) {
        writeLong(car.id.value)       //  8 bit
        writeLong(car.owner.id.value) // 16 bit
    }

    private fun DataOutputStream.encodeAttributes(car: PrivateCar) {
        car.run {
            writeInt(seats)                         //  4 Bit
            writeLong(mainUser?.id?.value ?: Long.MIN_VALUE) // 12 Bit
            writeInt(segment.encode())              // 16 Bit
            writeInt(engine.type.encode())          // 20 Bit
            writeLocation(location) // 60 bit
        }
    }
}

class BinaryActivityReader(
    private val codeActivity: CodePlan<ActivityType>,
    val personConverter: (PersonId) -> Person,
    private val contextSimulationSeed: Long
) : BinaryReader<MutablePlannedActivity> {
    override fun fromBinary(file: Path): List<MutablePlannedActivity> {
        val mappedBuffer = mapFileToMemory(file)
        val size = mappedBuffer.getInt(0)
        val idArray = Array(size) {
            ActivityId(-1L)
        }

        for (i in 0 until size) {
            idArray[i] = extractIds(mappedBuffer, i * idBitSize + 4)
        }
        val activities = idArray.map {
            MutablePlannedActivity(
                it, contextSimulationSeed
            )
        }
        //TODO find solution for magic numbers.
        for (i in 0 until size) {
            extractContent(mappedBuffer, i * attributesBitSize + 4 + size * idBitSize, activities[i])
        }

        return activities


    }

    private fun extractIds(buffer: MappedByteBuffer, at: Int): ActivityId {
        return ActivityId(buffer.getLong(at))
    }

    private val idBitSize = 8


    // 28 Bits
    private fun extractContent(obuffer: MappedByteBuffer, at: Int, target: MutablePlannedActivity) {
        val buffer = TrackingBuffer(obuffer, at)
        target.apply {
            val personId = PersonId(buffer.nextLong)
            person = personConverter(personId)
            observedTripDuration = buffer.nextInt.toDuration(DurationUnit.MINUTES)
            startTime = buffer.nextLong.toDuration(DurationUnit.MINUTES).sinceStart
            duration = buffer.nextInt.toDuration(DurationUnit.MINUTES)
            activityType = codeActivity.decode(buffer.nextInt)
        }
    }

    private val attributesBitSize = 28
}

class BinaryActivityWriter : BinaryWriter<PlannedActivity> {

    override fun operateStream(outStream: DataOutputStream, elements: Collection<PlannedActivity>) {
        val size = elements.size
        outStream.writeInt(size) // Write the amount of agents that are expected to be found in this file

        elements.forEach { outStream.encodeID(it) } // For each agent write the ID and the household ID
        elements.forEach { outStream.encodeAttributes(it) }
    }


    private fun DataOutputStream.encodeID(act: PlannedActivity) {
        writeLong(act.id.value)
    }


    private fun DataOutputStream.encodeAttributes(act: PlannedActivity) {
        writeLong(act.person.id.value) // 8
        writeInt(act.observedTripDuration.toInt(DurationUnit.MINUTES)) // 12
        writeLong(act.startTime.minutesSinceStart) // 20
        writeInt(act.duration.toInt(DurationUnit.MINUTES)) // 24
        writeInt(act.activityType.encode()) // 28
    }
}

class BinaryPersonReader(val map: (HouseholdId) -> MutableHousehold, private val contextSimulationSeed: Long) :
    BinaryReader<MutablePerson> {
    override fun fromBinary(path: Path): List<MutablePerson> {
        val mappedBuffer = mapFileToMemory(path)
        val size = mappedBuffer.getInt(0)
        val idArray = Array(size) {
            PersonId(-1L) to HouseholdId(-1)
        }

        for (i in 0 until size) {
            idArray[i] = extractIds(mappedBuffer, i * idBitSize + 4)
        }
        val persons = idArray.map {
            MutablePerson(
                it.first, map(it.second), contextSimulationSeed
            )
        }
        for (i in 0 until size) {
            extractInfos(mappedBuffer, i * attributesBitSize + 4 + size * idBitSize, persons[i])
        }

        return persons
    }

    // 16 Bit
    private fun extractIds(buffer: MappedByteBuffer, at: Int): Pair<PersonId, HouseholdId> {
        return TrackingBuffer(buffer, at).run {
            PersonId(nextLong) to HouseholdId(nextLong)
        }
    }

    private val idBitSize = 16

    // 39 Bit
    private fun extractInfos(buffer: MappedByteBuffer, at: Int, person: MutablePerson) {
        TrackingBuffer(buffer, at).run {
            person.apply {
                age = nextInt
                employment = Employment.decode(nextInt)
                sex = Sex.decode(nextInt)
                income = nextDouble.euros
                hasBike = nextBoolean
                hasCommuterTicket = nextBoolean
                hasLicense = nextBoolean
                eMobilityAcceptance = UnitIntervalValue(nextDouble)
                chargingInfluence = ChargingInfluence.decode(nextInt)
                graduation = Graduation.decode(nextInt)

            }
        }
    }

    private val attributesBitSize = 39

}

class BinaryPersonWriter : BinaryWriter<Person> {
    override fun operateStream(outStream: DataOutputStream, elements: Collection<Person>) {
        val size = elements.size
        outStream.writeInt(size) // Write the amount of agents that are expected to be found in this file

        elements.forEach { outStream.encodeID(it) } // For each agent write the ID and the household ID
        elements.forEach { outStream.encodeAttributes(it) }


    }

    private fun DataOutputStream.encodeID(person: Person) {

        person.run {
            writeLong(id.value)             //  8 bit
            writeLong(household.id.value)   // 16 bit
        }
    }

    // Currently 39 byte
    private fun DataOutputStream.encodeAttributes(person: Person) {
        person.run {
            writeInt(age)                                       // 4 byte
            writeInt(employment.encode())                       // 8 byte
            writeInt(sex.encode()) // TODO could be boolean     //12 byte
            writeDouble(income.toDouble(CurrencyUnit.EUROS))    //20 byte
            writeBoolean(hasBike)                               //21 byte
            writeBoolean(hasCommuterTicket)                     //22 byte
            writeBoolean(hasLicense)                            //23 byte
            writeDouble(eMobilityAcceptance.toDouble())         //31 byte
            writeInt(chargingInfluence.encode())                //35 byte
            writeInt(graduation.encode())                       //39 byte
            //TODO add memberships, they are currently missing

        }
    }
}

class BinaryZoneReader(val seed: Long, val regionCode: Decodable<AreaType>) : BinaryReader<MutableZone> {
    override fun fromBinary(path: Path): List<MutableZone> {
        val mappedBuffer = mapFileToMemory(path)
        val size = mappedBuffer.getInt(0)
        val maxNameLength = mappedBuffer.getInt(4)
        val idArray = Array(size) {
            ZoneId(-1L) to LOCATIONUNKNOWN
        }

        for (i in 0 until size) {
            idArray[i] = extractIds(mappedBuffer, i * idBitSize + 4 + 4)
        }
        val zones = idArray.map {
            MutableZone(
                it.first, it.second, seed
            )
        }
        for (i in 0 until size) {
            extractInfos(
                mappedBuffer, i * (attributesBitSize + maxNameLength) + 4 + 4 + size * idBitSize, zones[i],
                maxNameLength = maxNameLength
            )
        }

        return zones
    }

    // 40 Bit
    private fun extractIds(buffer: MappedByteBuffer, at: Int): Pair<ZoneId, Location> {
        return TrackingBuffer(buffer, at).run {

            val id = nextLong

            ZoneId(id) to Location(
                GPSCoordinate.decimalDegree(nextDouble, nextDouble),
                roadAccess = RoadAccess(roadId = nextLong, position = UnitIntervalValue(nextDouble)),
                zone= null
            )
        }
    }

    private val idBitSize = 40

    // 29 + ???? Bit
    private fun extractInfos(buffer: MappedByteBuffer, at: Int, zone: MutableZone, maxNameLength: Int) {
        TrackingBuffer(buffer, at).run {
            zone.apply {
                visumId = nextLong //8
                name = readString(maxNameLength)//??
                regionType = regionCode.decode(nextInt) //12
                classification = ZoneClassification.decode(nextInt)//16
                parkingPlaces = nextInt//20
                isDestination = nextBoolean//21
                relief = nextDouble.toDistance(DistanceUnit.METERS)//29

            }
        }
    }

    private val attributesBitSize = 29
}

class BinaryZoneWriter : BinaryWriter<Zone> {
    override fun operateStream(outStream: DataOutputStream, elements: Collection<Zone>) {
        val size = elements.size
        val maxNameLength =
            elements.maxOf { it.name.length } // It is idiotic to give the zones a name, it was never used in old mobitopp, and won't be used in new mobitopp
        outStream.writeInt(size) // Write the amount of zones found in the simulation
        outStream.writeInt(maxNameLength)
        elements.forEach { outStream.encodeID(it) } // Encode constructor arguments of MutableZone
        elements.forEach { outStream.encodeAttributes(it, maxNameLength) } // Encode Secondary arugments, which are set in the builder


    }

    private fun DataOutputStream.encodeID(zone: Zone) {

        zone.run {
            writeLong(id.value)
            writeDouble(centroid.coordinate.latitudeDegrees)
            writeDouble(centroid.coordinate.longitudeDegrees)
            writeLong(centroid.roadAccess?.roadId ?: Long.MIN_VALUE)//  8 bit
            writeDouble(centroid.roadAccess?.position?.toDouble() ?: 0.0)
            //TODO maybe lateral Distance, or even a write Location method, since this is not the first time a location is written
        }
    }


    private fun DataOutputStream.encodeAttributes(zone: Zone, maxNameLength: Int) {
        zone.run {
            writeLong(visumId)
            // Note that the matrix column field is not written, it is simply an index, and can thus be parsed in the reader
            writeChars(name.padEnd(maxNameLength, '.'))
            writeInt(regionType.encode())
            writeInt(classification.encode())
            writeInt(parkingPlaces)
            writeBoolean(isDestination)
            writeDouble(relief.toDouble(DistanceUnit.METERS))
        }
    }
}

