import java.util.*

plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
}

group = "com.appdav.kotlin-utilities"
version = "1.1"

fun repoProperty(name: String) : String{
    return with(Properties()){
        load(file("../repo.properties").inputStream())
        get(name) as String
    }
}

publishing{
    publications.create("lib", MavenPublication::class.java){
        repositories{
            maven(repoProperty("url")){
                credentials{
                    username = repoProperty("username")
                    password = repoProperty("password")
                }
            }
            artifact("$buildDir\\libs\\${project.name}-${project.version}.jar")
            artifact("$buildDir\\libs\\${project.name}-${project.version}-sources.jar"){
                classifier = "sources"
            }
        }
    }
}


repositories {
    mavenCentral()
}

dependencies{
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.8.10")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
}

tasks.test {
    useJUnitPlatform()
}
