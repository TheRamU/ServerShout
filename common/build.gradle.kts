dependencies {
    compileOnly("com.zaxxer:HikariCP:4.0.3")
    compileOnly("com.mysql:mysql-connector-j:9.0.0")
    compileOnly("org.yaml:snakeyaml:2.0")
    compileOnly("net.luckperms:api:5.4")
}

val templateSource = file("src/main/templates")
val templateDest = layout.buildDirectory.dir("generated/sources/templates")
val generateTemplates = tasks.register<Copy>("generateTemplates") {
    val props = mapOf("version" to project.parent?.version)
    inputs.properties(props)

    from(templateSource)
    into(templateDest)
    expand(props)
}

sourceSets.main.configure {
    java.srcDir(generateTemplates.map { it.outputs })
}
