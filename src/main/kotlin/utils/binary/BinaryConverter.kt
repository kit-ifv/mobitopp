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
import domain.data.Person
import domain.data.PersonId
import domain.data.PlannedActivity
import domain.data.PrivateCar
import domain.data.Sex
import domain.data.Zone
import domain.data.ZoneId
import domain.data.buildEngine
import domain.enums.ActivityType
import domain.location.Location
import domain.location.RoadAccess
import units.CurrencyUnit
import units.GPSCoordinate
import units.UnitIntervalValue
import units.euros
import units.share
import usecases.steps.ActivityLocation
import utils.CodePlan
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
    return (b and 0x80) != 0
}

interface BinaryConverter<T, S : T> {
    fun fromBinary(file: Path): List<S>
    fun toBinary(path: Path, elements: Collection<T>) {
        Files.newOutputStream(path).use { fileStream ->
            BufferedOutputStream(fileStream).use { bufferedStream ->
                DataOutputStream(bufferedStream).use { outputStream ->
                    operateStream(outputStream, elements)

                }
            }
        }
    }

    fun operateStream(outStream: DataOutputStream, elements: Collection<T>)
}

class TrackingBuffer(private val buffer: MappedByteBuffer, initialOffset: Int) {
    private var currentPosition = initialOffset

    val nextInt get() = buffer.getInt(currentPosition).also { currentPosition += 4 }
    val nextDouble get() = buffer.getDouble(currentPosition).also { currentPosition += 8 }
    val nextLong get() = buffer.getLong(currentPosition).also { currentPosition += 8 }
    val nextBoolean get() = buffer.getBoolean(currentPosition).also { currentPosition += 1 }
}


class HouseholdConverter(val zoneC: (ZoneId) -> Zone, private val contextSimulationSeed: Long) :
    BinaryConverter<Household, MutableHousehold> {
    constructor(map: Map<ZoneId, Zone>, contextSimulationSeed: Long) : this(map::getValue, contextSimulationSeed)

    override fun fromBinary(file: Path): List<MutableHousehold> {
        val mappedBuffer = mapFileToMemory(file)
        val size = mappedBuffer.getInt(0)
        val ids = Array<HouseholdId>(size) {
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

    override fun operateStream(outStream: DataOutputStream, elements: Collection<Household>) {
        outStream.writeInt(elements.size) // Write Size as Int in the beginning of the file
        // Separate the writing to mimic the construction of the object, so first write all the necessary constructor parameters
        elements.forEach { outStream.writeIDS(it) }
        // And then write all the secondary attributes that are set afterwards.
        elements.forEach { outStream.writeHouseholdAttributes(it) }
    }

    private val idBitSize = 8
    private fun DataOutputStream.writeIDS(element: Household) {
        writeLong(element.id.value)
    }

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
                val zoneId = ZoneId(nextLong)
                val zone = zoneC(zoneId)
                val lat = nextDouble
                val lon = nextDouble
                location = Location(GPSCoordinate.decimalDegree(lat, lon), zone, roadAccess = null)
            }
        }
    }

    private val attributesBitSize = 56
    private fun DataOutputStream.writeHouseholdAttributes(element: Household) {
        element.run {
            writeLong(householdNumber) // 8
            writeInt(surveyYear) // 12
            writeInt(domCode) // 16
            writeInt(type) // 20
            writeDouble(incomePerMonth.toDouble(CurrencyUnit.EUROS)) // 28
            writeInt(economicStatus.encode()) // 32
            writeLong(location.zone?.id?.value ?: -1) // 40
            writeDouble(location.coordinate.latitudeDegrees) // 48
            writeDouble(location.coordinate.longitudeDegrees) //56
        }
    }

}

class FixedDestinationConverter(
    val personConverter: (PersonId) -> Person,
    private val activityTypeConverter: CodePlan<ActivityType>,
    val zoneConverter: (ZoneId) -> Zone
) : BinaryConverter<ActivityLocation, ActivityLocation> {

    override fun fromBinary(file: Path): List<ActivityLocation> {
        val mappedBuffer = mapFileToMemory(file)
        val size = mappedBuffer.getInt(0)
        return (0 until size).map {
            extractContent(mappedBuffer, it * elementBitSize + 4)
        }
    }


    override fun operateStream(outStream: DataOutputStream, elements: Collection<ActivityLocation>) {
        val size = elements.size
        outStream.writeInt(size) // Write the amount of agents that are expected to be found in this file
        elements.forEach { outStream.encodeElement(it) }
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

class CarConverter(
    val householdConverter: (HouseholdId) -> MutableHousehold,
    val personConverter: (PersonId) -> Person,
    private val carEngineStatistics: CarEngineStatistics = CarEngineStatistics()
) : BinaryConverter<PrivateCar, MutablePrivateCar> {
    override fun fromBinary(file: Path): List<MutablePrivateCar> {
        val mappedBuffer = mapFileToMemory(file)
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
                mainUser = personConverter(PersonId(nextLong))
                segment = CarSegment.decode(nextInt)
                val engineType = EngineType.decode(nextInt)
                engine = carEngineStatistics.buildEngine(segment, engineType)
            }
        }

    }

    private val attributeBitSize = 20
    private fun DataOutputStream.encodeAttributes(car: PrivateCar) {
        car.run {
            writeInt(seats)                         //  4 Bit
            writeLong(mainUser?.id?.value ?: -1) // 12 Bit
            writeInt(segment.encode())              // 16 Bit
            writeInt(engine.type.encode())          // 20 Bit
        }
    }


}

class ActivityConverter(
    private val codeActivity: CodePlan<ActivityType>,
    val personConverter: (PersonId) -> Person,
    private val contextSimulationSeed: Long
) : BinaryConverter<PlannedActivity, MutablePlannedActivity> {
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

    override fun operateStream(outStream: DataOutputStream, elements: Collection<PlannedActivity>) {
        val size = elements.size
        outStream.writeInt(size) // Write the amount of agents that are expected to be found in this file

        elements.forEach { outStream.encodeID(it) } // For each agent write the ID and the household ID
        elements.forEach { outStream.encodeAttributes(it) }
    }

    private fun extractIds(buffer: MappedByteBuffer, at: Int): ActivityId {
        return ActivityId(buffer.getLong(at))
    }

    private val idBitSize = 8
    private fun DataOutputStream.encodeID(act: PlannedActivity) {
        writeLong(act.id.value)
    }

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

    private fun DataOutputStream.encodeAttributes(act: PlannedActivity) {
        writeLong(act.person.id.value) // 8
        writeInt(act.observedTripDuration.toInt(DurationUnit.MINUTES)) // 12
        writeLong(act.startTime.minutesSinceStart) // 20
        writeInt(act.duration.toInt(DurationUnit.MINUTES)) // 24
        writeInt(act.activityType.encode()) // 28
    }
}

class PersonConverter(val map: (HouseholdId) -> MutableHousehold, private val contextSimulationSeed: Long) :
    BinaryConverter<Person, MutablePerson> {
    constructor(map: Map<HouseholdId, MutableHousehold>, seed: Long) : this(map::getValue, seed)

    override fun fromBinary(file: Path): List<MutablePerson> {
        val mappedBuffer = mapFileToMemory(file)
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
        //TODO find solution for magic numbers.
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
    private fun DataOutputStream.encodeID(person: Person) {

        person.run {
            writeLong(id.value)             //  8 bit
            writeLong(household.id.value)   // 16 bit
        }
    }

    override fun operateStream(outStream: DataOutputStream, elements: Collection<Person>) {
        val size = elements.size
        outStream.writeInt(size) // Write the amount of agents that are expected to be found in this file

        elements.forEach { outStream.encodeID(it) } // For each agent write the ID and the household ID
        elements.forEach { outStream.encodeAttributes(it) }
    }

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

    // Currently 39 bits
    private fun DataOutputStream.encodeAttributes(person: Person) {
        person.run {
            writeInt(age)                                       // 4 bit
            writeInt(employment.encode())                       // 8 bit
            writeInt(sex.encode()) // TODO could be boolean     //12 bit
            writeDouble(income.toDouble(CurrencyUnit.EUROS))    //20 bit
            writeBoolean(hasBike)                               //21 bit
            writeBoolean(hasCommuterTicket)                     //22 bit
            writeBoolean(hasLicense)                            //23 bit
            writeDouble(eMobilityAcceptance.toDouble())         //31 bit
            writeInt(chargingInfluence.encode())                //35 bit
            writeInt(graduation.encode())                       //39 bit
            //TODO add memberships, they are currently missing

        }
    }


}


