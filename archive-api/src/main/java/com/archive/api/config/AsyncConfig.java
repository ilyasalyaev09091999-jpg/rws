package com.archive.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Конфигурация асинхронного исполнения задач для импорта архива.
 */
@Configuration
public class AsyncConfig {

    /**
     * Создает пул потоков для асинхронного импорта XLSX.
     *
     * @return task executor для фоновых задач импорта
     */
    @Bean(name = "archiveImportTaskExecutor")
    public TaskExecutor archiveImportTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("archive-import-");
        executor.initialize();
        return executor;
    }
}