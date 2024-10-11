dependencies {
    implementation(project(":common"))
    compileOnly("dev.folia:folia-api:1.20.6-R0.1-SNAPSHOT")
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}