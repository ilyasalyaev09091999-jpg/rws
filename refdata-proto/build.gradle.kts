import com.google.protobuf.gradle.id

plugins {
    java
    `java-library`
    id("com.google.protobuf") version "0.9.4"
}

repositories {
    mavenCentral()
}

dependencies {
    api("io.grpc:grpc-protobuf:1.60.0")
    api("io.grpc:grpc-stub:1.60.0")
    api("com.google.protobuf:protobuf-java:3.25.0")
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
        all().forEach {
            it.plugins {
                id("grpc")
            }
        }
    }
}
