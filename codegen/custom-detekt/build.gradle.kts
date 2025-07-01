plugins {
    alias(libs.plugins.kotlin.jvm) // "2.0.10" //
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.detekt.api)
    implementation(libs.detekt.rules)
}
