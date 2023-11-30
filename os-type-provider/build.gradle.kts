import java.util.*

plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
}

group = "com.appdav.kotlin-utilities"
version = "1.0"

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


tasks.test {
    useJUnitPlatform()
}
