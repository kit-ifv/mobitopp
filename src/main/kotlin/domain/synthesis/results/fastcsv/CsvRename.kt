package domain.synthesis.results.fastcsv
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class CsvRename(val name: String)
