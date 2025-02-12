package domain.enums.areatype

import utils.Encodable

/**
 * Area types can distinguish areas of different purpose: e.g. residential vs. industrial.
 * There are multiple definitions of area types:
 * hence each project can select which area type should be used.
 */
interface AreaType : Encodable
