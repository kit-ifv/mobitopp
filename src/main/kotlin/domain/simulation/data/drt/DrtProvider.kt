<<<<<<<< HEAD:src/main/kotlin/domain/simulation/data/DrtProvider.kt
package domain.simulation.data
========
package domain.synthesis.data.drt
>>>>>>>> origin/merge-before-release:src/main/kotlin/domain/simulation/data/drt/DrtProvider.kt

import domain.shared.enums.Mode
import domain.shared.location.zone.ZoneId
import utils.Identifiable

<<<<<<<< HEAD:src/main/kotlin/domain/simulation/data/DrtProvider.kt
@JvmInline
value class DrtProviderId(val value: Long)

========
// TODO remove I from all domain entity interfaces, use pattern as in this file:
// interface = entity name
// mutable long term data class = entity name + "Data"
// dynamic agent class = entity name + "Agent"
>>>>>>>> origin/merge-before-release:src/main/kotlin/domain/simulation/data/drt/DrtProvider.kt
interface DrtProvider : Identifiable<DrtProviderId> {
    val name: String
    val mode: Mode
    val serviceArea: List<ZoneId>
    val operatingHours: IntRange // TODO more detailed hours
}

