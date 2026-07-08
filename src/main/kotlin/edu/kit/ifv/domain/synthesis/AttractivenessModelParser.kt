package edu.kit.ifv.domain.synthesis

import edu.kit.ifv.domain.shared.behavior.AttractivenessFromCsv
import edu.kit.ifv.domain.shared.behavior.AttractivenessModel
import edu.kit.ifv.domain.shared.behavior.ChoiceModelPurposes
import java.nio.file.Path

class AttractivenessModelParser {

    lateinit var path: Path // = attractivenessModelPath

    lateinit var purposes: ChoiceModelPurposes
    fun build(): AttractivenessModel = AttractivenessFromCsv(
        path = path,
        activityTypes = purposes.allActivityTypes,
    )

    companion object {
        fun parse(path: Path, purposes: ChoiceModelPurposes): AttractivenessModel {
            val parser = AttractivenessModelParser().apply {
                this.path = path
                this.purposes = purposes
            }

            return parser.build()
        }
    }
}
