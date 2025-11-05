package domain.simulation.results

import core.events.AppScope
import domain.shared.enums.Mode
import domain.synthesis.data.PersonId
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import utils.units.AbsoluteTime
import java.nio.file.Path
import kotlin.io.path.createDirectories

interface AvailabilityWriter {

    fun notify(time: AbsoluteTime, id: PersonId, tag: String, result: List<Mode>)

    fun close()
}

object NoAvailabilityWriter : AvailabilityWriter {
    override fun notify(time: AbsoluteTime, id: PersonId, tag: String, result: List<Mode>) {
        //
    }
    override fun close() {
        //
    }
}

class ConcurrentAvailabilityWriter(
    filePath: Path
) : AvailabilityWriter {
    private val channel: Channel<String> = Channel(Channel.UNLIMITED)

    private val job: Job = AppScope.launch {

        filePath.parent.createDirectories()

        filePath.toFile().bufferedWriter().use { writer ->
            writer.write("time;id;tag;mode")
            writer.newLine()
            for (res in channel) {
                writer.write(res)
                writer.newLine()
            }
        }
    }

    override fun notify(time: AbsoluteTime, id: PersonId, tag: String, result: List<Mode>) {
        channel.trySend(stringify(time, id, tag, result))
    }

    private fun stringify(time: AbsoluteTime, id: PersonId, tag: String, result: List<Mode>): String =
        result.joinToString("\n") { mode ->
            "${time.secondsSinceStart};${id.value};$tag;${mode.code}"
        }

    override fun close() = runBlocking {
        channel.close()
        job.join()
    }
}
