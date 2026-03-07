package com.refdata.api.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация Spring-бинов для маппинга объектов в {@code refdata-api}.
 */
@Configuration
public class ModelMapperConfig {

    /**
     * Создает общий бин {@link ModelMapper} для преобразования сущностей БД
     * в DTO сервисного слоя.
     *
     * @return экземпляр {@link ModelMapper}.
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
