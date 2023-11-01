plugins {
    java
    application
}

group = "com.nonghyupit"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.apache.tomcat:tomcat-jdbc:9.0.80")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes ["Main-Class"] = "Encryptor"
    }
}