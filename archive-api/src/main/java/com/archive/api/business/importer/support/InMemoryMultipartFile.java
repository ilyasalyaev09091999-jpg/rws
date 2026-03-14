package com.archive.api.business.importer.support;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * In-memory реализация {@link MultipartFile} для передачи байтов в сервис импорта.
 */
public class InMemoryMultipartFile implements MultipartFile {

    private final String filename;
    private final byte[] content;

    /**
     * Создает MultipartFile из массива байтов.
     *
     * @param filename имя файла
     * @param content байты файла
     */
    public InMemoryMultipartFile(String filename, byte[] content) {
        this.filename = filename;
        this.content = content;
    }

    /**
     * @return фиксированное имя поля multipart
     */
    @Override
    public String getName() {
        return "file";
    }

    /**
     * @return исходное имя файла
     */
    @Override
    public String getOriginalFilename() {
        return filename;
    }

    /**
     * @return MIME-тип XLSX
     */
    @Override
    public String getContentType() {
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }

    /**
     * @return {@code true}, если контент отсутствует
     */
    @Override
    public boolean isEmpty() {
        return content == null || content.length == 0;
    }

    /**
     * @return размер контента в байтах
     */
    @Override
    public long getSize() {
        return content == null ? 0 : content.length;
    }

    /**
     * @return массив байтов файла
     */
    @Override
    public byte[] getBytes() {
        return content;
    }

    /**
     * @return входной поток по содержимому файла
     */
    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(content);
    }

    /**
     * Записывает содержимое файла в указанный путь.
     *
     * @param dest файл назначения
     * @throws IOException при ошибке записи
     */
    @Override
    public void transferTo(java.io.File dest) throws IOException {
        java.nio.file.Files.write(dest.toPath(), content);
    }
}