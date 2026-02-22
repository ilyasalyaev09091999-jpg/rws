package com.rws.api.config;

import com.refdata.grpc.RefDataServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

    @Value("${grpc.refdata.address}")
    private String grpcAddress; // например, "localhost:9090"

    @Bean
    public RefDataServiceGrpc.RefDataServiceBlockingStub refdataStub() {
        ManagedChannel channel = ManagedChannelBuilder
                .forTarget(grpcAddress)
                .usePlaintext() // если TLS не нужен
                .build();

        return RefDataServiceGrpc.newBlockingStub(channel);
    }
}
