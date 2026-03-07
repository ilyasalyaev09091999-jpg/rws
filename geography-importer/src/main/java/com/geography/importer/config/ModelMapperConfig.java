package com.geography.importer.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация бинов mapping-слоя для {@code geography-importer}.
 */
@Configuration
public class ModelMapperConfig {

    /**
     * Создаёт singleton {@link ModelMapper}, используемый для преобразований DTO/Entity.
     *
     * @return настроенный экземпляр {@link ModelMapper}.
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
