import com.google.protobuf.gradle.*
plugins {
    id("org.jetbrains.kotlin.jvm") version "1.5.21"
    id("org.jetbrains.kotlin.kapt") version "1.5.21"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("io.micronaut.application") version "2.0.4"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.5.21"
    id("com.google.protobuf") version "0.8.15"
}

version = "0.1"
group = "br.com.zup.academy.erombi"

val kotlinVersion=project.properties.get("kotlinVersion")
repositories {
    mavenCentral()
}

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("br.com.zup.academy.erombi.*")
    }
}

dependencies {
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.grpc:micronaut-grpc-client-runtime")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("javax.annotation:javax.annotation-api")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    implementation("io.micronaut:micronaut-validation")

    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")

    kaptTest("io.micronaut:micronaut-inject-java")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter:3.8.0")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.mockito:mockito-inline:3.8.0")

//    testImplementation("org.mockito:mockito-core:3.12.4")
//    testImplementation("org.mockito:mockito-junit-jupiter:3.8.0")
//    testImplementation("org.mockito:mockito-inline:3.11.2")

    testImplementation("io.micronaut:micronaut-http-client")
//    testAnnotationProcessor("io.micronaut:micronaut-inject-java")
//    testImplementation("org.hamcrest:hamcrest")
//    testImplementation("org.mockito:mockito-core:3.11.2")
//    testImplementation("org.mockito:mockito-inline:3.11.2")
//    testImplementation("org.mockito:mockito-junit-jupiter:3.11.2")
//    testImplementation("org.testcontainers:testcontainers")
//    testImplementation("org.testcontainers:junit-jupiter")
//    testImplementation("io.micronaut.test:micronaut-test-junit5:2.3.7")
//    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")
}


application {
    mainClass.set("br.com.zup.academy.erombi.ApplicationKt")
}
java {
    sourceCompatibility = JavaVersion.toVersion("11")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }


}
sourceSets {
    main {
        java {
            srcDirs("build/generated/source/proto/main/grpc")
            srcDirs("build/generated/source/proto/main/java")
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.17.2"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.38.0"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                // Apply the "grpc" plugin whose spec is defined above, without options.
                id("grpc")
            }
        }
    }
}
