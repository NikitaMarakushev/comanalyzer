@Grab('org.eclipse.jgit:org.eclipse.jgit:5.13.0.202109080827-r')
@Grab('org.apache.maven:maven-model:3.8.4')
import org.eclipse.jgit.api.Git
import org.apache.maven.model.io.xpp3.MavenXpp3Reader
import groovy.xml.XmlSlurper

def repoUrl = 'https://github.com/NikitaMarakushev/comanalyzer.git'
def tempDir = File.createTempDir('comanalyzer-build-', '')
def outputJar = new File('comanalyzer.jar')

try {
    println "Cloning git repo..."
    Git.cloneRepository()
            .setURI(repoUrl)
            .setDirectory(tempDir)
            .call()

    println "Analyze project..."
    def pomFile = new File(tempDir, 'pom.xml')
    if (!pomFile.exists()) {
        throw new RuntimeException("There is no pom.xml filein repo")
    }

    def pom = new MavenXpp3Reader().read(new FileReader(pomFile))
    def mainClass = findMainClass(pomFile)

    println "Run maven build..."
    def mvnCmd = "mvn clean package -DskipTests -f ${pomFile.absolutePath}"
    def proc = mvnCmd.execute(null, tempDir)
    proc.waitForProcessOutput(System.out, System.err)

    if (proc.exitValue() != 0) {
        throw new RuntimeException("Maven build failed with error")
    }

    def targetDir = new File(tempDir, 'target')
    def builtJar = targetDir.listFiles().find { it.name.endsWith('.jar') && !it.name.endsWith('-sources.jar') }

    if (!builtJar) {
        throw new RuntimeException("There is no built JAR")
    }

    println "Copy results..."
    builtJar.renameTo(outputJar)

    println "Done! Executable JAR : ${outputJar.absolutePath}"
    if (mainClass) {
        println "The main class is : $mainClass"
        println "Run: java -jar ${outputJar.name}"
    }
} finally {
    tempDir.deleteDir()
}

def findMainClass(File pomFile) {
    def pom = new XmlSlurper().parse(pomFile)

    def shadePlugin = pom.build.plugins.plugin.find {
        it.artifactId.text() == 'maven-shade-plugin'
    }

    if (shadePlugin) {
        return shadePlugin.configuration?.archive?.manifest?.mainClass?.text()
    }

    def springBootPlugin = pom.build.plugins.plugin.find {
        it.artifactId.text() == 'spring-boot-maven-plugin'
    }

    if (springBootPlugin) {
        return springBootPlugin.configuration?.mainClass?.text() ?:
                pom.parent.artifactId.text() + '.Application'
    }

    return null
}