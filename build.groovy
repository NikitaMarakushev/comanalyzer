#!/usr/bin/env groovy

def outputJar = new File('comanalyzer.jar')

println "Run build project comanalyzer..."

try {
    def pomFile = new File('pom.xml')
    if (!pomFile.exists()) {
        throw new RuntimeException("Error: pom.xml not found")
    }

    def mainClass = getMainClassFromPom(pomFile)
    if (!mainClass) {
        throw new RuntimeException("There is no main class from pom.xml")
    }

    println "Main class found: $mainClass"

    println "Run maven build..."
    def mvnCmd = "mvn clean compile assembly:single -DskipTests"
    def mvnProc = mvnCmd.execute()
    mvnProc.waitForProcessOutput(System.out, System.err)

    if (mvnProc.exitValue() != 0) {
        throw new RuntimeException("Maven build error")
    }

    def targetDir = new File('target')
    def builtJar = targetDir.listFiles().find {
        it.name.endsWith('-jar-with-dependencies.jar') ||
                (it.name.startsWith('comanalyzer') && it.name.endsWith('.jar') && !it.name.endsWith('-sources.jar'))
    }

    if (!builtJar) {
        throw new RuntimeException("There is no built JAR-file found")
    }

    if (outputJar.exists()) {
        outputJar.delete()
    }
    builtJar.renameTo(outputJar)

    println "\nBuild success!"
    println "Executable JAR: ${outputJar.absolutePath}"
    println "Run using: java -jar ${outputJar.name}"

} catch (Exception e) {
    println "\nBuild error: ${e.message}"
    System.exit(1)
}

static def getMainClassFromPom(File pomFile) {
    def pom = new XmlSlurper().parse(pomFile)

    def mainClass = pom.properties?.getProperty('exec.mainClass')?.text()
    if (mainClass) return mainClass

    def jarPlugin = pom.build?.plugins?.plugin?.find {
        it.artifactId.text() == 'maven-jar-plugin'
    }
    mainClass = jarPlugin?.configuration?.archive?.manifest?.mainClass?.text()
    if (mainClass) return mainClass

    return "${pom.groupId.text()}.${pom.artifactId.text()}.Main"
}