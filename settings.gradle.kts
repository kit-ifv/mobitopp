rootProject.name = "mobitopp-reengineering"

include("annotations")
project(":annotations").projectDir = file("codegen/annotations")
include("processor")
project(":processor").projectDir = file("codegen/processor")