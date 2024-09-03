plugins {
    kotlin("kapt") version "1.9.21"
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":common"))
    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    kapt("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    compileOnly("net.luckperms:api:5.4")
}

val targetJavaVersion = 17
kotlin {
    jvmToolchain(targetJavaVersion)
}
