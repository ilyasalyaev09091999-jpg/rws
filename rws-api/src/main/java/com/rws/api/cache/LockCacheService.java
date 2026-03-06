package com.rws.api.cache;

import com.refdata.grpc.Empty;
import com.refdata.grpc.LockForRws;
import com.refdata.grpc.LockServiceGrpc;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Кэш справочной информации о шлюзах для REST-слоя {@code rws-api}.
 * <p>
 * Сервис получает данные по gRPC из {@code refdata-api} через
 * {@link LockServiceGrpc.LockServiceBlockingStub} (клиент {@code refdata}),
 * сохраняет их в памяти и предоставляет быстрый доступ без отдельного
 * сетевого вызова на каждый HTTP-запрос.
 * </p>
 * <p>
 * Жизненный цикл кэша:
 * </p>
 * <ul>
 *   <li>после старта приложения выполняется первичная загрузка в {@link #init()};</li>
 *   <li>далее кэш периодически обновляется в {@link #refresh()};</li>
 *   <li>при ошибке gRPC-вызова предыдущая успешная версия данных сохраняется.</li>
 * </ul>
 * <p>
 * Потокобезопасность:
 * </p>
 * <ul>
 *   <li>поле {@link #locks} объявлено как {@code volatile}, поэтому читатели
 *   всегда видят согласованную ссылку на актуальный снимок списка;</li>
 *   <li>обновление выполняется в {@code synchronized}-методе
 *   {@link #refresh()}, чтобы исключить конкурентные записи.</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LockCacheService {

    @GrpcClient("refdata")
    private LockServiceGrpc.LockServiceBlockingStub stub;

    /**
     * Текущий in-memory снимок шлюзов для {@code rws-api}.
     * <p>
     * Инициализируется пустым списком, затем полностью заменяется
     * при успешном обновлении в {@link #refresh()}. Возвращается наружу через
     * Lombok-геттер {@code getLocks()}.
     * </p>
     */
    @Getter
    private volatile List<LockForRws> locks = List.of();

    /**
     * Выполняет первичное заполнение кэша после создания Spring-бина.
     * <p>
     * Делегирует работу в {@link #refresh()} для унификации логики начальной
     * загрузки и планового обновления.
     * </p>
     */
    @PostConstruct
    public void init() {
        refresh();
    }

    /**
     * Периодически обновляет кэш шлюзов из {@code refdata-api}.
     * <p>
     * Запускается по расписанию с фиксированной задержкой 10 минут между
     * завершением предыдущего и началом следующего выполнения.
     * Метод выполняет блокирующий gRPC-вызов
     * {@code getAllLocksForRws(Empty)} и при успехе атомарно подменяет
     * ссылку на список в поле {@link #locks}.
     * </p>
     * <p>
     * В случае ошибки логирует причину и оставляет прежние данные в кэше,
     * чтобы избежать деградации REST-эндпоинтов при временной недоступности
     * {@code refdata-api}.
     * </p>
     */
    @Scheduled(fixedDelay = 10 * 60 * 1000) // каждые 10 минут
    public synchronized void refresh() {
        try {
            locks = stub.getAllLocksForRws(Empty.newBuilder().build()).getLocksList();
            log.info("Locks cache refreshed: locks = {}", locks.size());
        } catch (Exception e) {
            log.info("Failed to refresh locks cache: {}", e.getMessage());
        }
    }
}
