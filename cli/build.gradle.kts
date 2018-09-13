import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.gradle.api.plugins.ExtensionAware
import org.junit.platform.gradle.plugin.FiltersExtension
import org.junit.platform.gradle.plugin.EnginesExtension
import org.junit.platform.gradle.plugin.JUnitPlatformExtension

plugins {
    `application`
    id("org.jetbrains.kotlin.jvm")
    id("org.junit.platform.gradle.plugin")
    id("de.fuerstenau.buildconfig") version "1.1.8"
    jacoco
}

application {
    mainClassName = "com.malinskiy.marathon.cli.ApplicationViewKt"
    applicationName = "marathon"
}

distributions {
    getByName("main") {
        baseName = "marathon"
    }
}

kotlin.experimental.coroutines = Coroutines.ENABLE

dependencies {
    implementation(project(":core"))
    implementation(project(":vendor-android"))
    implementation(Libraries.kotlinStdLib)
    implementation(Libraries.kotlinCoroutines)
    implementation(Libraries.kotlinLogging)
    implementation(Libraries.slf4j)
    implementation(Libraries.argParser)
    implementation(Libraries.jacksonDatabind)
    implementation(Libraries.jacksonKotlin)
    implementation(Libraries.jacksonYaml)
    implementation(Libraries.jacksonJSR310)
    testCompile(TestLibraries.kluent)
    testCompile(TestLibraries.spekAPI)
    testRuntime(TestLibraries.spekJUnitPlatformEngine)
}

Deployment.initialize(project)

val compileKotlin by tasks.getting(KotlinCompile::class) {
    kotlinOptions.jvmTarget = "1.8"
}
val compileTestKotlin by tasks.getting(KotlinCompile::class) {
    kotlinOptions.jvmTarget = "1.8"
}

buildConfig {
    appName = project.name
    version = Versions.marathon
}

junitPlatform {
    filters {
        engines {
            include("spek")
        }
    }
}

// extension for configuration
fun JUnitPlatformExtension.filters(setup: FiltersExtension.() -> Unit) {
    when (this) {
        is ExtensionAware -> extensions.getByType(FiltersExtension::class.java).setup()
        else -> throw IllegalArgumentException("${this::class} must be an instance of ExtensionAware")
    }
}

fun FiltersExtension.engines(setup: EnginesExtension.() -> Unit) {
    when (this) {
        is ExtensionAware -> extensions.getByType(EnginesExtension::class.java).setup()
        else -> throw IllegalArgumentException("${this::class} must be an instance of ExtensionAware")
    }
}

val jacocoTestResultTaskName = "jacocoJunit5TestReport"
val junitPlatformTest: JavaExec by tasks
jacoco {
    toolVersion = "0.8.2"
    applyTo(junitPlatformTest)
}

task<JacocoReport>(jacocoTestResultTaskName) {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Generates code coverage report for the ${junitPlatformTest.name} task."

    executionData(junitPlatformTest)
    dependsOn(junitPlatformTest)

    //   sourceSets(sourceSets["main"])
    sourceDirectories = files("src/main/kotlin")
    classDirectories = files("build/kotlin/main")

    reports {
        html.isEnabled = true
        xml.isEnabled = true
        csv.isEnabled = true
        html.destination = File("$buildDir/reports/")

    }
}