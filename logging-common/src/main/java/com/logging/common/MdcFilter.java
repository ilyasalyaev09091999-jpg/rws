package com.logging.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.UUID;

@Component
public class MdcFilter extends OncePerRequestFilter {

    public static final String REQUEST_ID = "requestId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Генерируем уникальный requestId для каждого запроса
        String requestId = UUID.randomUUID().toString();

        // Кладем в MDC
        MDC.put(REQUEST_ID, requestId);

        try {
            // Можно передать requestId в response header (опционально)
            response.addHeader("X-Request-Id", requestId);

            // Продолжаем фильтрацию запроса
            filterChain.doFilter(request, response);
        } finally {
            // Обязательно очищаем MDC после запроса
            MDC.remove(REQUEST_ID);
        }
    }
}
