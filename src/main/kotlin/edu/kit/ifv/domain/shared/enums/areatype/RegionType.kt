package edu.kit.ifv.domain.shared.enums.areatype
import edu.kit.ifv.utils.codes.Encodable

/**
 * Area types can distinguish areas of different purpose: e.g. residential vs. industrial.
 * There are multiple definitions of area types:
 * hence each project can select which area type should be used.
 */
interface RegionType : Encodable {

    fun toRegioStaR17(): RegioStaR17
    // TODO remove cast function and make models specify which region type they accept
}
