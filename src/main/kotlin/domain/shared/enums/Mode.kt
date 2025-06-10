package domain.shared.enums

import utils.Encodable

/**
 * A mode describes the type of transportation a person uses to travel.
 * Each project can provide a custom definition of which mode of transportation are available.
 */
interface Mode : Encodable {

    val requiresVehicleTakeAlong: Boolean
}

object MODEUNKOWN : Mode { // TODO get rid of mode unknown!
    override val requiresVehicleTakeAlong: Boolean = false

    private const val ERROR_CODE = "MODE UNKNOWN should never be encoded!"

    override val code: Int
        get() = throw UnsupportedOperationException(ERROR_CODE)

    override val description: String
        get() = throw UnsupportedOperationException(ERROR_CODE)
}
