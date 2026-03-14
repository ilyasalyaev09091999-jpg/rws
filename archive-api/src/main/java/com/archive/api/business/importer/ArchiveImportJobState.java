package com.archive.api.business.importer;

/**
 * Состояния асинхронной задачи импорта.
 */
public enum ArchiveImportJobState {
    /** Задача создана, но еще не запущена. */
    PENDING,
    /** Задача выполняется. */
    RUNNING,
    /** Задача завершилась успешно. */
    DONE,
    /** Задача завершилась с ошибкой. */
    FAILED
}