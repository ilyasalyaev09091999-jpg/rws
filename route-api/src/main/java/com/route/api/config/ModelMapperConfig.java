package com.route.api.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring-конфигурация ModelMapperConfig для инфраструктурных зависимостей.
 */
@Configuration
public class ModelMapperConfig {

    /**
     * Создает и возвращает bean ModelMapper.
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
