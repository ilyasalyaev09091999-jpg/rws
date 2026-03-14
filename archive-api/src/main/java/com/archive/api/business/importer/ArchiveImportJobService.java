package com.archive.api.business.importer;

import com.archive.api.business.importer.support.InMemoryMultipartFile;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Сервис управления асинхронными задачами импорта архива.
 */
@Service
public class ArchiveImportJobService {

    private final ArchiveXlsxImportService archiveXlsxImportService;
    private final TaskExecutor taskExecutor;

    private final Map<String, JobState> jobs = new ConcurrentHashMap<>();

    public ArchiveImportJobService(ArchiveXlsxImportService archiveXlsxImportService,
                                   @Qualifier("archiveImportTaskExecutor") TaskExecutor taskExecutor) {
        this.archiveXlsxImportService = archiveXlsxImportService;
        this.taskExecutor = taskExecutor;
    }

    /**
     * Запускает асинхронный импорт и возвращает начальный статус.
     *
     * @param fileName имя файла
     * @param content байты файла
     * @return статус созданной задачи
     */
    public ArchiveImportJobStatus start(String fileName, byte[] content) {
        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("File is empty");
        }

        String normalizedFileName = (fileName == null || fileName.isBlank()) ? "unknown.xlsx" : fileName;
        String jobId = UUID.randomUUID().toString();

        JobState state = new JobState(jobId, normalizedFileName);
        jobs.put(jobId, state);

        taskExecutor.execute(() -> runJob(state, content));

        return state.snapshot();
    }

    /**
     * Возвращает актуальный статус задачи по {@code jobId}.
     *
     * @param jobId идентификатор задачи
     * @return текущий статус задачи
     */
    public ArchiveImportJobStatus getStatus(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            throw new IllegalArgumentException("jobId is empty");
        }

        JobState state = jobs.get(jobId);
        if (state == null) {
            throw new IllegalArgumentException("Import job not found: " + jobId);
        }

        return state.snapshot();
    }

    private void runJob(JobState state, byte[] content) {
        state.status = ArchiveImportJobState.RUNNING;

        try {
            ArchiveImportResult result = archiveXlsxImportService.importFile(
                    new InMemoryMultipartFile(state.fileName, content)
            );

            state.totalRows = result.totalRows();
            state.importedRows = result.importedRows();
            state.skippedRows = result.skippedRows();
            state.errorRows = result.errorRows();
            state.status = ArchiveImportJobState.DONE;
            state.errorMessage = null;
        } catch (Exception ex) {
            state.status = ArchiveImportJobState.FAILED;
            state.errorMessage = ex.getMessage();
        }
    }

    private static final class JobState {
        private final String jobId;
        private final String fileName;
        private volatile ArchiveImportJobState status;
        private volatile Integer totalRows;
        private volatile Integer importedRows;
        private volatile Integer skippedRows;
        private volatile Integer errorRows;
        private volatile String errorMessage;

        private JobState(String jobId, String fileName) {
            this.jobId = jobId;
            this.fileName = fileName;
            this.status = ArchiveImportJobState.PENDING;
        }

        private ArchiveImportJobStatus snapshot() {
            return new ArchiveImportJobStatus(
                    jobId,
                    status,
                    fileName,
                    totalRows,
                    importedRows,
                    skippedRows,
                    errorRows,
                    errorMessage
            );
        }
    }
}