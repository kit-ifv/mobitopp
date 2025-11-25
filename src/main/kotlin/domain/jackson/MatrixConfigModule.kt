package domain.jackson

import application.config.subconfigs.MatrixConfig
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import java.nio.file.Path
import kotlin.io.path.Path

val MatrixConfigModule = SimpleModule("MatrixConfigModule").apply {
    addDeserializer(MatrixConfig::class.java, MatrixConfigDeserializer())
    // serialization is straight forward.
}

class MatrixConfigDeserializer : JsonDeserializer<MatrixConfig>() {
    override fun deserialize(
        p0: JsonParser?,
        p1: DeserializationContext?
    ): MatrixConfig? {
        if (p0 == null) return null

        if (p0.currentToken == JsonToken.START_OBJECT) {
            p0.nextToken()
        }
        val givenParams = mutableMapOf<String, String>()
        do {
            if (p0.currentToken == JsonToken.FIELD_NAME) {
                val name = p0.text
                if (p0.nextToken() == JsonToken.VALUE_STRING) {
                    val value = p0.valueAsString
                    givenParams[name] = value
                } else {
                    error(
                        "Unexpected JsonToken. After a field_name a string value should follow for MatrixConfig objects"
                    )
                }
            }
        } while (p0.nextToken() != JsonToken.END_OBJECT)

        return initConfig(givenParams)
    }

    private fun Map<String, String>.retrieveParam(name: String,): Path? {
        return if (containsKey(name)) { Path(get(name)!!) } else null
    }

    /**
     * Constructs a config out of the given params.
     * @throws error If the given params do not contain either 'matrixRepo' or all other fields since
     * no sensible config can be constructed then.
     */
    private fun initConfig(givenParams: Map<String, String>): MatrixConfig {
        val matrixRepo: Path? = givenParams.retrieveParam("matrixRepo")
        val costMatrixConfig: Path? = givenParams.retrieveParam("costMatrixConfig")
        val durationMatrixConfig: Path? = givenParams.retrieveParam("durationMatrixConfig")
        val distanceMatrix: Path? = givenParams.retrieveParam("distanceMatrix")

        if (matrixRepo != null) {
            return MatrixConfig(
                matrixRepo = matrixRepo,
                costMatrixConfig = costMatrixConfig,
                durationMatrixConfig = durationMatrixConfig,
                distanceMatrix = distanceMatrix
            )
        } else if (
            costMatrixConfig != null &&
            durationMatrixConfig != null &&
            distanceMatrix != null
        ) {
            return MatrixConfig(
                costMatrixConfig = costMatrixConfig,
                durationMatrixConfig = durationMatrixConfig,
                distanceMatrix = distanceMatrix
            )
        } else {
            error("Missing mandatory fields. Either set 'matrixRepo' or all other fields.")
        }
    }
}
