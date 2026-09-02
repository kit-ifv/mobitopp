package edu.kit.ifv.domain.synthesis.results.binary

import edu.kit.ifv.binary.ParallelBinaryRW

val StandardOutputBinaryCarWriter = ParallelBinaryRW<SynthesisCarBinaryRecord>(
    0,
    { record, _ ->
        putInt(record.houseHoldID)
        putLong(record.carID)
    },
    { _ ->
        SynthesisCarBinaryRecord(int, long)
    },
)

val StandardFixedDestinationWriter = ParallelBinaryRW<FixedDestinationBinaryRecord>(
    0,
    { record, _ ->
        putInt(record.personID)
        putInt(record.activityType)
        putLong(record.zoneID)
    },
    { _ ->
        FixedDestinationBinaryRecord(int, int, long)
    },
)

val StandardSynthesisBinaryActivitiesWriter = ParallelBinaryRW<ActivitiesBinaryRecord>(
    0,
    { record, _ ->
        putInt(record.personID)
        putLong(record.durationMinutes)
        putLong(record.startTimeMinutes)
        putInt(record.activityType)
    },
    { _ ->
        ActivitiesBinaryRecord(int, long, long, int)
    },
)
