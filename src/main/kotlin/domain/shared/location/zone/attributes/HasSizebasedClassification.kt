package domain.shared.location.zone.attributes

import domain.shared.enums.areatype.SizebasedRegiostarClassification
import domain.shared.location.Location

interface HasSizebasedClassification : Location {
    val sizebasedRegiostarClassification: SizebasedRegiostarClassification
}
