package application.config

import application.config.subconfigs.CSVConfig
import domain.shared.config.Yaml
import kotlin.io.path.Path
import kotlin.test.Test

class CSVConfigParsingTest {
    @Test
    fun basicParsingTest() {
        val path = "src/test/kotlin/application/config/YamlTest.kt"
        val parsed: CSVConfig = Yaml.readYaml(path)
        assert(parsed.personCSV == Path("testD/person.csv"))
    }
}