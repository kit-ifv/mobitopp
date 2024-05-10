package domain.enums

import utils.CodePlan
import utils.Encodable

enum class ZoneClassification(private val code: Int) : Encodable {
    STUDY_AREA(0),
    EXTENDED_STUDY_AREA(1),
    OUTLYING_AREA(2);

    override fun encode() = this.code

    companion object : CodePlan<ZoneClassification> {
        override fun decode(i: Int) = entries.first { it.code == i }
        override fun decode(s: String) = valueOf(s)
    }
}
