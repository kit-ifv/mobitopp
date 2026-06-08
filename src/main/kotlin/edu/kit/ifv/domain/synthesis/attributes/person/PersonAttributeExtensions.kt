package edu.kit.ifv.domain.synthesis.attributes.person
import edu.kit.ifv.domain.shared.enums.person.Employment
import edu.kit.ifv.domain.synthesis.behavior.MinimalistPerson

val MinimalistPerson<HasLicence>.hasLicence get() = attributes.hasLicence
val MinimalistPerson<HasEmployment>.employment get() = attributes.employment
fun MinimalistPerson<HasEmployment>.isPrimaryStudent(): Boolean = employment == Employment.STUDENT_PRIMARY
fun MinimalistPerson<HasEmployment>.isSecondaryStudent(): Boolean = employment == Employment.STUDENT_SECONDARY
fun MinimalistPerson<HasEmployment>.isTertiaryStudent(): Boolean = employment == Employment.STUDENT_TERTIARY

fun MinimalistPerson<HasEmployment>.isWorker(): Boolean =
    employment == Employment.FULLTIME || employment == Employment.PARTTIME
