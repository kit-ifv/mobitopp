rootProject.name = "mobitopp-reengineering"

include("annotations")
project(":annotations").projectDir = file("codegen/annotations")
include("processor")
project(":processor").projectDir = file("codegen/processor")
include("custom-detekt")
project(":custom-detekt").projectDir = file("codegen/custom-detekt")
//
//val dcmPath =  "../discretechoicemodelling"
//if (File(dcmPath).exists()) {
//    println("Include local build of discrete-choice")
//    includeBuild(dcmPath)
//}
//
//
//val actiToppPAth =  "../actitopp"
//if (File(actiToppPAth).exists()) {
//    println("Include local build of actitoppNG")
//    includeBuild(actiToppPAth)
//}

//val unitsPath =  "../kotlin-units"
//if (File(unitsPath).exists()) {
//    println("Include local build of kotlin-units")
//    includeBuild(unitsPath)
//}
//
//val synLibPath = "../synthesislibrary"
//
//if (File(synLibPath).exists()) {
//    println("Include local build of synthesis-algorithms")
//    includeBuild(synLibPath)
//}
//
//val netfilePath = "../visumNetfileParser"
//
//if (File(netfilePath).exists()) {
//    println("Include local build of netfile parser")
//    includeBuild(netfilePath)
//}
//
//val synLibPath = "../synthesislibrary"
//
//if (File(synLibPath).exists()) {
//    println("Include local build of synthesis-algorithms")
//    includeBuild(synLibPath)
//}

//val netfilePath = "../visumnetfileparser"
//
//if (File(netfilePath).exists()) {
//    println("Include local build of netfile parser")
//    includeBuild(netfilePath)
//}