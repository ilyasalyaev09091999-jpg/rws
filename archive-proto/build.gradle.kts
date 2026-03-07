import com.google.protobuf.gradle.id

plugins {
    java
    `java-library`
    id("com.google.protobuf") version "0.9.4"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    api("io.grpc:grpc-protobuf:1.60.0")
    api("io.grpc:grpc-stub:1.60.0")
    api("com.google.protobuf:protobuf-java:4.28.2")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.0"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.60.0"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                id("grpc") {
                    option("annotation_package=jakarta.annotation")
                }
            }
        }
    }
}
