
val jacocoTestResultTaskName = "jacocoJunit5TestReport"
subprojects {
    val junitPlatformTest: JavaExec by tasks // You don't need this if using Junt 4

    jacoco {
        toolVersion = "0.8.0"
        applyTo(junitPlatformTest) // You don't need this if using Junt 4
    }

    val sourceSets = java.sourceSets

    task<JacocoReport>(jacocoTestResultTaskName) { // For Junit4 I think this becomes `tasks.getting(JacocoReport::class) { ...`

        executionData(junitPlatformTest) // You don't need this if using Junt 4
        dependsOn(junitPlatformTest)

        sourceSets(sourceSets["main"])
        sourceDirectories = files(sourceSets["main"].allSource.srcDirs)
        classDirectories = files(sourceSets["main"].output)
        reports {
            html.isEnabled = true
            xml.isEnabled = true
            csv.isEnabled = false
        }
    }
}


val jacocoTestResultTaskName = "jacocoJunit5TestReport"
val junitPlatformTest: JavaExec by tasks




//jacoco {

//}


//JacocoPluginExtensio
//tasks.withType<Test> {
//
//    jacocoTestResultTaskName {
//
//    }
//}

//jacoco {
//    toolVersion = "0.7.9"
//    applyTo(junitPlatformTest)
//}

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
        html.destination = File("build/reports/x/")

    }
}