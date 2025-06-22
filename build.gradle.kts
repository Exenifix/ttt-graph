plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "ru.vsu.cs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

javafx {
    version = "24.0.1"
    modules = mutableListOf("javafx.controls", "javafx.fxml")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass = "Main"
}
