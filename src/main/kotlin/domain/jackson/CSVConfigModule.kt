package domain.jackson

import application.config.subconfigs.CSVConfig
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.module.SimpleModule
import java.nio.file.Path
import kotlin.io.path.Path

val CSVConfigModule = SimpleModule("CSVConfigModule").apply {
    addDeserializer(CSVConfig::class.java, CSVConfigDeserializer())
    // serialization is straight forward.
}

class CSVConfigDeserializer : JsonDeserializer<CSVConfig>() {
    override fun deserialize(
        p0: JsonParser?,
        p1: DeserializationContext?
    ): CSVConfig? {
        if (p0 == null || p1 == null) return null
        if (p0.currentToken == JsonToken.START_OBJECT) {
            p0.nextToken()
        }
        val node = p0.codec.readTree<JsonNode>(p0)
        val givenParams = mutableMapOf<String, String>()
        val parameterNames = CSVConfig.getParameterNames()
        for(name in parameterNames) {
            if (node.get(name) != null)
            givenParams[name] = node.get(name).textValue()
        }
        return initConfig(givenParams)
    }

    private fun Map<String, String>.retrieveParam(name: String): Path? {
        return if (containsKey(name)) { Path(get(name)!!) } else null
    }

    /**
     * Constructs a config out of the given params.
     * @throws error If the given params don't contain either 'dataRepo' and 'zoneRepo' or all other fields since
     * no sensible config can be constructed then.
     */
    private fun initConfig(givenParams: Map<String, String>): CSVConfig {
        val dataRepo: Path? = givenParams.retrieveParam("dataRepo")
        val zoneRepo: Path? = givenParams.retrieveParam("zoneRepo")
        val personCSV: Path? = givenParams.retrieveParam("personCSV")
        val householdCSV: Path? = givenParams.retrieveParam("householdCSV")
        val activityCSV: Path? = givenParams.retrieveParam("activityCSV")
        val privateCarsCSV: Path? = givenParams.retrieveParam("privateCarsCSV")
        val fixedDestinationCSV: Path? = givenParams.retrieveParam("fixedDestinationCSV")
        val attractivitiesCSV: Path? = givenParams.retrieveParam("attractivitiesCSV")
        val bikeSharingStationsCSV: Path? = givenParams.retrieveParam("bikeSharingStationsCSV")
        val zonesCSV: Path? = givenParams.retrieveParam("zonesCSV")

        if (dataRepo != null && zoneRepo != null) {
            return CSVConfig(
                dataRepo = dataRepo,
                zoneRepo = zoneRepo,
                personCSV = personCSV,
                householdCSV = householdCSV,
                activityCSV = activityCSV,
                privateCarsCSV = privateCarsCSV,
                fixedDestinationCSV = fixedDestinationCSV,
                attractivitiesCSV = attractivitiesCSV,
                bikeSharingStationsCSV = bikeSharingStationsCSV,
                zonesCSV = zonesCSV
            )
        } else {
            @Suppress("ComplexCondition")
            if (
                personCSV != null &&
                householdCSV != null &&
                activityCSV != null &&
                privateCarsCSV != null &&
                fixedDestinationCSV != null &&
                attractivitiesCSV != null &&
                bikeSharingStationsCSV != null &&
                zonesCSV != null
            ) {
                return CSVConfig(
                    personCSV = personCSV,
                    householdCSV = householdCSV,
                    activityCSV = activityCSV,
                    privateCarsCSV = privateCarsCSV,
                    fixedDestinationCSV = fixedDestinationCSV,
                    attractivitiesCSV = attractivitiesCSV,
                    bikeSharingStationsCSV = bikeSharingStationsCSV,
                    zonesCSV = zonesCSV
                )
            } else {
                error("Missing mandatory fields. Either set 'dataRepo' and 'zoneRepo' or all other fields.")
            }
        }
    }
}
