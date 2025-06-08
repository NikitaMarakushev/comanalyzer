@Grab('org.eclipse.jgit:org.eclipse.jgit:5.13.0.202109080827-r')
@Grab('org.apache.maven:maven-model:3.8.4')
import org.eclipse.jgit.api.Git
import org.apache.maven.model.io.xpp3.MavenXpp3Reader
import groovy.xml.XmlSlurper

def repoUrl = 'https://github.com/NikitaMarakushev/comanalyzer.git'
def tempDir = File.createTempDir('comanalyzer-build-', '')
def outputJar = new File('comanalyzer.jar')


