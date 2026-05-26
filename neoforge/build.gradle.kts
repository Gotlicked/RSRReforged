plugins {
    id("net.neoforged.moddev")
    id("java-library")
    id("maven-publish")
}

base {
    archivesName = "${property("mod_id")}-${project.name}-${property("minecraft_version")}-${property("mod_version")}"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(property("java_version").toString())
    }
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
    maven {
        url = uri ("https://mvnrepository.com/")
    }
    exclusiveContent {
        forRepository {
            maven {
                url = uri ("https://maven.creeperhost.net")
            }
        }
        filter {
            includeGroupAndSubgroups("com.refinedmods")
        }
    }
    exclusiveContent {
        forRepository {
            maven {
                url = uri ("https://maven.blamejared.com")
            }
        }
        filter {
            includeGroupAndSubgroups("mezz.jei")
        }
    }
}

val commonJava: Configuration by configurations.creating
val commonResources: Configuration by configurations.creating

sourceSets.main {
    resources.srcDir("src/generated/resources")
}

neoForge {
    version = "${property("neoforge_version")}"

    val at = project(":common").file("src/main/resources/META-INF/accesstransformer.cfg")
    if (at.exists()) {
        accessTransformers.from(at.absolutePath)
    }

    runs {
        create("client") {
            client()
            ideName = "NeoForge Client"
            gameDirectory = mkdir(file("runs/client"))
        }
        create("data") {
            clientData()
            ideName = "NeoForge Data"
            gameDirectory = mkdir(file("runs/data"))
            programArguments.addAll(
                "--mod", "${property("mod_id")}", "--all", "--output",
                file("src/generated/resources/").absolutePath,
                "--existing", file("src/main/resources/assets").absolutePath,
                "--existing", file("src/main/resources/data").absolutePath,
            )
        }
        create("server") {
            server()
            ideName = "NeoForge Server"
            gameDirectory = mkdir(file("runs/server"))
        }
    }

    mods {
        create("${property("mod_id")}") {
            sourceSet(sourceSets.main.get())
        }
    }
}

dependencies {
    implementation("org.apiguardian:apiguardian-api:1.1.2")
    implementation("com.refinedmods.refinedstorage:refinedstorage-neoforge:${property("rs_version")}")
    runtimeOnly("mezz.jei:jei-${property("minecraft_version")}-neoforge:${property("jei_version")}")
    api("mezz.jei:jei-${property("minecraft_version")}-neoforge-api:${property("jei_version")}")
    add("commonJava",      project(path = ":common", configuration = "commonJava"))
    add("commonResources", project(path = ":common", configuration = "commonResources"))
    compileOnly(project(":common"))
}

tasks.named<JavaCompile>("compileJava") {
    dependsOn(commonJava)
    source(commonJava)
}

tasks.named<ProcessResources>("processResources") {
    dependsOn(commonResources)
    from(commonResources)
    filesMatching(listOf(
        "META-INF/neoforge.mods.toml",
        "pack.mcmeta",
        "*.mixins.json",
    )) {
        expand(project.properties)
    }
    from("src/main/templates")
}

tasks.named<Javadoc>("javadoc") {
    dependsOn(commonJava)
    source(commonJava)
}

tasks.named<Jar>("sourcesJar") {
    dependsOn(commonJava, commonResources)
    from(commonJava)
    from(commonResources)
}

tasks.named<Jar>("jar") {
    dependsOn(commonJava, commonResources)
    from(commonJava)
    from(commonResources)
}

tasks.withType<Copy>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<Jar>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            artifactId = base.archivesName.get()
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri(
                System.getenv("local_maven_url")
                    ?: layout.buildDirectory.dir("repo").get().asFile.toURI().toString()
            )
        }
    }
}
