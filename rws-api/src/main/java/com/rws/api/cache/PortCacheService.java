package com.rws.api.cache;

import com.refdata.grpc.Empty;
import com.refdata.grpc.PortForRws;
import com.refdata.grpc.PortServiceGrpc;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Кэш справочной информации о портах для REST-слоя {@code rws-api}.
 * <p>
 * Сервис получает данные по gRPC из {@code refdata-api} через
 * {@link PortServiceGrpc.PortServiceBlockingStub} (клиент {@code refdata}),
 * хранит их в оперативной памяти и предоставляет контроллерам
 * быстрый доступ к неизменяемому снимку данных.
 * </p>
 * <p>
 * Жизненный цикл кэша:
 * </p>
 * <ul>
 *   <li>после старта приложения выполняется первичная загрузка в {@link #init()};</li>
 *   <li>далее кэш обновляется по расписанию в {@link #refresh()};</li>
 *   <li>при неуспешном обновлении остаётся последняя валидная версия.</li>
 * </ul>
 * <p>
 * Потокобезопасность:
 * </p>
 * <ul>
 *   <li>{@link #ports} объявлен как {@code volatile}, что гарантирует
 *   видимость новой ссылки для всех потоков после обновления;</li>
 *   <li>запись в кэш выполняется только внутри синхронизированного метода
 *   {@link #refresh()}, исключая гонки при обновлении.</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PortCacheService {

    @GrpcClient("refdata")
    private PortServiceGrpc.PortServiceBlockingStub stub;

    /**
     * Текущий in-memory снимок портов для {@code rws-api}.
     * <p>
     * Изначально содержит пустой список и полностью заменяется
     * при каждом успешном вызове {@link #refresh()}. Для чтения используется
     * Lombok-геттер {@code getPorts()}.
     * </p>
     */
    @Getter
    private volatile List<PortForRws> ports = List.of();

    /**
     * Выполняет первичную загрузку данных о портах после инициализации бина.
     * <p>
     * Использует {@link #refresh()}, чтобы единая логика загрузки применялась
     * как при старте, так и при плановых обновлениях.
     * </p>
     */
    @PostConstruct
    public void init() {
        refresh();
    }

    /**
     * Периодически обновляет кэш портов из {@code refdata-api}.
     * <p>
     * Запускается с фиксированной задержкой 10 минут между двумя выполнениями.
     * Внутри делает блокирующий gRPC-вызов
     * {@code getAllPortsForRws(Empty)} и при успехе атомарно подменяет
     * ссылку на список в поле {@link #ports}.
     * </p>
     * <p>
     * Если обновление завершилось ошибкой, сервис логирует проблему и
     * продолжает отдавать предыдущий успешно загруженный снимок данных.
     * </p>
     */
    @Scheduled(fixedDelay = 10 * 60 * 1000) // каждые 10 минут
    public synchronized void refresh() {
        try {
            ports = stub.getAllPortsForRws(Empty.newBuilder().build()).getPortsList();
            log.info("Ports cache refreshed: ports = {}", ports.size());
        } catch (Exception e) {
            log.info("Failed to refresh ports cache: {}", e.getMessage());
        }
    }
}
