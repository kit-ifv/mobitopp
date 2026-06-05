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
        work = purposes.work,
        privateVisit = purposes.privateVisit,
        activityTypes = purposes.allActivityTypes,
    )

    companion object {
        fun parse(path: Path): AttractivenessModel {
            val parser = AttractivenessModelParser().apply { this.path = path }

            return parser.build()
        }
    }
}
